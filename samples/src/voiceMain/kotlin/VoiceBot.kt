@file:OptIn(KordVoice::class)

package dev.kord.voice.test

import dev.kord.common.annotation.KordVoice
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.BaseVoiceChannelBehavior
import dev.kord.core.behavior.channel.connect
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.on
import dev.kord.voice.AudioFrame
import kotlinx.coroutines.launch
import runMain

fun main(args: Array<String>) = runMain {
    val kord =
        Kord(args.firstOrNull() ?: error("Missing token"))

    kord.createGlobalApplicationCommands {
        input("join", "Test command") {
            dmPermission = false
        }
    }

    kord.on<GuildChatInputCommandInteractionCreateEvent> {
        val channel = interaction.user.asMember(interaction.guildId).getVoiceState().getChannelOrNull()
        if (channel == null) {
            interaction.respondPublic { content = "not in channel" }
            return@on
        }
        interaction.respondPublic { content = "success" }
        channel.connectEcho()
    }

    kord.login()
}

@OptIn(KordVoice::class)
private suspend fun BaseVoiceChannelBehavior.connectEcho() {
    val buffer = mutableListOf(AudioFrame.SILENCE, AudioFrame.SILENCE, AudioFrame.SILENCE, AudioFrame.SILENCE)
    val connection = connect {
        receiveVoice = true
        audioProvider {
            buffer.removeFirstOrNull() ?: AudioFrame.SILENCE
        }
    }
    connection.scope.launch {
        connection.streams.incomingAudioFrames.collect { (userId, frame) ->
            println("Received frame from:${userId}")
            buffer.add(frame)
        }
    }
}
