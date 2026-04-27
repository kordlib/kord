package dev.kord.voice.dave

import java.io.Closeable

/**
 * Result of processing an MLS commit message.
 */
public sealed class DaveCommitResult {
    /** The commit processing failed. */
    public object Failed : DaveCommitResult()
    /** The commit was irrelevant or a duplicate and was ignored. */
    public object Ignored : DaveCommitResult()
    /** The commit was successfully processed, producing roster changes. */
    public data class Success(val rosterMap: Map<Long, ByteArray>) : DaveCommitResult()
}

/**
 * Abstraction for the DAVE (Discord Audio & Video End-to-End Encryption) protocol.
 *
 * DAVE uses MLS (Messaging Layer Security) for group key exchange and AES-128-GCM
 * for per-frame media encryption between voice channel participants.
 *
 * Two implementations exist:
 * - [LibdaveDaveProtocol] — real implementation using libdave-jvm JNI bindings
 * - [NoOpDaveProtocol] — passthrough fallback when native library is unavailable
 */
public interface DaveProtocol : Closeable {
    /** Maximum supported DAVE protocol version. 0 if DAVE is not available. */
    public val maxProtocolVersion: Int

    /** Whether DAVE E2EE is currently active. */
    public val isActive: Boolean

    /** The current DAVE protocol version. */
    public val currentProtocolVersion: Int

    /**
     * Initialize the MLS session for a voice channel.
     *
     * @param version DAVE protocol version
     * @param channelId voice channel or DM call ID (used as MLS group ID)
     * @param selfUserId the bot's own user ID (as a Long/snowflake)
     * @param authSessionId authentication session ID from the Ready event
     */
    public fun initialize(version: Int, channelId: String, selfUserId: Long, authSessionId: String)

    /** Set the voice gateway's MLS external sender credential. */
    public fun setExternalSender(externalSender: ByteArray)

    /**
     * Process MLS proposals from the voice gateway.
     * @return commit+welcome bytes to send back, or null if no response needed
     */
    public fun processProposals(proposals: ByteArray, recognizedUserIds: Set<Long>): ByteArray?

    /** Process an MLS commit message. */
    public fun processCommit(commit: ByteArray): DaveCommitResult

    /**
     * Process an MLS welcome message (when joining an existing group).
     * @return roster map of userId → keyData, or null on failure
     */
    public fun processWelcome(welcome: ByteArray, recognizedUserIds: Set<Long>): Map<Long, ByteArray>?

    /** Get the marshalled MLS key package to send to the voice gateway. */
    public fun getMarshalledKeyPackage(): ByteArray

    /**
     * Prepare key ratchets for a protocol transition.
     *
     * @param transitionId the transition identifier
     * @param protocolVersion the target protocol version (0 = non-DAVE)
     */
    public fun prepareKeyRatchets(transitionId: Int, protocolVersion: Int)

    /** Execute a pending protocol transition. */
    public fun executeTransition(transitionId: Int)

    /** Reset the MLS session state. */
    public fun reset()

    /**
     * Encrypt a raw audio frame for sending.
     *
     * @param ssrc the SSRC for this audio stream
     * @param frame raw audio frame bytes
     * @return encrypted frame bytes, or the original frame if passthrough
     */
    public fun encryptFrame(ssrc: UInt, frame: ByteArray): ByteArray

    /**
     * Decrypt an encrypted audio frame from a specific user.
     *
     * @param userId the sender's user ID
     * @param frame encrypted frame bytes
     * @return decrypted frame bytes, or null if decryption failed (frame should be dropped)
     */
    public fun decryptFrame(userId: Long, frame: ByteArray): ByteArray?

    /** Register an SSRC with the OPUS codec for the encryptor. */
    public fun assignSsrcToCodec(ssrc: UInt)

    /** Add a recognized user ID (for MLS proposal validation). */
    public fun addUser(userId: Long)

    /** Remove a user and clean up their decryptor resources. */
    public fun removeUser(userId: Long)

    /** Set the DAVE protocol version. */
    public fun setProtocolVersion(version: Int)

    /** Get the epoch authenticator for verification display. */
    public fun getLastEpochAuthenticator(): ByteArray
}
