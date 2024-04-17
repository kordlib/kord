package dev.kord.gateway

import kotlinx.cinterop.*
import platform.zlib.*

@ExperimentalForeignApi
private class ZlibException(msg: CPointer<ByteVar>?, ret: Int) : IllegalStateException(
    message = msg?.toKString()
        ?: zError(ret)?.toKString()?.ifEmpty { null } // zError returns empty string for unknown codes
        ?: "unexpected return code: $ret"
)

@OptIn(ExperimentalForeignApi::class)
internal actual fun Inflater(): Inflater = object : Inflater {
    // see https://zlib.net/manual.html

    private var decompressed = UByteArray(1024) // buffer only grows, is reused for every zlib inflate call
    private var decompressedLen = 0
    private var closed = false
    private val zStream = nativeHeap.alloc<z_stream>()

    init {
        try {
            // next_in, avail_in, zalloc, zfree and opaque must be initialized before calling inflateInit
            zStream.next_in = null
            zStream.avail_in = 0u
            zStream.zalloc = null
            zStream.zfree = null
            zStream.opaque = null
            // initialize msg to null in case inflateInit doesn't, we use it for throwing exceptions
            zStream.msg = null
            val ret = inflateInit(zStream.ptr)
            if (ret != Z_OK) throw ZlibException(zStream.msg, ret)
        } catch (e: Throwable) {
            try {
                nativeHeap.free(zStream)
            } catch (freeException: Throwable) {
                e.addSuppressed(freeException)
            }
            throw e
        }
    }

    override fun inflate(compressed: ByteArray, compressedLen: Int): String {
        check(!closed) { "Inflater has already been closed." }
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
                    throw ZlibException(zStream.msg, ret)
                }
                if (zStream.avail_in == 0u || zStream.avail_out != 0u) break
                // grow decompressed buffer
                decompressedLen = decompressed.size
                decompressed = decompressed.copyOf(decompressed.size * 2)
            }
        }
        return decompressed
            .asByteArray()
            .decodeToString(endIndex = decompressed.size - zStream.avail_out.convert<Int>())
    }

    override fun close() {
        if (closed) return
        closed = true
        try {
            val ret = inflateEnd(zStream.ptr)
            if (ret != Z_OK) throw ZlibException(zStream.msg, ret)
        } catch (e: Throwable) {
            try {
                nativeHeap.free(zStream)
            } catch (freeException: Throwable) {
                e.addSuppressed(freeException)
            }
            throw e
        }
        nativeHeap.free(zStream)
    }
}
