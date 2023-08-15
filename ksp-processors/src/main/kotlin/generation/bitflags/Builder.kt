package dev.kord.ksp.generation.bitflags

import com.squareup.kotlinpoet.DelicateKotlinPoetApi
import com.squareup.kotlinpoet.KModifier.*
import com.squareup.kotlinpoet.TypeSpec
import dev.kord.ksp.*
import dev.kord.ksp.generation.GenerationEntity.BitFlags
import dev.kord.ksp.generation.GenerationEntity.BitFlags.ValueType.BIT_SET
import dev.kord.ksp.generation.GenerationEntity.BitFlags.ValueType.INT
import dev.kord.ksp.generation.shared.EMPTY_BIT_SET
import dev.kord.ksp.generation.shared.GenerationContext

context(BitFlags, GenerationContext)
internal fun TypeSpec.Builder.addBuilder() {
    addClass(builderCN) {
        primaryConstructor {
            addParameter(valueName, valueCN) {
                when (valueType) {
                    INT -> defaultValue("0")
                    BIT_SET -> defaultValue("%M()", EMPTY_BIT_SET)
                }
            }
        }
        addProperty(valueName, valueCN, PRIVATE) {
            mutable()
            initializer(valueName)
        }

        val builder = builderCN.simpleName
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
            receiver(collectionCN)

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
            receiver(collectionCN)

            addStatement(
                when (valueType) {
                    INT -> "this@$builder.$valueName = this@$builder.$valueName and this.$valueName.inv()"
                    BIT_SET -> "this@$builder.$valueName.remove(this.$valueName)"
                }
            )
        }

        addFunction("build") {
            returns(collectionCN)
            addStatement("return %T($valueName)", collectionCN)
        }

        addFunction("flags") {
            addKdoc("@suppress")
            @OptIn(DelicateKotlinPoetApi::class)
            addAnnotation(
                Deprecated(
                    "Renamed to 'build'",
                    ReplaceWith("this.build()", imports = emptyArray()),
                    DeprecationLevel.WARNING,
                )
            )
            returns(collectionCN)
            addStatement("return build()")
        }
    }
}
