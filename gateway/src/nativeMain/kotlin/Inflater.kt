package dev.kord.gateway

import io.ktor.websocket.*
import kotlinx.cinterop.*
import platform.zlib.*

private const val CHUNK_SIZE = 256 * 1000
private val ZLIB_SUFFIX = ubyteArrayOf(0x00u, 0x00u, 0xffu, 0xffu)

internal actual fun Inflater(): Inflater = NativeInflater()

@OptIn(ExperimentalForeignApi::class)
private class NativeInflater : Inflater {
    // see https://www.zlib.net/manual.html

    private var frameBuffer = UByteArray(0)

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

    override fun Frame.inflateData(): String? {
        frameBuffer += data.asUByteArray()
        // check if the last four bytes are equal to ZLIB_SUFFIX
        if (frameBuffer.size < 4 ||
            !frameBuffer.copyOfRange(frameBuffer.size - 4, frameBuffer.size).contentEquals(ZLIB_SUFFIX)
        ) {
            return null
        }
        var out = ByteArray(0)
        memScoped {
            val uncompressedDataSize = CHUNK_SIZE // allocate enough space for the uncompressed data
            val uncompressedData = allocArray<uByteVar>(uncompressedDataSize)
            zStream.apply {
                next_in = frameBuffer.refTo(0).getPointer(memScope)
                avail_in = frameBuffer.size.convert()
            }

            do {
                zStream.apply {
                    next_out = uncompressedData
                    avail_out = uncompressedDataSize.convert()
                }
                inflate(zStream.ptr, Z_NO_FLUSH).check(listOf(Z_OK, Z_STREAM_END)) {
                    frameBuffer = UByteArray(0)
                }
                out += uncompressedData.readBytes(uncompressedDataSize - zStream.avail_out.convert<Int>())
            } while (zStream.avail_out == 0u)
        }

        frameBuffer = UByteArray(0)
        return out.decodeToString()
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

@ExperimentalForeignApi
private fun Int.check(validCodes: List<Int> = listOf(Z_OK), cleanup: () -> Unit = {}) {
    if (this !in validCodes) {
        try {
            throw ZlibException(zErrorMessage(this).toString())
        } finally {
            cleanup()
        }
    }
}

private class ZlibException(message: String?) : IllegalStateException(message)

@ExperimentalForeignApi
private fun zErrorMessage(errorCode: Int) = zError(errorCode)?.toKString() ?: errorCode

@ExperimentalForeignApi
private fun throwZlibException(msg: CPointer<ByteVar>?, ret: Int): Nothing =
    throw ZlibException(msg?.toKString() ?: zError(ret)?.toKString() ?: ret.toString())
