@file:Suppress("CONTEXT_RECEIVERS_DEPRECATED")

package dev.kord.ksp.generation.kordenum

import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.TypeSpec
import dev.kord.ksp.addFunction
import dev.kord.ksp.addObject
import dev.kord.ksp.addParameter
import dev.kord.ksp.generation.GenerationEntity.KordEnum
import dev.kord.ksp.generation.GenerationEntity.KordEnum.ValueType
import dev.kord.ksp.generation.GenerationEntity.KordEnum.ValueType.INT
import dev.kord.ksp.generation.GenerationEntity.KordEnum.ValueType.STRING
import dev.kord.ksp.generation.shared.GenerationContext
import dev.kord.ksp.generation.shared.addSharedSerializerContent
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

private fun ValueType.toEncodingPostfix() = when (this) {
    INT -> "Int"
    STRING -> "String"
}

context(enum: KordEnum, context: GenerationContext)
internal fun TypeSpec.Builder.addSerializer() = addObject("Serializer") {
    addSharedSerializerContent(context.entityCN)
    val encodingPostfix = enum.valueType.toEncodingPostfix()
    addFunction("serialize") {
        addModifiers(OVERRIDE)
        addParameter<Encoder>("encoder")
        addParameter("value", context.entityCN)
        addStatement("encoder.encode$encodingPostfix(value.${enum.valueName})")
    }
    addFunction("deserialize") {
        addModifiers(OVERRIDE)
        returns(context.entityCN)
        addParameter<Decoder>("decoder")
        addStatement("return from(decoder.decode$encodingPostfix())")
    }
}
