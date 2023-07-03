package dev.kord.ksp.generation.bitflags

import com.squareup.kotlinpoet.CodeBlock
import dev.kord.ksp.generation.GenerationEntity.BitFlags.ValueType
import dev.kord.ksp.generation.GenerationEntity.BitFlags.ValueType.BIT_SET
import dev.kord.ksp.generation.GenerationEntity.BitFlags.ValueType.INT
import dev.kord.ksp.generation.shared.EMPTY_BIT_SET

internal fun ValueType.defaultParameterCode() = when (this) {
    INT -> CodeBlock.of("%L", 0)
    BIT_SET -> CodeBlock.of("%M()", EMPTY_BIT_SET)
}
