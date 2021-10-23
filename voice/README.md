# Kord Voice

An implementation of Discord's [Voice Connection](https://discord.com/developers/docs/topics/voice-connections).

Kord Voice acts as the middleman between Discord and your audio.

You may want to use something like [LavaPlayer](https://github.com/sedmelluq/lavaplayer), to deal with getting proper
audio. The example will use LavaPlayer to get audio. However, anything that can output 20ms of Opus-encoded 48k stereo
audio data should work fine.

## Example Usage

A bot who plays audio from YouTube using a user-provided query and LavaPlayer.

```kotlin
suspend fun main(args: Array<String>) {
    val token = args.firstOrNull() ?: error("token required")
    val kord = Kord(token)

    val lavaplayerManager = DefaultAudioPlayerManager()
    // to use YouTube, we tell LavaPlayer to use remote sources, like YouTube.
    AudioSourceManagers.registerRemoteSources(lavaplayerManager)

    // here we keep track of active voice connections
    val connections: MutableMap<Snowflake, VoiceConnection> = mutableMapOf()

    kord.on<MessageCreateEvent> {
        if (message.content.startsWith("!play")) {
            val channel = member?.getVoiceState()?.getChannelOrNull() ?: return@on

            // lets close the old connection if there is one
            if (connections.contains(guildId)) {
                connections.remove(guildId)!!.shutdown()
            }

            // our lavaplayer audio player which will provide frames of audio
            val player = lavaplayerManager.createPlayer()

            // lavaplayer uses ytsearch: as an identifier to search for YouTube
            val query = "ytsearch: ${message.content.removePrefix("!play")}"

            val track = lavaplayerManager.playTrack(query, player)

            // here we actually connect to the voice channel
            val connection = channel.connect {
                // the audio provider should provide frames of audio
                audioProvider { AudioFrame.fromData(player.provide()?.data) }
            }

            connections[guildId!!] = connection

            message.reply {
                content = "playing track: ${track.info.title}"
            }
        } else if (message.content == "!stop") {
            if (guildId == null) return@on

            connections.remove(guildId)?.shutdown()
        }
    }

    kord.login()
}

// lavaplayer isn't super kotlin-friendly, so we'll make it nicer to work with
suspend fun DefaultAudioPlayerManager.playTrack(query: String, player: AudioPlayer): AudioTrack {
    val track = suspendCoroutine<AudioTrack> {
        this.loadItem(query, object : AudioLoadResultHandler {
            override fun trackLoaded(track: AudioTrack) {
                it.resume(track)
            }

            override fun playlistLoaded(playlist: AudioPlaylist) {
                it.resume(playlist.tracks.first())
            }

            override fun noMatches() {
                TODO()
            }

            override fun loadFailed(exception: FriendlyException?) {
                TODO()
            }
        })
    }

    player.playTrack(track)

    return track
}
```

## Audio Transmission
### Sending
Kord handles all sending of audio with the help of the `AudioProvider`. The `AudioProvider` is how you will tell Kord what audio to sent to Discord. This can be configured in the `VoiceConnectionBuilder`.
```kotlin
voiceChannel.connect {
    // audio provider wants a AudioFrame each time its called by Kord
    audioProvider {
        AudioFrame.fromData(
            /* an opus audio frame, you can use something like lavaplayer to do this for you*/
        )
    }
}
```

### Receiving*
Kord provides support to receive audio from Discord (*however it is not guaranteed to be stable, as it is not documented by Discord). First you must tell Kord to process incoming packets, in the `VoiceConnectionBuilder`
```kotlin
voiceChannel.connect {
    receiveVoice = true
}
```

This flag will switch out the NOP implementation with a proper `Streams` implementation exposed in the voice connection. This `Streams` class provides access to several points of incoming packet processing.
```kotlin
val connection = voiceChannel.connect {
    receiveVoice = true
}

// processIncomingAudio is anything you want to do with the flow of `AudioFrame`s in a `Stream`.
processIncomingAudio(connection.streams.incomingAudioFrames)
```

## Installation

Replace `{version}` with the latest version number on maven central.

[![Download](https://img.shields.io/maven-central/v/dev.kord/kord-voice.svg?color=fb5502&label=Kord&logoColor=05c1fd&style=for-the-badge)](https://search.maven.org/search?q=g:%22dev.kord%22%20AND%20a:%22kord-voice%22)

### Gradle (groovy)
```groovy
dependencies {
    implementation("dev.kord:kord-voice:{version}")
}
```

### Gradle (kotlin)
```kotlin
dependencies {
    implementation("dev.kord:kord-voice:{version}")
}
```

### Maven

##### Kord Snapshots Repository (Optional):
```xml
<dependency>
    <groupId>dev.kord</groupId>
    <artifactId>kord-voice</artifactId>
    <version>{version}</version>
</dependency>
```
