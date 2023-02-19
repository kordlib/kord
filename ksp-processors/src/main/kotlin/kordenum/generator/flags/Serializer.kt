package dev.kord.ksp.kordenum.generator.flags

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import dev.kord.ksp.GenerateKordEnum
import dev.kord.ksp.addAnnotation
import dev.kord.ksp.addClass
import dev.kord.ksp.addFunction
import dev.kord.ksp.kordenum.KordEnum
import dev.kord.ksp.kordenum.ProcessingContext
import dev.kord.ksp.kordenum.toPrimitiveKind
import kotlinx.serialization.Serializable

private val SERIALIZER_METHOD = MemberName("kotlinx.serialization.builtins", "serializer")

context(KordEnum, ProcessingContext, FileSpec.Builder)
internal fun TypeSpec.Builder.addSerializer(serializerName: ClassName, collectionName: ClassName) {
    addAnnotation<Serializable> {
        addMember("with·=·%T::class", serializerName)
    }
    addClass(serializerName) {
        superclass(BIT_FLAGs.nestedClass("Serializer").parameterizedBy(valueTypeName, enumName, collectionName))
        addSuperclassConstructorParameter("%T", valueType.toPrimitiveKind())
        addSuperclassConstructorParameter("%S", valueName)
        if (valueType != GenerateKordEnum.ValueType.BITSET) {
            addSuperclassConstructorParameter("%T.%M()", valueTypeName, SERIALIZER_METHOD)
        } else {
            addSuperclassConstructorParameter("%T.serializer()", valueTypeName)
        }

        addFunction("Implementation") {
            addModifiers(KModifier.OVERRIDE)
            addParameter("code", valueTypeName)
            returns(collectionName)
            addCode("return %T(code)", collectionName)
        }
    }
}
