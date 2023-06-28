package dev.kord.ksp.generation.generator.flags

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.KModifier.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.jvm.jvmName
import dev.kord.ksp.*
import dev.kord.ksp.addClass
import dev.kord.ksp.generation.*
import dev.kord.ksp.generation.GenerationEntity.BitFlags
import dev.kord.ksp.generation.GenerationEntity.BitFlags.ValueType.BIT_SET
import dev.kord.ksp.generation.GenerationEntity.BitFlags.ValueType.INT

private typealias FileSpecBuilder = FileSpec.Builder

private fun ClassName.flagsClass() = peerClass(simpleName + 's')
private val CONTRACT = MemberName("kotlin.contracts", "contract")
private val EXACTLY_ONCE = MemberName("kotlin.contracts.InvocationKind", "EXACTLY_ONCE")

context(BitFlags, ProcessingContext, FileSpecBuilder)
internal fun TypeSpec.Builder.addBitFlags() = addEntity {
    primaryConstructor {
        addParameter(valueName, valueCN)
    }
    if (valueType == BIT_SET) {
        addConstructor {
            addModifiers(PROTECTED)
            addParameter("values", LONG, VARARG)
            callThisConstructor(CodeBlock.of("%T(values)", DISCORD_BIT_SET))
        }
    }
    val collectionName = entityCN.flagsClass()
    val builderName = collectionName.nestedClass("Builder")
    this@FileSpecBuilder.addClass(collectionName) {
        addFlagsDoc(collectionName, builderName)

        primaryConstructor {
            addCodeParameter()
        }
        addProperty(valueName, valueCN, PUBLIC) {
            initializer(valueName)
        }

        addProperty("values", type = SET.parameterizedBy(entityCN), PUBLIC) {
            getter {
                addStatement("return %T.entries.filter·{·it·in·this·}.toSet()", entityCN)
            }
        }

        addFunction("contains") {
            addModifiers(PUBLIC, OPERATOR)
            addParameter("flag", entityCN)
            returns<Boolean>()

            addStatement(
                when (valueType) {
                    INT -> "return this.code·and·flag.code·==·flag.code"
                    BIT_SET -> "return flag.code·in·this.code"
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
                    BIT_SET -> "return flags.code·in·this.code"
                }
            )
        }

        addFunction("plus") {
            addModifiers(PUBLIC, OPERATOR)
            addParameter("flag", entityCN)
            returns(collectionName)

            addStatement(
                when (valueType) {
                    INT -> "return %T(this.code·or·flag.code)"
                    BIT_SET -> "return %T(this.code·+·flag.code)"
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
                    BIT_SET -> "return %T(this.code·+·flags.code)"
                },
                collectionName,
            )
        }

        addFunction("minus") {
            addModifiers(PUBLIC, OPERATOR)
            addParameter("flag", entityCN)
            returns(collectionName)

            addStatement(
                when (valueType) {
                    INT -> "return %T(this.code·and·flag.code.inv())"
                    BIT_SET -> "return %T(this.code·-·flag.code)"
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
                    BIT_SET -> "return %T(this.code·-·flags.code)"
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

context(BitFlags, ProcessingContext)
fun FunSpec.Builder.addCodeParameter() {
    addParameter(valueName, valueCN) {
        defaultValue(valueType.defaultParameter())
    }
}

context(BitFlags, ProcessingContext)
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
        addParameter("flags", entityCN, VARARG)
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
        addParameter("flags", ITERABLE.parameterizedBy(entityCN))
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
