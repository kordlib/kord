package dev.kord.gateway

import dev.kord.common.annotation.KordUnsafe
import io.ktor.util.*
import io.ktor.websocket.*
import java.io.ByteArrayOutputStream
import java.util.zip.Inflater
import java.util.zip.InflaterOutputStream

/**
 * [WebSocketExtension] inflating incoming websocket requests using `zlib`.
 *
 * *Note:** Normally you don't need this and this is configured by Kord automatically, however, if you want to use
 * a custom HTTP client, you might need to add this, don't use it if you don't use what you're doing
 */
@KordUnsafe
public class WebSocketCompression : WebSocketExtension<Unit> {
    /**
     * https://discord.com/developers/docs/topics/gateway#transport-compression
     *
     * > Every connection to the gateway should use its own unique zlib context.
     *
     * https://api.ktor.io/ktor-shared/ktor-websockets/io.ktor.websocket/-web-socket-extension/index.html
     * > A WebSocket extension instance. This instance is created for each WebSocket request,
     * for every installed extension by WebSocketExtensionFactory.
     */
    private val inflater = Inflater()

    override val factory: WebSocketExtensionFactory<Unit, out WebSocketExtension<Unit>>
        get() = Companion
    override val protocols: List<WebSocketExtensionHeader>
        get() = emptyList()

    override fun clientNegotiation(negotiatedProtocols: List<WebSocketExtensionHeader>): Boolean = true

    override fun processIncomingFrame(frame: Frame): Frame {
        return if (frame is Frame.Binary) {
            frame.deflateData()
        } else {
            frame
        }
    }

    // Discord doesn't support deflating of gateway commands
    override fun processOutgoingFrame(frame: Frame): Frame = frame

    override fun serverNegotiation(requestedProtocols: List<WebSocketExtensionHeader>): List<WebSocketExtensionHeader> =
        requestedProtocols

    private fun Frame.deflateData(): Frame {
        val outputStream = ByteArrayOutputStream()
        InflaterOutputStream(outputStream, inflater).use {
            it.write(data)
        }

        return outputStream.use {
            val raw = String(outputStream.toByteArray(), 0, outputStream.size(), Charsets.UTF_8)
            Frame.Text(raw)
        }
    }

    public companion object : WebSocketExtensionFactory<Unit, WebSocketCompression> {
        override val key: AttributeKey<WebSocketCompression> = AttributeKey("WebSocketCompression")
        override val rsv1: Boolean = false
        override val rsv2: Boolean = false
        override val rsv3: Boolean = false

        override fun install(config: Unit.() -> Unit): WebSocketCompression = WebSocketCompression()
    }
}
