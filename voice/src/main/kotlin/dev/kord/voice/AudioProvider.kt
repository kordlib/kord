package dev.kord.voice

import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.nio.ByteBuffer

val SILENCE_BYTES = byteArrayOf(0xFC.toByte(), 0xFF.toByte(), 0xFE.toByte())

sealed class AudioFrame {
    object Wait : AudioFrame()
    class Frame(val data: ByteBuffer) : AudioFrame()
    object Silence : AudioFrame() {
        val data = ByteBuffer.wrap(SILENCE_BYTES)
    }
}


fun interface AudioProvider {

    fun CoroutineScope.produce(sendChannel: SendChannel<AudioFrame>)

    companion object {

        fun take(bufferSize: Int = StandardAudioDataFormats.DISCORD_OPUS.maximumChunkSize(), producer: suspend (buffer: ByteBuffer) -> Boolean) = AudioProvider { sendChannel ->
            launch {
                val buffer = ByteBuffer.allocate(bufferSize)
                var speaking = true
                while (isActive) {
                    if (producer(buffer)) {
                        sendChannel.send(AudioFrame.Frame(buffer))
                        speaking = true
                        continue
                    }
                    if (speaking) {
                        sendChannel.send(AudioFrame.Silence)
                        speaking = false
                    } else sendChannel.send(AudioFrame.Wait)


                }
            }

        }
    }
}

fun AudioPlayer.defaultProvider(frame: MutableAudioFrame = MutableAudioFrame()) = AudioProvider.take { buffer ->
    delay(20)
    buffer.clear()
    frame.setBuffer(buffer)
    val hasData = provide(frame)
    if (hasData) buffer.flip()
    return@take hasData
}