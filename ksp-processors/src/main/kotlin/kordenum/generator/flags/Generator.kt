@file:Suppress("PrivatePropertyName")

package dev.kord.ksp.kordenum.generator.flags

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import dev.kord.ksp.*
import dev.kord.ksp.addClass
import dev.kord.ksp.kordenum.*
import dev.kord.ksp.kordenum.DISCORD_BIT_SET
import dev.kord.ksp.kordenum.KordEnum
import dev.kord.ksp.kordenum.ProcessingContext
import dev.kord.ksp.kordenum.addEnum

private typealias FileSpecBuilder = FileSpec.Builder

private const val PACKAGE_NAME = "dev.kord.common.entity.flags"

internal val BIT_FLAGs = ClassName(PACKAGE_NAME, "BitFlags")
private val INT_BIT_FLAG = ClassName(PACKAGE_NAME, "IntBitFlag")
private val INT_BIT_FLAGS = INT_BIT_FLAG.flagsClass()
private val BIT_SET_BIT_FLAG = ClassName(PACKAGE_NAME, "DiscordBitSetFlag")
private val BIT_SET_BIT_FLAGS = BIT_SET_BIT_FLAG.flagsClass()

private fun GenerateKordEnum.ValueType.collectionSuperType() = when (this) {
    GenerateKordEnum.ValueType.INT -> INT_BIT_FLAGS
    GenerateKordEnum.ValueType.BITSET -> BIT_SET_BIT_FLAGS
    else -> error("Unsupported type for bit flags")
}

private fun GenerateKordEnum.ValueType.superType() = when (this) {
    GenerateKordEnum.ValueType.INT -> INT_BIT_FLAG
    GenerateKordEnum.ValueType.BITSET -> BIT_SET_BIT_FLAG
    else -> error("Unsupported type for bit flags")
}

private fun GenerateKordEnum.ValueType.defaultParameter() = when (this) {
    GenerateKordEnum.ValueType.INT -> CodeBlock.of("%L", 0)
    GenerateKordEnum.ValueType.STRING -> CodeBlock.of("%S", "")
    GenerateKordEnum.ValueType.BITSET -> CodeBlock.of("%M()", MemberName("dev.kord.common", "EmptyBitSet"))
    else -> error("Unsupported type for bit flags")
}

private fun ClassName.flagsClass() = peerClass(simpleName + 's')

context(KordEnum, ProcessingContext, FileSpecBuilder)
internal fun TypeSpec.Builder.addFlagEnum() = addEnum(additionalValuePropertyModifiers = listOf(KModifier.OVERRIDE)) {
    addSuperinterface(valueType.superType())
    primaryConstructor {
        addParameter(valueName, valueTypeName)
    }
    if (valueType == GenerateKordEnum.ValueType.BITSET) {
        addConstructor {
            addModifiers(KModifier.PROTECTED)
            addParameter("values", LONG, KModifier.VARARG)
            callThisConstructor(CodeBlock.of("%T(values)", DISCORD_BIT_SET))
        }
    }
    val collectionName = enumName.flagsClass()
    val builderName = collectionName.nestedClass("Builder")
    val serializerName = collectionName.nestedClass("Serializer")
    this@FileSpecBuilder.addClass(collectionName) {
        addFlagsDoc(collectionName, builderName)
        val collectionSuperType = valueType.collectionSuperType()
        superclass(collectionSuperType.parameterizedBy(enumName, collectionName, builderName))

        primaryConstructor {
            addCodeParameter()
        }
        addProperty("name", STRING, KModifier.PROTECTED, KModifier.OVERRIDE) {
            initializer("%S", collectionName.simpleName)
        }

        addEqualsAndHashCode()

        addFunction("buildUpon") {
            addModifiers(KModifier.INTERNAL, KModifier.OVERRIDE)
            returns(builderName)
            addCode("return %T(code)", builderName)
        }

        addFunction("Implementation") {
            addModifiers(KModifier.PROTECTED, KModifier.OVERRIDE)
            addParameter("flags", valueTypeName)
            returns(collectionName)
            addCode("return %T(flags)", collectionName)
        }
        addSuperclassConstructorParameter("%T.entries", enumName)
        addSuperclassConstructorParameter(valueName)
        addBuilder(builderName, collectionName, collectionSuperType)
        addSerializer(serializerName, collectionName)
        addCompanion(builderName, collectionName)
    }
}

context(KordEnum, ProcessingContext)
fun FunSpec.Builder.addCodeParameter() {
    addParameter(valueName, valueTypeName) {
        defaultValue(valueType.defaultParameter())
    }
}
