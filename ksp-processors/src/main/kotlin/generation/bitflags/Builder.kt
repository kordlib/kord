package dev.kord.ksp.generation.bitflags

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier.*
import com.squareup.kotlinpoet.TypeSpec
import dev.kord.ksp.*
import dev.kord.ksp.generation.GenerationEntity.BitFlags
import dev.kord.ksp.generation.GenerationEntity.BitFlags.ValueType.BIT_SET
import dev.kord.ksp.generation.GenerationEntity.BitFlags.ValueType.INT
import dev.kord.ksp.generation.shared.GenerationContext

context(BitFlags, GenerationContext)
internal fun TypeSpec.Builder.addBuilder(collectionName: ClassName) {
    addClass("Builder") {
        primaryConstructor {
            addParameter(valueName, valueCN) {
                defaultValue(valueType.defaultParameterCode())
            }
        }
        addProperty(valueName, valueCN, PRIVATE) {
            mutable()
            initializer(valueName)
        }

        val builder = "Builder"
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
            addStatement("return %T(code)", collectionName)
        }
    }
}
