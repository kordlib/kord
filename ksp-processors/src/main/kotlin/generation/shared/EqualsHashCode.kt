package dev.kord.ksp.generation.shared

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.TypeSpec
import dev.kord.ksp.addFunction
import dev.kord.ksp.addParameter
import dev.kord.ksp.generation.GenerationEntity
import dev.kord.ksp.returns

context(GenerationEntity)
internal fun TypeSpec.Builder.addEqualsAndHashCode(className: ClassName, vararg additionalModifiers: KModifier) {
    addFunction("equals") {
        addModifiers(OVERRIDE, *additionalModifiers)
        returns<Boolean>()
        addParameter<Any?>("other")
        addStatement("return this·===·other || (other·is·%T·&&·this.$valueName·==·other.$valueName)", className)
    }
    addFunction("hashCode") {
        addModifiers(OVERRIDE, *additionalModifiers)
        returns<Int>()
        addStatement("return $valueName.hashCode()")
    }
}
