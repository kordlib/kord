package dev.kord.ksp.generation.bitflags

import com.squareup.kotlinpoet.KModifier.OPERATOR
import com.squareup.kotlinpoet.KModifier.PUBLIC
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import dev.kord.ksp.addFunction
import dev.kord.ksp.generation.GenerationEntity.BitFlags
import dev.kord.ksp.generation.GenerationEntity.BitFlags.ValueType.BIT_SET
import dev.kord.ksp.generation.GenerationEntity.BitFlags.ValueType.INT
import dev.kord.ksp.generation.shared.GenerationContext
import dev.kord.ksp.returns

context(BitFlags)
internal fun TypeSpec.Builder.addContains(parameterName: String, parameterType: TypeName) = addFunction("contains") {
    addModifiers(PUBLIC, OPERATOR)
    addParameter(parameterName, parameterType)
    returns<Boolean>()
    addStatement(
        when (valueType) {
            INT -> "return this.$valueName·and·$parameterName.$valueName·==·$parameterName.$valueName"
            BIT_SET -> "return $parameterName.$valueName·in·this.$valueName"
        }
    )
}

context(BitFlags, GenerationContext)
internal fun TypeSpec.Builder.addPlus(parameterName: String, parameterType: TypeName) = addFunction("plus") {
    addModifiers(PUBLIC, OPERATOR)
    addParameter(parameterName, parameterType)
    returns(collectionCN)
    addStatement(
        when (valueType) {
            INT -> "return %T(this.$valueName·or·$parameterName.$valueName)"
            BIT_SET -> "return %T(this.$valueName·+·$parameterName.$valueName)"
        },
        collectionCN,
    )
}

context(BitFlags, GenerationContext)
internal fun TypeSpec.Builder.addMinus(parameterName: String, parameterType: TypeName) = addFunction("minus") {
    addModifiers(PUBLIC, OPERATOR)
    addParameter(parameterName, parameterType)
    returns(collectionCN)
    addStatement(
        when (valueType) {
            INT -> "return %T(this.$valueName·and·$parameterName.$valueName.inv())"
            BIT_SET -> "return %T(this.$valueName·-·$parameterName.$valueName)"
        },
        collectionCN,
    )
}
