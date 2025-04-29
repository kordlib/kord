package dev.kord.voice.udp

public class DecryptedVoicePacket @ExperimentalUnsignedTypes constructor(
    public val sequenceNumber: UShort,
    public val timestamp: UInt,
    public val ssrc: UInt,
    @property:ExperimentalUnsignedTypes
    public val csrcs: UIntArray,
    public val headerExtension: HeaderExtension?,
    public val decryptedAudio: ByteArray,
) {
    @OptIn(ExperimentalUnsignedTypes::class)
    public constructor(
        sequenceNumber: UShort,
        timestamp: UInt,
        ssrc: UInt,
        headerExtension: HeaderExtension?,
        decryptedAudio: ByteArray,
    ) : this(sequenceNumber, timestamp, ssrc, csrcs = EMPTY_UINT_ARRAY, headerExtension, decryptedAudio)

    public class HeaderExtension @ExperimentalUnsignedTypes constructor(
        public val definedByProfile: UShort,
        @property:ExperimentalUnsignedTypes
        public val headerExtension: UIntArray,
    )

    internal companion object {
        @ExperimentalUnsignedTypes
        internal val EMPTY_UINT_ARRAY = UIntArray(size = 0)
    }
}
