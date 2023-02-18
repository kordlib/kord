package dev.kord.ksp.kordenum.generator.flags

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import dev.kord.ksp.addCompanionObject
import dev.kord.ksp.addFunction
import dev.kord.ksp.kordenum.KordEnum
import dev.kord.ksp.kordenum.ProcessingContext

context(KordEnum, ProcessingContext)
internal fun TypeSpec.Builder.addCompanion(
    builderName: ClassName,
    collectionName: ClassName
) {
    addCompanionObject {
        superclass(
            BIT_FLAGs.nestedClass("Companion")
                .parameterizedBy(valueTypeName, enumName, collectionName, builderName)
        )

        addFunction("Builder") {
            addModifiers(KModifier.OVERRIDE)
            returns(builderName)
            addCode("return %T()", builderName)
        }
    }
}
