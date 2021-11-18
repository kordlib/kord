package dev.kord.voice.udp

/**
 * A guesstimated list of known Discord RTP payloads.
 */
public sealed class PayloadType(public val raw: Byte) {
    public object Alive : PayloadType(0x37.toByte())
    public object Audio : PayloadType(0x78.toByte())
    public class Unknown(value: Byte) : PayloadType(value)

    public companion object {
        public fun from(value: Byte): PayloadType = when (value) {
            0x37.toByte() -> Alive
            0x78.toByte() -> Audio
            else -> Unknown(value)
        }
    }
}