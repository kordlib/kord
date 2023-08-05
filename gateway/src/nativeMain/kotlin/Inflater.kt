package dev.kord.gateway

import io.ktor.utils.io.core.*
import io.ktor.websocket.*
import kotlinx.cinterop.*
import platform.zlib.*

private const val MAX_WBITS = 15 // Maximum window size in bits
private const val CHUNK_SIZE = 256 * 1000

@OptIn(ExperimentalForeignApi::class)
internal actual class Inflater : Closeable {
    private val zStream = nativeHeap.alloc<z_stream>().apply {
        val initResponse = inflateInit2(ptr, MAX_WBITS)
        if (initResponse != Z_OK) {
            nativeHeap.free(this)
            throw IllegalStateException("Could not initialize zlib: $initResponse")
        }
    }

    actual fun Frame.inflateData(): String {
        val compressedData = data
        var out = ByteArray(0)
        memScoped {
            val uncompressedDataSize = CHUNK_SIZE // allocate enough space for the uncompressed data
            val uncompressedData = allocArray<uByteVar>(uncompressedDataSize)
            zStream.apply {
                next_in = compressedData.refTo(0).getPointer(memScope).reinterpret()
                avail_in = compressedData.size.convert()
            }

            do {
                zStream.apply {
                    next_out = uncompressedData
                    avail_out = uncompressedDataSize.convert()
                }
                val resultCode = inflate(zStream.ptr, Z_NO_FLUSH)
                if (resultCode != Z_OK && resultCode != Z_STREAM_END) {
                    throw IllegalStateException("An error occurred during decompression of frame: $resultCode")
                }
                out += uncompressedData.readBytes(uncompressedDataSize - zStream.avail_out.toInt())
            } while (zStream.avail_out == 0u)
        }

        return out.decodeToString()
    }

    override fun close() {
        inflateEnd(zStream.ptr)
        nativeHeap.free(zStream)
    }
}
