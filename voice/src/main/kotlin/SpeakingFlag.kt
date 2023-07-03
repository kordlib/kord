@file:Generate(
    INT_FLAGS, name = "SpeakingFlag", wasEnum = true,
    docUrl = "https://discord.com/developers/docs/topics/voice-connections#speaking",
    entries = [
        Entry(name = "Microphone", intValue = 1 shl 0),
        Entry(name = "Soundshare", intValue = 1 shl 1),
        Entry(name = "Priority", intValue = 1 shl 2),
    ]
)

package dev.kord.voice

import dev.kord.ksp.Generate
import dev.kord.ksp.Generate.EntityType.INT_FLAGS
import dev.kord.ksp.Generate.Entry
