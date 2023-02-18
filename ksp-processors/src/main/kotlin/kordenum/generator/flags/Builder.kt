package dev.kord.ksp.kordenum.generator.flags

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import dev.kord.ksp.addClass
import dev.kord.ksp.addFunction
import dev.kord.ksp.kordenum.KordEnum
import dev.kord.ksp.kordenum.ProcessingContext
import dev.kord.ksp.primaryConstructor

context(KordEnum, ProcessingContext)
internal fun TypeSpec.Builder.addBuilder(builderName: ClassName, collectionName: ClassName, collectionSuperType: ClassName) {
    addClass(builderName) {
        primaryConstructor {
            addCodeParameter()
        }
        superclass(collectionSuperType.nestedClass("Builder").parameterizedBy(enumName, collectionName))
        addSuperclassConstructorParameter(valueName)

        addFunction("flags") {
            addModifiers(KModifier.OVERRIDE)
            returns(collectionName)
            addCode("return %T(code)", collectionName)
        }
    }

}
