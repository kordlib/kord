package dev.kord.ksp.kordenum.generator.flags

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier.*
import com.squareup.kotlinpoet.TypeSpec
import dev.kord.ksp.GenerateKordEnum.ValueType.*
import dev.kord.ksp.addClass
import dev.kord.ksp.addFunction
import dev.kord.ksp.addProperty
import dev.kord.ksp.kordenum.KordEnum
import dev.kord.ksp.kordenum.ProcessingContext
import dev.kord.ksp.primaryConstructor

context(KordEnum, ProcessingContext)
internal fun TypeSpec.Builder.addBuilder(builderName: ClassName, collectionName: ClassName) {
    addClass(builderName) {
        primaryConstructor {
            addCodeParameter()
        }
        addProperty(valueName, valueTypeName, PRIVATE) {
            mutable()
            initializer(valueName)
        }

        val builder = builderName.simpleName
        addFunction("unaryPlus") {
            addModifiers(PUBLIC, OPERATOR)
            receiver(enumName)

            addStatement(
                when (valueType) {
                    INT -> "this@$builder.code = this@$builder.code or this.code"
                    BITSET -> "this@$builder.code.add(this.code)"
                    STRING -> throw IllegalStateException()
                }
            )
        }
        addFunction("unaryPlus") {
            addModifiers(PUBLIC, OPERATOR)
            receiver(collectionName)

            addStatement(
                when (valueType) {
                    INT -> "this@$builder.code = this@$builder.code or this.code"
                    BITSET -> "this@$builder.code.add(this.code)"
                    STRING -> throw IllegalStateException()
                }
            )
        }

        addFunction("unaryMinus") {
            addModifiers(PUBLIC, OPERATOR)
            receiver(enumName)

            addStatement(
                when (valueType) {
                    INT -> "this@$builder.code = this@$builder.code and this.code.inv()"
                    BITSET -> "this@$builder.code.remove(this.code)"
                    STRING -> throw IllegalStateException()
                }
            )
        }
        addFunction("unaryMinus") {
            addModifiers(PUBLIC, OPERATOR)
            receiver(collectionName)

            addStatement(
                when (valueType) {
                    INT -> "this@$builder.code = this@$builder.code and this.code.inv()"
                    BITSET -> "this@$builder.code.remove(this.code)"
                    STRING -> throw IllegalStateException()
                }
            )
        }

        addFunction("flags") {
            returns(collectionName)
            addCode("return %T(code)", collectionName)
        }
    }
}
