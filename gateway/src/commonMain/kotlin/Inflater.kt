package dev.kord.gateway

import io.ktor.utils.io.core.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

internal interface Inflater : Closeable {
    /** Decompresses [compressedLen] bytes from [compressed] and decodes them to a [String]. */
    fun inflate(compressed: ByteArray, compressedLen: Int): String
}

internal expect fun Inflater(): Inflater

// check if the last four bytes are equal to Z_SYNC_FLUSH suffix (00 00 ff ff),
// see https://discord.com/developers/docs/topics/gateway#transport-compression
private fun ByteArray.endsWithZlibSuffix(len: Int) = len >= 4
    && this[len - 4] == 0x00.toByte()
    && this[len - 3] == 0x00.toByte()
    && this[len - 2] == 0xff.toByte()
    && this[len - 1] == 0xff.toByte()

internal fun Flow<Frame>.decompressFrames(inflater: Inflater): Flow<String> {
    var buffer = ByteArray(0)
    var bufferLen = 0
    return transform { frame ->
        when (frame) {
            is Frame.Text, is Frame.Binary -> {
                val data = frame.data
                val dataLen = data.size
                // skip copying into buffer if buffer is empty and data has suffix
                if (bufferLen == 0 && data.endsWithZlibSuffix(dataLen)) {
                    emit(inflater.inflate(data, dataLen))
                } else {
                    if (buffer.size - bufferLen < dataLen) {
                        buffer = buffer.copyOf(bufferLen + dataLen)
                    }
                    data.copyInto(buffer, destinationOffset = bufferLen)
                    bufferLen += dataLen
                    if (buffer.endsWithZlibSuffix(bufferLen)) {
                        emit(inflater.inflate(buffer, bufferLen))
                        bufferLen = 0
                    }
                }
            }
            else -> {} // ignore other frame types
        }
    }
}
