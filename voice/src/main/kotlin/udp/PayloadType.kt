package dev.kord.voice.udp

/**
 * A guesstimated list of known Discord RTP payloads.
 */
sealed class PayloadType(val raw: Byte) {
    object Alive : PayloadType(0x37.toByte())
    object Audio : PayloadType(0x78.toByte())
    class Unknown(value: Byte) : PayloadType(value)

    companion object {
        fun from(value: Byte) = when (value) {
            0x37.toByte() -> Alive
            0x78.toByte() -> Audio
            else -> Unknown(value)
        }
    }
}