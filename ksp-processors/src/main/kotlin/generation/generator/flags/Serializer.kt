package dev.kord.ksp.generation.generator.flags

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier.*
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import dev.kord.ksp.*
import dev.kord.ksp.generation.GenerationEntity.BitFlags
import dev.kord.ksp.generation.GenerationEntity.BitFlags.ValueType.BIT_SET
import dev.kord.ksp.generation.GenerationEntity.BitFlags.ValueType.INT
import dev.kord.ksp.generation.K_SERIALIZER
import dev.kord.ksp.generation.PRIMITIVE_SERIAL_DESCRIPTOR
import dev.kord.ksp.generation.ProcessingContext
import dev.kord.ksp.generation.toPrimitiveKind
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

private val SERIALIZER_METHOD = MemberName("kotlinx.serialization.builtins", "serializer")

context(BitFlags, ProcessingContext, FileSpec.Builder)
internal fun TypeSpec.Builder.addSerializer(collectionName: ClassName) {
    addAnnotation<Serializable> {
        addMember("with·=·%T.Serializer::class", collectionName)
    }
    addObject("Serializer") {
        addModifiers(INTERNAL)
        addSuperinterface(K_SERIALIZER.parameterizedBy(collectionName))

        addProperty<SerialDescriptor>("descriptor", OVERRIDE) {
            initializer(
                "%M(%S, %T)",
                PRIMITIVE_SERIAL_DESCRIPTOR,
                collectionName.canonicalName,
                valueType.toPrimitiveKind(),
            )
        }

        addProperty("delegate", K_SERIALIZER.parameterizedBy(valueCN), PRIVATE) {
            when (valueType) {
                INT -> initializer("%T.%M()", valueCN, SERIALIZER_METHOD)
                BIT_SET -> initializer("%T.serializer()", valueCN)
            }
        }

        addFunction("serialize") {
            addModifiers(OVERRIDE)
            addParameter<Encoder>("encoder")
            addParameter("value", collectionName)
            addStatement("return encoder.encodeSerializableValue(delegate, value.$valueName)")
        }

        addFunction("deserialize") {
            addModifiers(OVERRIDE)
            addParameter<Decoder>("decoder")
            addStatement("return %T(decoder.decodeSerializableValue(delegate))", collectionName)
        }
    }
}
