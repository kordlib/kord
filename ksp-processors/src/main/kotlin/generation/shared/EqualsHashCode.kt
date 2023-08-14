package dev.kord.ksp.generation.shared

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.TypeSpec
import dev.kord.ksp.addFunction
import dev.kord.ksp.addParameter
import dev.kord.ksp.returns

internal fun TypeSpec.Builder.addEqualsAndHashCodeBasedOnClassAndSingleProperty(
    className: ClassName,
    property: String,
    vararg modifiers: KModifier,
) {
    addFunction("equals") {
        addModifiers(OVERRIDE, *modifiers)
        returns<Boolean>()
        addParameter<Any?>("other")
        addStatement("return this·===·other || (other·is·%T·&&·this.$property·==·other.$property)", className)
    }
    addFunction("hashCode") {
        addModifiers(OVERRIDE, *modifiers)
        returns<Int>()
        addStatement("return $property.hashCode()")
    }
}
