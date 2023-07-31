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
                    INT -> "this@$builder.$valueName = this@$builder.$valueName or this.$valueName"
                    BIT_SET -> "this@$builder.$valueName.add(this.$valueName)"
                }
            )
        }
        addFunction("unaryPlus") {
            addModifiers(PUBLIC, OPERATOR)
            receiver(collectionName)

            addStatement(
                when (valueType) {
                    INT -> "this@$builder.$valueName = this@$builder.$valueName or this.$valueName"
                    BIT_SET -> "this@$builder.$valueName.add(this.$valueName)"
                }
            )
        }

        addFunction("unaryMinus") {
            addModifiers(PUBLIC, OPERATOR)
            receiver(entityCN)

            addStatement(
                when (valueType) {
                    INT -> "this@$builder.$valueName = this@$builder.$valueName and this.$valueName.inv()"
                    BIT_SET -> "this@$builder.$valueName.remove(this.$valueName)"
                }
            )
        }
        addFunction("unaryMinus") {
            addModifiers(PUBLIC, OPERATOR)
            receiver(collectionName)

            addStatement(
                when (valueType) {
                    INT -> "this@$builder.$valueName = this@$builder.$valueName and this.$valueName.inv()"
                    BIT_SET -> "this@$builder.$valueName.remove(this.$valueName)"
                }
            )
        }

        addFunction("flags") {
            returns(collectionName)
            addStatement("return %T($valueName)", collectionName)
        }
    }
}
