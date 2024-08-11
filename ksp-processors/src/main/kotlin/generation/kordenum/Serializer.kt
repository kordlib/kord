package dev.kord.ksp.generation.kordenum

import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.TypeSpec
import dev.kord.codegen.kotlinpoet.addFunction
import dev.kord.codegen.kotlinpoet.addObject
import dev.kord.codegen.kotlinpoet.addParameter
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

context(KordEnum, GenerationContext)
internal fun TypeSpec.Builder.addSerializer() = addObject("Serializer") {
    addSharedSerializerContent(entityCN)
    val encodingPostfix = valueType.toEncodingPostfix()
    addFunction("serialize") {
        addModifiers(OVERRIDE)
        addParameter<Encoder>("encoder")
        addParameter("value", entityCN)
        addStatement("encoder.encode$encodingPostfix(value.$valueName)")
    }
    addFunction("deserialize") {
        addModifiers(OVERRIDE)
        returns(entityCN)
        addParameter<Decoder>("decoder")
        addStatement("return from(decoder.decode$encodingPostfix())")
    }
}
