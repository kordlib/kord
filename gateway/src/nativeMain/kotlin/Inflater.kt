package dev.kord.gateway

import io.ktor.websocket.*
import kotlinx.cinterop.*
import platform.zlib.*

private const val MAX_WBITS = 15 // Maximum window size in bits
private const val CHUNK_SIZE = 256 * 1000

internal actual fun Inflater(): Inflater = NativeInflater()

@OptIn(ExperimentalForeignApi::class)
private class NativeInflater : Inflater {
    private val zStream = nativeHeap.alloc<z_stream>().apply {
        val initResponse = inflateInit2(ptr, MAX_WBITS)
        if (initResponse != Z_OK) {
            nativeHeap.free(this)
            throw ZLibException("Could not initialize zlib: ${zErrorMessage(initResponse)}")
        }
    }

    override fun Frame.inflateData(): String {
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
                    throw ZLibException(
                        "An error occurred during decompression of frame: ${zErrorMessage(resultCode)}"
                    )
                }
                out += uncompressedData.readBytes(uncompressedDataSize - zStream.avail_out.convert<Int>())
            } while (zStream.avail_out == 0u)
        }

        return out.decodeToString()
    }

    override fun close() {
        inflateEnd(zStream.ptr)
        nativeHeap.free(zStream)
    }
}

private class ZLibException(message: String?) : IllegalStateException(message)

@OptIn(ExperimentalForeignApi::class)
private fun zErrorMessage(errorCode: Int) = zError(errorCode)?.toKString() ?: errorCode
