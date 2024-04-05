package dev.kord.gateway

import kotlinx.cinterop.*
import platform.zlib.*

private class ZlibException(message: String) : IllegalStateException(message)

@OptIn(ExperimentalForeignApi::class)
internal actual fun Inflater(): Inflater = object : Inflater {
    // see https://zlib.net/manual.html

    private var decompressed = UByteArray(1024) // buffer only grows, is reused for every zlib inflate call
    private var decompressedLen = 0

    private val zStream = nativeHeap.alloc<z_stream>().also { zStream ->
        // next_in, avail_in, zalloc, zfree and opaque must be initialized before calling inflateInit
        zStream.next_in = null
        zStream.avail_in = 0u
        zStream.zalloc = null
        zStream.zfree = null
        zStream.opaque = null
        // initialize msg just in case, we use it for throwing exceptions
        zStream.msg = null
        val ret = inflateInit(zStream.ptr)
        if (ret != Z_OK) {
            try {
                throwZlibException(zStream.msg, ret)
            } finally {
                nativeHeap.free(zStream)
            }
        }
    }

    private fun throwZlibException(msg: CPointer<ByteVar>?, ret: Int): Nothing =
        throw ZlibException(msg?.toKString() ?: zError(ret)?.toKString() ?: ret.toString())

    override fun inflate(compressed: ByteArray, compressedLen: Int): String =
        compressed.asUByteArray().usePinned { compressedPinned ->
            zStream.next_in = compressedPinned.addressOf(0)
            zStream.avail_in = compressedLen.convert()
            decompressedLen = 0
            while (true) {
                val ret = decompressed.usePinned { decompressedPinned ->
                    zStream.next_out = decompressedPinned.addressOf(decompressedLen)
                    zStream.avail_out = (decompressed.size - decompressedLen).convert()
                    inflate(zStream.ptr, Z_NO_FLUSH)
                }
                if (ret != Z_OK && ret != Z_STREAM_END) {
                    throwZlibException(zStream.msg, ret)
                }
                if (zStream.avail_in == 0u || zStream.avail_out != 0u) break
                // grow decompressed buffer
                decompressedLen = decompressed.size
                decompressed = decompressed.copyOf(decompressed.size * 2)
            }
            decompressed.asByteArray().decodeToString(endIndex = decompressed.size - zStream.avail_out.convert<Int>())
        }

    override fun close() {
        val ret = inflateEnd(zStream.ptr)
        try {
            if (ret != Z_OK) throwZlibException(zStream.msg, ret)
        } finally {
            nativeHeap.free(zStream)
        }
    }
}
