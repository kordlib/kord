@file:Suppress("CONTEXT_RECEIVERS_DEPRECATED")

package dev.kord.ksp.generation.bitflags

import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.KModifier.PRIVATE
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import dev.kord.ksp.addFunction
import dev.kord.ksp.addObject
import dev.kord.ksp.addParameter
import dev.kord.ksp.addProperty
import dev.kord.ksp.generation.GenerationEntity.BitFlags
import dev.kord.ksp.generation.GenerationEntity.BitFlags.ValueType.BIT_SET
import dev.kord.ksp.generation.GenerationEntity.BitFlags.ValueType.INT
import dev.kord.ksp.generation.shared.GenerationContext
import dev.kord.ksp.generation.shared.K_SERIALIZER
import dev.kord.ksp.generation.shared.addSharedSerializerContent
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

private val SERIALIZER_METHOD = MemberName("kotlinx.serialization.builtins", "serializer")

context(BitFlags, GenerationContext)
internal fun TypeSpec.Builder.addSerializer() = addObject("Serializer") {
    addSharedSerializerContent(collectionCN)
    addProperty("delegate", K_SERIALIZER.parameterizedBy(valueCN), PRIVATE) {
        when (valueType) {
            INT -> initializer("%T.%M()", valueCN, SERIALIZER_METHOD)
            BIT_SET -> initializer("%T.serializer()", valueCN)
        }
    }
    addFunction("serialize") {
        addModifiers(OVERRIDE)
        addParameter<Encoder>("encoder")
        addParameter("value", collectionCN)
        addStatement("encoder.encodeSerializableValue(delegate, value.$valueName)")
    }
    addFunction("deserialize") {
        addModifiers(OVERRIDE)
        addParameter<Decoder>("decoder")
        returns(collectionCN)
        addStatement("return %T(decoder.decodeSerializableValue(delegate))", collectionCN)
    }
}
