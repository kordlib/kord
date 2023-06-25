@file:Suppress("PrivatePropertyName")

package dev.kord.ksp.kordenum.generator.flags

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.KModifier.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.jvm.jvmName
import dev.kord.ksp.*
import dev.kord.ksp.GenerateKordEnum.ValueType.BITSET
import dev.kord.ksp.GenerateKordEnum.ValueType.INT
import dev.kord.ksp.GenerateKordEnum.ValueType.STRING
import dev.kord.ksp.addClass
import dev.kord.ksp.kordenum.*

private typealias FileSpecBuilder = FileSpec.Builder

private fun ClassName.flagsClass() = peerClass(simpleName + 's')
private val CONTRACT = MemberName("kotlin.contracts", "contract")
private val EXACTLY_ONCE = MemberName("kotlin.contracts.InvocationKind", "EXACTLY_ONCE")

context(KordEnum, ProcessingContext, FileSpecBuilder)
internal fun TypeSpec.Builder.addFlagEnum() = addEnum {
    primaryConstructor {
        addParameter(valueName, valueTypeName)
    }
    if (valueType == GenerateKordEnum.ValueType.BITSET) {
        addConstructor {
            addModifiers(KModifier.PROTECTED)
            addParameter("values", LONG, KModifier.VARARG)
            callThisConstructor(CodeBlock.of("%T(values)", DISCORD_BIT_SET))
        }
    }
    val collectionName = enumName.flagsClass()
    val builderName = collectionName.nestedClass("Builder")
    this@FileSpecBuilder.addClass(collectionName) {
        addFlagsDoc(collectionName, builderName)

        primaryConstructor {
            addCodeParameter()
        }
        addProperty(valueName, valueTypeName, PUBLIC) {
            initializer(valueName)
        }

        addProperty("values", type = SET.parameterizedBy(enumName), PUBLIC) {
            getter {
                addStatement("return %T.entries.filter·{·it·in·this·}.toSet()", enumName)
            }
        }

        addFunction("contains") {
            addModifiers(PUBLIC, OPERATOR)
            addParameter("flag", enumName)
            returns<Boolean>()

            addStatement(
                when (valueType) {
                    INT -> "return this.code·and·flag.code·==·flag.code"
                    BITSET -> "return flag.code·in·this.code"
                    STRING -> throw IllegalStateException()
                }
            )
        }
        addFunction("contains") {
            addModifiers(PUBLIC, OPERATOR)
            addParameter("flags", collectionName)
            returns<Boolean>()

            addStatement(
                when (valueType) {
                    INT -> "return this.code·and·flags.code·==·flags.code"
                    BITSET -> "return flags.code·in·this.code"
                    STRING -> throw IllegalStateException()
                }
            )
        }

        addFunction("plus") {
            addModifiers(PUBLIC, OPERATOR)
            addParameter("flag", enumName)
            returns(collectionName)

            addStatement(
                when (valueType) {
                    INT -> "return %T(this.code·or·flag.code)"
                    BITSET -> "return %T(this.code·+·flag.code)"
                    STRING -> throw IllegalStateException()
                },
                collectionName,
            )
        }
        addFunction("plus") {
            addModifiers(PUBLIC, OPERATOR)
            addParameter("flags", collectionName)
            returns(collectionName)

            addStatement(
                when (valueType) {
                    INT -> "return %T(this.code·or·flags.code)"
                    BITSET -> "return %T(this.code·+·flags.code)"
                    STRING -> throw IllegalStateException()
                },
                collectionName,
            )
        }

        addFunction("minus") {
            addModifiers(PUBLIC, OPERATOR)
            addParameter("flag", enumName)
            returns(collectionName)

            addStatement(
                when (valueType) {
                    INT -> "return %T(this.code·and·flag.code.inv())"
                    BITSET -> "return %T(this.code·-·flag.code)"
                    STRING -> throw IllegalStateException()
                },
                collectionName,
            )
        }
        addFunction("minus") {
            addModifiers(PUBLIC, OPERATOR)
            addParameter("flags", collectionName)
            returns(collectionName)

            addStatement(
                when (valueType) {
                    INT -> "return %T(this.code·and·flags.code.inv())"
                    BITSET -> "return %T(this.code·-·flags.code)"
                    STRING -> throw IllegalStateException()
                },
                collectionName,
            )
        }

        addEqualsAndHashCode(collectionName)

        addFunction("toString") {
            addModifiers(OVERRIDE)
            returns<String>()
            addStatement("return \"${collectionName.simpleName}(values=\$values)\"")
        }
        addBuilder(builderName, collectionName)
        addSerializer(collectionName)
    }
    this@FileSpecBuilder.addTopLevelFunctions(collectionName, builderName)
}

context(KordEnum, ProcessingContext)
fun FunSpec.Builder.addCodeParameter() {
    addParameter(valueName, valueTypeName) {
        defaultValue(valueType.defaultParameter())
    }
}

context(KordEnum, ProcessingContext)
private fun FileSpec.Builder.addTopLevelFunctions(collectionName: ClassName, builderName: ClassName) {
    val factoryFunctionName = collectionName.simpleName

    addFunction(factoryFunctionName) {
        addModifiers(PUBLIC, INLINE)
        addParameter("builder", type = LambdaTypeName.get(receiver = builderName, returnType = UNIT))
        returns(collectionName)

        addStatement("%M { callsInPlace(builder, %M) }", CONTRACT, EXACTLY_ONCE)
        addStatement("return %T().apply(builder).flags()", builderName)
    }

    addFunction(factoryFunctionName) {
        addModifiers(PUBLIC)
        addParameter("flags", enumName, VARARG)
        returns(collectionName)

        addStatement("return $factoryFunctionName·{ flags.forEach·{ +it } }")
    }

    addFunction(factoryFunctionName) {
        addModifiers(PUBLIC)
        addParameter("flags", collectionName, VARARG)
        returns(collectionName)

        addStatement("return $factoryFunctionName·{ flags.forEach·{ +it } }")
    }

    addFunction(factoryFunctionName) {
        addModifiers(PUBLIC)
        addParameter("flags", ITERABLE.parameterizedBy(enumName))
        returns(collectionName)

        addStatement("return $factoryFunctionName·{ flags.forEach·{ +it } }")
    }

    addFunction(factoryFunctionName) {
        jvmName("${factoryFunctionName}0")
        addModifiers(PUBLIC)
        addParameter("flags", ITERABLE.parameterizedBy(collectionName))
        returns(collectionName)

        addStatement("return $factoryFunctionName·{ flags.forEach·{ +it } }")
    }

    addFunction("copy") {
        addModifiers(PUBLIC, INLINE)
        receiver(collectionName)
        addParameter("block", type = LambdaTypeName.get(receiver = builderName, returnType = UNIT))
        returns(collectionName)

        addStatement("%M { callsInPlace(block, %M) }", CONTRACT, EXACTLY_ONCE)
        addStatement("return %T($valueName).apply(block).flags()", builderName)
    }
}
