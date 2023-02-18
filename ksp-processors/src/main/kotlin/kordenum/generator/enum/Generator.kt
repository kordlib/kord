package dev.kord.ksp.kordenum.generator.enum

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import dev.kord.ksp.addFunction
import dev.kord.ksp.addParameter
import dev.kord.ksp.kordenum.KordEnum
import dev.kord.ksp.kordenum.ProcessingContext
import dev.kord.ksp.kordenum.addEnum
import dev.kord.ksp.primaryConstructor
import dev.kord.ksp.returns

context(KordEnum, ProcessingContext, FileSpec.Builder)
internal fun TypeSpec.Builder.addNormalEnum() = addEnum {
    primaryConstructor {
        addParameter(valueName, valueTypeName)
    }
    addFunction("equals") {
        addModifiers(KModifier.FINAL, KModifier.OVERRIDE)
        returns<Boolean>()
        addParameter<Any?>("other")
        addStatement("return this·===·other || (other·is·%T·&&·this.$valueName·==·other.$valueName)", enumName)
    }

    addFunction("hashCode") {
        addModifiers(KModifier.FINAL, KModifier.OVERRIDE)
        returns<Int>()
        addStatement("return $valueName.hashCode()")
    }

    addFunction("toString") {
        addModifiers(KModifier.FINAL, KModifier.OVERRIDE)
        returns<String>()
        addStatement("return \"%T.\${this::class.simpleName}($valueName=\$$valueName)\"", enumName)
    }

    addEnumSerializer()
}
