@file:Generate(
    INT_FLAGS, name = "SpeakingFlag", valueName = "code",
    docUrl = "https://discord.com/developers/docs/topics/voice-connections#speaking",
    entries = [
        Entry("Microphone", shift = 0, kDoc = "Normal transmission of voice audio."),
        Entry("Soundshare", shift = 1, kDoc = "Transmission of context audio for video, no speaking indicator."),
        Entry("Priority", shift = 2, kDoc = "Priority speaker, lowering audio of other speakers."),
    ]
)

package dev.kord.voice

import dev.kord.ksp.Generate
import dev.kord.ksp.Generate.EntityType.INT_FLAGS
import dev.kord.ksp.Generate.Entry
