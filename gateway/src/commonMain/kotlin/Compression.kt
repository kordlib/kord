@file:Suppress("FunctionName")

package dev.kord.gateway

import dev.kord.common.annotation.KordInternal
import io.ktor.websocket.*

/** @suppress */
@KordInternal // Only public for interface, binary API might change at any time
public interface Decompressor : AutoCloseable {
    public fun Frame.decompress(): String

    public companion object Noop : Decompressor {
        override fun Frame.decompress(): String = data.decodeToString()
        override fun close() {}
    }
}

internal expect fun ZLibDecompressor(): Decompressor
internal expect fun ZstdDecompressor(): Decompressor

/**
 * Different compression modes for the Discord gateway.
 *
 * @property name the name used by the Discord API
 */
public sealed interface Compression {
    public val name: String?
    public fun newDecompressor(): Decompressor

    /**
     * Implementation using no compression.
     */
    public data object None : Compression {
        override val name: String? = null
        override fun newDecompressor(): Decompressor = Decompressor.Noop
    }

    /**
     * Implementation using [zlib](https://zlib.net/).
     */
    public data object ZLib : Compression {
        override val name: String = "zlib-stream"
        override fun newDecompressor(): Decompressor = ZLibDecompressor()
    }

    /**
     * Implementation using [Zstandard](https://facebook.github.io/zstd/)
     */
    public data object Zstd : Compression {
        override val name: String = "zstd-stream"
        override fun newDecompressor(): Decompressor = ZstdDecompressor()
    }
}
