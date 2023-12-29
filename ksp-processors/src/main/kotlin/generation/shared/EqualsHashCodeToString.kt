package dev.kord.ksp.generation.shared

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier.FINAL
import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.TypeSpec
import dev.kord.ksp.addFunction
import dev.kord.ksp.addParameter
import dev.kord.ksp.generation.GenerationEntity
import dev.kord.ksp.returns

internal fun TypeSpec.Builder.addEqualsAndHashCodeBasedOnClassAndSingleProperty(
    className: ClassName,
    property: String,
    isFinal: Boolean = false,
) {
    val final = if (isFinal) arrayOf(FINAL) else emptyArray()
    addFunction("equals") {
        addModifiers(*final, OVERRIDE)
        addParameter<Any?>("other")
        returns<Boolean>()
        addStatement("return this·===·other || (other·is·%T·&&·this.$property·==·other.$property)", className)
    }
    addFunction("hashCode") {
        addModifiers(*final, OVERRIDE)
        returns<Int>()
        addStatement("return $property.hashCode()")
    }
}

context(GenerationEntity)
internal fun TypeSpec.Builder.addEntityToString(property: String) = addFunction("toString") {
    addModifiers(FINAL, OVERRIDE)
    returns<String>()
    addStatement(
        "return if·(this·is·Unknown)·\"$entityName.Unknown($property=$$property)\" " +
            "else·\"$entityName.\${this::class.simpleName}\""
    )
}
