package dev.kord.ksp.generation.generator.flags

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier.*
import com.squareup.kotlinpoet.TypeSpec
import dev.kord.ksp.addClass
import dev.kord.ksp.addFunction
import dev.kord.ksp.addProperty
import dev.kord.ksp.generation.GenerationEntity.BitFlags
import dev.kord.ksp.generation.GenerationEntity.BitFlags.ValueType.BIT_SET
import dev.kord.ksp.generation.GenerationEntity.BitFlags.ValueType.INT
import dev.kord.ksp.generation.ProcessingContext
import dev.kord.ksp.primaryConstructor

context(BitFlags, ProcessingContext)
internal fun TypeSpec.Builder.addBuilder(builderName: ClassName, collectionName: ClassName) {
    addClass(builderName) {
        primaryConstructor {
            addCodeParameter()
        }
        addProperty(valueName, valueCN, PRIVATE) {
            mutable()
            initializer(valueName)
        }

        val builder = builderName.simpleName
        addFunction("unaryPlus") {
            addModifiers(PUBLIC, OPERATOR)
            receiver(entityCN)

            addStatement(
                when (valueType) {
                    INT -> "this@$builder.code = this@$builder.code or this.code"
                    BIT_SET -> "this@$builder.code.add(this.code)"
                }
            )
        }
        addFunction("unaryPlus") {
            addModifiers(PUBLIC, OPERATOR)
            receiver(collectionName)

            addStatement(
                when (valueType) {
                    INT -> "this@$builder.code = this@$builder.code or this.code"
                    BIT_SET -> "this@$builder.code.add(this.code)"
                }
            )
        }

        addFunction("unaryMinus") {
            addModifiers(PUBLIC, OPERATOR)
            receiver(entityCN)

            addStatement(
                when (valueType) {
                    INT -> "this@$builder.code = this@$builder.code and this.code.inv()"
                    BIT_SET -> "this@$builder.code.remove(this.code)"
                }
            )
        }
        addFunction("unaryMinus") {
            addModifiers(PUBLIC, OPERATOR)
            receiver(collectionName)

            addStatement(
                when (valueType) {
                    INT -> "this@$builder.code = this@$builder.code and this.code.inv()"
                    BIT_SET -> "this@$builder.code.remove(this.code)"
                }
            )
        }

        addFunction("flags") {
            returns(collectionName)
            addCode("return %T(code)", collectionName)
        }
    }
}
