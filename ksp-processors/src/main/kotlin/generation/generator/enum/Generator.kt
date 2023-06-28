package dev.kord.ksp.generation.generator.enum

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import dev.kord.ksp.generation.GenerationEntity.KordEnum
import dev.kord.ksp.generation.ProcessingContext
import dev.kord.ksp.generation.addEntity

context(KordEnum, ProcessingContext, FileSpec.Builder)
internal fun TypeSpec.Builder.addKordEnum() = addEntity {
    addKordEnumSerializer()
}
