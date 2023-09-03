package dev.kord.ksp.generation.shared

import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.STRING
import dev.kord.ksp.generation.GenerationEntity
import dev.kord.ksp.generation.GenerationEntity.BitFlags
import dev.kord.ksp.generation.GenerationEntity.KordEnum
import kotlinx.serialization.descriptors.PrimitiveKind

internal fun GenerationEntity.ValueType.toClassName() = when (this) {
    KordEnum.ValueType.INT, BitFlags.ValueType.INT -> INT
    KordEnum.ValueType.STRING -> STRING
    BitFlags.ValueType.BIT_SET -> DISCORD_BIT_SET
}

internal fun GenerationEntity.ValueType.toFormat() = when (this) {
    KordEnum.ValueType.INT, BitFlags.ValueType.INT, BitFlags.ValueType.BIT_SET -> "%L"
    KordEnum.ValueType.STRING -> "%S"
}

internal fun GenerationEntity.ValueType.toPrimitiveKind() = when (this) {
    KordEnum.ValueType.INT, BitFlags.ValueType.INT -> PrimitiveKind.INT::class
    KordEnum.ValueType.STRING, BitFlags.ValueType.BIT_SET -> PrimitiveKind.STRING::class
}
