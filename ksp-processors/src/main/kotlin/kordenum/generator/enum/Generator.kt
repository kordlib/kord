package dev.kord.ksp.kordenum.generator.enum

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import dev.kord.ksp.kordenum.KordEnum
import dev.kord.ksp.kordenum.ProcessingContext
import dev.kord.ksp.kordenum.addEnum
import dev.kord.ksp.primaryConstructor

context(KordEnum, ProcessingContext, FileSpec.Builder)
internal fun TypeSpec.Builder.addNormalEnum() = addEnum {
    primaryConstructor {
        addParameter(valueName, valueTypeName)
    }

    addEnumSerializer()
}
