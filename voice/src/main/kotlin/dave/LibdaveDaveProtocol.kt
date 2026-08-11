package dev.kord.voice.dave

import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import moe.kyokobot.libdave.Codec
import moe.kyokobot.libdave.Decryptor
import moe.kyokobot.libdave.Encryptor
import moe.kyokobot.libdave.KeyRatchet
import moe.kyokobot.libdave.MediaType
import moe.kyokobot.libdave.NativeDaveFactory
import moe.kyokobot.libdave.Session
import moe.kyokobot.libdave.callbacks.MLSFailureCallback

/**
 * Real DAVE protocol implementation using libdave-jvm JNI bindings.
 *
 * All MLS session operations are protected by a [ReentrantReadWriteLock] for thread safety.
 * Write-lock guards state mutations (initialize, reset, close, add/remove user, etc.).
 * Read-lock guards the hot-path encrypt/decrypt methods, allowing concurrent frame processing
 * without contention while still being safe against concurrent state mutations.
 * Manages a single [Encryptor] for outbound audio and per-user [Decryptor]s for inbound audio.
 */
public class LibdaveDaveProtocol internal constructor(
    private val factory: NativeDaveFactory,
) : DaveProtocol {

    override val maxProtocolVersion: Int = factory.maxSupportedProtocolVersion()

    override var isActive: Boolean = false
        private set

    override var currentProtocolVersion: Int = 0
        private set

    private val lock = ReentrantReadWriteLock()

    private var session: Session? = null
    private var encryptor: Encryptor? = null
    private val decryptors = ConcurrentHashMap<Long, Decryptor>()
    private val recognizedUserIds: MutableSet<Long> = mutableSetOf()
    private val activeE2eeUserIds: MutableSet<Long> = mutableSetOf()
    private var channelId: String = ""
    private var authSessionId: String = ""
    private var selfUserId: Long = 0L
    private var hasJoinedGroup: Boolean = false
    @Volatile
    private var mlsFailureDetected: Boolean = false

    private data class PendingTransition(
        val protocolVersion: Int,
        val selfKeyRatchet: KeyRatchet?,
        val userKeyRatchets: Map<Long, KeyRatchet>,
    )

    private val pendingTransitions: MutableMap<Int, PendingTransition> = mutableMapOf()

    private val mlsFailureCallback = MLSFailureCallback { source, reason ->
        logger.error { "MLS failure from $source: $reason" }
        mlsFailureDetected = true
    }

    override fun initialize(version: Int, channelId: String, selfUserId: Long, authSessionId: String): Unit =
        lock.write {
            this.channelId = channelId
            this.authSessionId = authSessionId
            logger.info { "Initializing DAVE session: version=$version, channel=$channelId, user=$selfUserId" }
            this.selfUserId = selfUserId
            currentProtocolVersion = version

            // Clean up previous session if any
            session?.close()

            // Clear stale state from previous session
            recognizedUserIds.clear()
            activeE2eeUserIds.clear()
            decryptors.values.forEach { it.close() }
            decryptors.clear()
            pendingTransitions.clear()
            hasJoinedGroup = false

            val newSession = factory.createSession("", authSessionId, mlsFailureCallback)
            newSession.init(version, channelId.toLong(), selfUserId.toString())
            session = newSession

            // Always recreate the encryptor to ensure clean internal state
            // (frame counter, codec mappings, key ratchet) on each connection.
            // Reusing a stale encryptor after reconnection causes nonce/key
            // mismatch with peers, resulting in permanent silence.
            encryptor?.close()
            encryptor = factory.createEncryptor()

            isActive = version > 0

            encryptor?.setPassthroughMode(!isActive)
        }

    override fun reinitialize() {
        data class ReinitializeSnapshot(
            val channelId: String,
            val protocolVersion: Int,
            val selfUserId: Long,
            val authSessionId: String,
        )

        val snapshot = lock.read {
            ReinitializeSnapshot(
                channelId = this.channelId,
                protocolVersion = this.currentProtocolVersion,
                selfUserId = this.selfUserId,
                authSessionId = this.authSessionId,
            )
        }

        if (snapshot.channelId.isEmpty()) {
            logger.warn { "Cannot reinitialize DAVE session: channelId is not stored" }
            return
        }

        logger.info {
            "Reinitializing DAVE session: version=${snapshot.protocolVersion}, " +
                "channel=${snapshot.channelId}, user=${snapshot.selfUserId}"
        }
        initialize(snapshot.protocolVersion, snapshot.channelId, snapshot.selfUserId, snapshot.authSessionId)
    }

    override fun setExternalSender(externalSender: ByteArray): Unit = lock.write {
        logger.debug { "Setting external sender (${externalSender.size} bytes)" }
        session?.setExternalSender(externalSender)
    }

    override fun processProposals(proposals: ByteArray, recognizedUserIds: Set<Long>): ByteArray? = lock.write {
        val s = session ?: run {
            logger.warn { "processProposals called without active session" }
            return null
        }
        if (!hasJoinedGroup) {
            logger.debug { "Skipping processProposals: not yet joined group" }
            return null
        }
        try {
            val allRecognized = buildRecognizedUserIds(recognizedUserIds)
            mlsFailureDetected = false
            val result = s.processProposals(proposals, allRecognized)
            if (mlsFailureDetected) {
                logger.warn { "MLS failure detected during processProposals, treating as failure" }
                return null
            }
            result
        } catch (e: Exception) {
            logger.error(e) { "Failed to process MLS proposals" }
            null
        }
    }

    override fun processCommit(commit: ByteArray): DaveCommitResult = lock.write {
        val s = session ?: run {
            logger.warn { "processCommit called without active session" }
            return DaveCommitResult.Failed
        }
        if (!hasJoinedGroup) {
            logger.debug { "Skipping processCommit: not yet joined group" }
            return DaveCommitResult.Ignored
        }
        try {
            val result = s.processCommit(commit)
            when {
                result.isFailed -> DaveCommitResult.Failed
                result.isIgnored -> DaveCommitResult.Ignored
                else -> {
                    val roster = result.rosterMap
                    updateKeyRatchetsFromRoster(s, roster)
                    DaveCommitResult.Success(roster)
                }
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to process MLS commit" }
            DaveCommitResult.Failed
        }
    }

    override fun processWelcome(welcome: ByteArray, recognizedUserIds: Set<Long>): Map<Long, ByteArray>? =
        lock.write {
            val s = session ?: run {
                logger.warn { "processWelcome called without active session" }
                return null
            }
            try {
                val allRecognized = buildRecognizedUserIds(recognizedUserIds)
                mlsFailureDetected = false
                val roster = s.processWelcome(welcome, allRecognized) ?: return null
                if (mlsFailureDetected) {
                    logger.warn { "MLS failure detected during processWelcome, treating as failure" }
                    return null
                }
                hasJoinedGroup = true
                updateKeyRatchetsFromRoster(s, roster)
                roster
            } catch (e: Exception) {
                logger.error(e) { "Failed to process MLS welcome" }
                null
            }
        }

    private fun buildRecognizedUserIds(externalRecognized: Set<Long>): Array<String> =
        (recognizedUserIds + setOf(selfUserId) + externalRecognized)
            .map { it.toString() }.toTypedArray()

    /**
     * Update encryptor and decryptor key ratchets from a roster map.
     * Must be called while holding the write lock.
     */
    private fun updateKeyRatchetsFromRoster(session: Session, roster: Map<Long, ByteArray>) {
        for (userId in roster.keys) {
            var ratchet: KeyRatchet? = null
            try {
                ratchet = session.getKeyRatchet(userId.toString())
                if (ratchet != null) {
                    if (userId == selfUserId) {
                        val enc = encryptor
                        if (enc != null) {
                            enc.setKeyRatchet(ratchet)
                            ratchet = null // Ownership transferred
                        }
                    } else {
                        val decryptor = decryptors.getOrPut(userId) { factory.createDecryptor() }
                        decryptor.transitionToKeyRatchet(ratchet)
                        ratchet = null // Ownership transferred
                        activeE2eeUserIds.add(userId)
                    }
                }
            } catch (e: Exception) {
                logger.error(e) { "Failed to update key ratchet for user $userId" }
            } finally {
                // Close the ratchet if ownership was NOT transferred
                ratchet?.close()
            }
        }
    }

    override fun getMarshalledKeyPackage(): ByteArray = lock.write {
        val s = session ?: run {
            logger.warn { "getMarshalledKeyPackage called without active session" }
            return ByteArray(0)
        }
        try {
            s.marshalledKeyPackage
        } catch (e: Exception) {
            logger.error(e) { "Failed to get marshalled key package" }
            ByteArray(0)
        }
    }

    override fun prepareKeyRatchets(transitionId: Int, protocolVersion: Int): Unit = lock.write {
        logger.debug { "Preparing key ratchets: transitionId=$transitionId, protocolVersion=$protocolVersion" }

        if (protocolVersion == 0) {
            // Transition to passthrough mode
            encryptor?.setPassthroughMode(true)
            for ((_, decryptor) in decryptors) {
                decryptor.transitionToPassthroughMode(true)
            }
            closePendingTransition(transitionId)
            pendingTransitions[transitionId] = PendingTransition(
                protocolVersion = 0,
                selfKeyRatchet = null,
                userKeyRatchets = emptyMap(),
            )
        } else {
            val s = session ?: run {
                logger.warn { "prepareKeyRatchets called without active session" }
                return
            }
            try {
                val selfRatchet = s.getKeyRatchet(selfUserId.toString())
                val userRatchets = mutableMapOf<Long, KeyRatchet>()
                for (userId in activeE2eeUserIds) {
                    try {
                        val ratchet = s.getKeyRatchet(userId.toString())
                        if (ratchet == null) {
                            logger.warn { "No key ratchet for user $userId during prepare" }
                            continue
                        }
                        userRatchets[userId] = ratchet
                    } catch (e: Exception) {
                        logger.error(e) { "Failed to get key ratchet for user $userId during prepare" }
                    }
                }
                closePendingTransition(transitionId)
                pendingTransitions[transitionId] = PendingTransition(
                    protocolVersion = protocolVersion,
                    selfKeyRatchet = selfRatchet,
                    userKeyRatchets = userRatchets,
                )
            } catch (e: Exception) {
                logger.error(e) { "Failed to prepare key ratchets" }
            }
        }
    }

    override fun executeTransition(transitionId: Int): Unit = lock.write {
        val transition = pendingTransitions.remove(transitionId) ?: run {
            logger.warn { "No pending transition found for id $transitionId" }
            return
        }

        logger.debug { "Executing transition $transitionId to protocol version ${transition.protocolVersion}" }
        currentProtocolVersion = transition.protocolVersion

        if (transition.protocolVersion == 0) {
            // Downgrade: delegate to reset() to avoid duplicating cleanup logic.
            // reset() handles session?.reset(), activeE2eeUserIds.clear(), hasJoinedGroup=false.
            // ReentrantReadWriteLock is reentrant so the nested write lock is safe.
            reset()
            isActive = false
        } else {
            isActive = true
            // Only mark as joined if we actually have ratchets.
            // An empty transition means we prepared ratchets before joining the group
            // (e.g. a protocol-version transition right after PrepareEpoch+reset).
            // Falsely setting hasJoinedGroup=true here would open processProposals on a
            // session that is on the wrong epoch.
            if (transition.selfKeyRatchet != null || transition.userKeyRatchets.isNotEmpty()) {
                hasJoinedGroup = true
            }
            // Apply self key ratchet
            transition.selfKeyRatchet?.let { ratchet ->
                val enc = encryptor
                if (enc != null) {
                    enc.setKeyRatchet(ratchet)
                } else {
                    ratchet.close()
                }
            }
            // Apply user key ratchets
            for ((userId, ratchet) in transition.userKeyRatchets) {
                val decryptor = decryptors.getOrPut(userId) { factory.createDecryptor() }
                decryptor.transitionToKeyRatchet(ratchet)
            }
        }
    }

    override fun reset(): Unit = lock.write {
        logger.debug { "Resetting DAVE session" }
        session?.reset()
        activeE2eeUserIds.clear()
        // Prevent stale encryption between reset and next initialize.
        // The encryptor will be fully recreated in initialize(), but
        // setting passthrough guards against any frames encrypted
        // in the interim window.
        encryptor?.setPassthroughMode(true)
        pendingTransitions.values.forEach { transition ->
            transition.selfKeyRatchet?.close()
            transition.userKeyRatchets.values.forEach { it.close() }
        }
        pendingTransitions.clear()
        hasJoinedGroup = false
    }

    override fun encryptFrame(ssrc: UInt, frame: ByteArray): ByteArray = lock.read {
        val enc = encryptor ?: return@read frame
        try {
            val maxSize = enc.getMaxCiphertextByteSize(MediaType.AUDIO, frame.size)
            val output = ByteArray(maxSize)
            val written = enc.encrypt(MediaType.AUDIO, ssrc.toInt(), frame, output)
            if (written < 0) {
                logger.trace { "Encrypt returned error code $written, passing through" }
                frame
            } else {
                output.copyOf(written)
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to encrypt frame" }
            frame
        }
    }

    override fun decryptFrame(userId: Long, frame: ByteArray): ByteArray? = lock.read {
        val dec = decryptors[userId] ?: return@read null
        try {
            val output = ByteArray(frame.size)
            val written = dec.decrypt(MediaType.AUDIO, frame, output)
            if (written < 0) {
                logger.trace { "Decrypt returned error code $written for user $userId, dropping frame" }
                null
            } else {
                output.copyOf(written)
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to decrypt frame from user $userId" }
            null
        }
    }

    override fun assignSsrcToCodec(ssrc: UInt): Unit = lock.write {
        encryptor?.assignSsrcToCodec(ssrc.toInt(), Codec.OPUS)
    }

    override fun addUser(userId: Long): Unit = lock.write {
        recognizedUserIds.add(userId)
    }

    override fun removeUser(userId: Long): Unit = lock.write {
        recognizedUserIds.remove(userId)
        activeE2eeUserIds.remove(userId)
        decryptors.remove(userId)?.close()
    }

    override fun setProtocolVersion(version: Int): Unit = lock.write {
        currentProtocolVersion = version
        session?.setProtocolVersion(version)
    }

    override fun getLastEpochAuthenticator(): ByteArray = lock.read {
        val s = session ?: return ByteArray(0)
        try {
            s.lastEpochAuthenticator
        } catch (e: Exception) {
            logger.error(e) { "Failed to get last epoch authenticator" }
            ByteArray(0)
        }
    }

    override fun close(): Unit = lock.write {
        logger.debug { "Closing DAVE protocol resources" }
        reset()  // closes pendingTransitions ratchets, resets session state, clears activeE2eeUserIds/hasJoinedGroup
        decryptors.values.forEach { it.close() }
        decryptors.clear()
        encryptor?.close()
        encryptor = null
        session?.close()
        session = null
        recognizedUserIds.clear()
        isActive = false
    }

    /**
     * Removes an existing pending transition for [transitionId] and closes its native key ratchets
     * to prevent resource leaks when Discord re-sends a prepare for the same transition ID.
     */
    private fun closePendingTransition(transitionId: Int) {
        pendingTransitions.remove(transitionId)?.let { old ->
            old.selfKeyRatchet?.close()
            old.userKeyRatchets.values.forEach { it.close() }
        }
    }

    public companion object {
        private val logger = KotlinLogging.logger {}

        /** Initial transition ID used during setup. */
        public const val INIT_TRANSITION_ID: Int = 0

        /**
         * Try to create a LibdaveDaveProtocol. Returns null if native library is not available.
         */
        public fun create(): LibdaveDaveProtocol? {
            return try {
                val factory = NativeDaveFactory()
                LibdaveDaveProtocol(factory)
            } catch (e: Throwable) {
                logger.warn(e) { "Failed to load libdave native library, DAVE E2EE will not be available" }
                null
            }
        }
    }
}
