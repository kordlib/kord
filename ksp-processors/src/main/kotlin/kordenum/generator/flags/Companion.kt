package dev.kord.ksp.kordenum.generator.flags

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import dev.kord.ksp.GenerateKordEnum
import dev.kord.ksp.addCompanionObject
import dev.kord.ksp.addFunction
import dev.kord.ksp.kordenum.DISCORD_BIT_SET
import dev.kord.ksp.kordenum.KordEnum
import dev.kord.ksp.kordenum.ProcessingContext

context (ProcessingContext, KordEnum)
private fun GenerateKordEnum.ValueType.companionSuperType(collectionName: ClassName, builderName: ClassName) =
    when (this) {
        GenerateKordEnum.ValueType.BITSET -> BIT_SET_BIT_FLAGS.nestedClass("Companion")
            .parameterizedBy(enumName, collectionName, builderName)

        else -> BIT_FLAGs.nestedClass("Companion").parameterizedBy(valueTypeName, enumName, collectionName, builderName)
    }

context(KordEnum, ProcessingContext)
internal fun TypeSpec.Builder.addCompanion(
    builderName: ClassName,
    collectionName: ClassName
) {
    addCompanionObject {
        superclass(valueType.companionSuperType(collectionName, builderName))

        addFunction("Builder") {
            addModifiers(KModifier.OVERRIDE)
            returns(builderName)
            addCode("return %T.%T()", builderName.enclosingClassName(), builderName)
        }

        if (valueType == GenerateKordEnum.ValueType.BITSET) {
            addFunction("Implementation") {
                addModifiers(KModifier.PROTECTED, KModifier.OVERRIDE)
                returns(collectionName)
                addParameter("flags", DISCORD_BIT_SET)
                addCode("return %T(flags)", collectionName)
            }
        }
    }
}
