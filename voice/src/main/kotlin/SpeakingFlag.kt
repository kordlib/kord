@file:GenerateKordEnum(
    name = "SpeakingFlag",
    valueType = GenerateKordEnum.ValueType.INT,
    isFlags = true,
    entries = [
        GenerateKordEnum.Entry(name = "Microphone", intValue = 1 shl 0),
        GenerateKordEnum.Entry(name = "Soundshare", intValue = 1 shl 1),
        GenerateKordEnum.Entry(name = "Priority", intValue = 1 shl 2)
    ]
)

package dev.kord.voice

import dev.kord.ksp.GenerateKordEnum
