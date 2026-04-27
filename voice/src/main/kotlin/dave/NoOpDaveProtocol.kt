package dev.kord.voice.dave

import io.github.oshai.kotlinlogging.KotlinLogging

/**
 * No-operation DAVE protocol implementation used when the native libdave
 * library is not available. All encrypt/decrypt operations pass through unchanged.
 *
 * Note: Discord voice servers requiring DAVE will reject this client with close code 4017.
 */
public object NoOpDaveProtocol : DaveProtocol {
    private val logger = KotlinLogging.logger {}

    override val maxProtocolVersion: Int = 0
    override val isActive: Boolean = false
    override val currentProtocolVersion: Int = 0

    override fun initialize(version: Int, channelId: String, selfUserId: Long, authSessionId: String) {
        logger.debug { "NoOp DAVE: initialize called but native library not available" }
    }

    override fun setExternalSender(externalSender: ByteArray) {}
    override fun processProposals(proposals: ByteArray, recognizedUserIds: Set<Long>): ByteArray? = null
    override fun processCommit(commit: ByteArray): DaveCommitResult = DaveCommitResult.Ignored
    override fun processWelcome(welcome: ByteArray, recognizedUserIds: Set<Long>): Map<Long, ByteArray>? = null
    override fun getMarshalledKeyPackage(): ByteArray = ByteArray(0)
    override fun prepareKeyRatchets(transitionId: Int, protocolVersion: Int) {}
    override fun executeTransition(transitionId: Int) {}
    override fun reset() {}

    override fun encryptFrame(ssrc: UInt, frame: ByteArray): ByteArray = frame
    override fun decryptFrame(userId: Long, frame: ByteArray): ByteArray = frame

    override fun assignSsrcToCodec(ssrc: UInt) {}
    override fun addUser(userId: Long) {}
    override fun removeUser(userId: Long) {}
    override fun setProtocolVersion(version: Int) {}
    override fun getLastEpochAuthenticator(): ByteArray = ByteArray(0)
    override fun close() {}
}
