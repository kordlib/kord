package dev.kord.ksp.generation.bitflags

import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.KModifier.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.jvm.jvmName
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import dev.kord.ksp.*
import dev.kord.ksp.generation.GenerationEntity.BitFlags
import dev.kord.ksp.generation.GenerationEntity.BitFlags.ValueType.BIT_SET
import dev.kord.ksp.generation.GenerationEntity.BitFlags.ValueType.INT
import dev.kord.ksp.generation.shared.*
import kotlinx.serialization.Serializable

internal fun BitFlags.generateFileSpec(originatingFile: KSFile) = fileSpecForGenerationEntity(originatingFile) {
    val collectionName = ClassName(packageName, entityName + 's')
    val builderName = collectionName.nestedClass("Builder")
    addClass(collectionName) {
        addCollectionDoc(collectionName, builderName)
        addAnnotation<Serializable> {
            addMember("with·=·%T.Serializer::class", collectionName)
        }
        primaryConstructor {
            addParameter(valueName, valueCN) {
                defaultValue(valueType.defaultParameterCode())
            }
        }
        addProperty(valueName, valueCN, PUBLIC) {
            initializer(valueName)
        }
        addProperty("values", type = SET.parameterizedBy(entityCN), PUBLIC) {
            getter {
                addStatement("return %T.entries.filter·{·it·in·this·}.toSet()", entityCN)
            }
        }
        if (hadFlagsProperty) {
            val type = (if (flagsPropertyWasSet) SET else LIST).parameterizedBy(entityCN)
            addProperty("flags", type, PUBLIC) {
                @OptIn(DelicateKotlinPoetApi::class)
                addAnnotation(
                    Deprecated(
                        "Renamed to 'values'.",
                        ReplaceWith("this.values", imports = emptyArray()),
                        DeprecationLevel.WARNING,
                    )
                )
                getter {
                    addStatement(if (flagsPropertyWasSet) "return values" else "return values.toList()")
                }
            }
        }
        addFunction("contains") {
            addModifiers(PUBLIC, OPERATOR)
            addParameter("flag", entityCN)
            returns<Boolean>()
            addStatement(
                when (valueType) {
                    INT -> "return this.$valueName·and·flag.$valueName·==·flag.$valueName"
                    BIT_SET -> "return flag.$valueName·in·this.$valueName"
                }
            )
        }
        addFunction("contains") {
            addModifiers(PUBLIC, OPERATOR)
            addParameter("flags", collectionName)
            returns<Boolean>()
            addStatement(
                when (valueType) {
                    INT -> "return this.$valueName·and·flags.$valueName·==·flags.$valueName"
                    BIT_SET -> "return flags.$valueName·in·this.$valueName"
                }
            )
        }
        addFunction("plus") {
            addModifiers(PUBLIC, OPERATOR)
            addParameter("flag", entityCN)
            returns(collectionName)
            addStatement(
                when (valueType) {
                    INT -> "return %T(this.$valueName·or·flag.$valueName)"
                    BIT_SET -> "return %T(this.$valueName·+·flag.$valueName)"
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
                    INT -> "return %T(this.$valueName·or·flags.$valueName)"
                    BIT_SET -> "return %T(this.$valueName·+·flags.$valueName)"
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
                    INT -> "return %T(this.$valueName·and·flag.$valueName.inv())"
                    BIT_SET -> "return %T(this.$valueName·-·flag.$valueName)"
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
                    INT -> "return %T(this.$valueName·and·flags.$valueName.inv())"
                    BIT_SET -> "return %T(this.$valueName·-·flags.$valueName)"
                },
                collectionName,
            )
        }
        addFunction("copy") {
            addModifiers(PUBLIC, INLINE)
            addParameter("block", type = LambdaTypeName.get(receiver = builderName, returnType = UNIT))
            returns(collectionName)
            addStatement("%M { callsInPlace(block, %M) }", CONTRACT, EXACTLY_ONCE)
            addStatement("return %T($valueName).apply(block).flags()", builderName)
        }
        addEqualsAndHashCode(collectionName)
        addFunction("toString") {
            addModifiers(OVERRIDE)
            returns<String>()
            addStatement("return \"${collectionName.simpleName}(values=\$values)\"")
        }
        if (collectionWasDataClass) {
            addDeprecatedDataClassArtifacts(collectionName)
        }
        addBuilder(collectionName)
        addSerializer(collectionName)
    }
    addFactoryFunctions(collectionName, builderName)
    addClass(entityCN) {
        // for ksp incremental processing
        addOriginatingKSFile(originatingFile)
        addEntityKDoc()
        addModifiers(PUBLIC, SEALED)
        if (hasCombinerFlag) {
            addProperty(valueName, valueCN, PUBLIC) {
                addKdoc("The raw $valueName used by Discord.")
            }
            addConstructor {
                addModifiers(PRIVATE)
                addParameter<Int>("shift")
                addStatement("this.$valueName = %M().also·{·it[shift]·=·true·}", EMPTY_BIT_SET)
            }
            addConstructor {
                addModifiers(PRIVATE)
                addParameter(valueName, valueCN)
                addStatement("this.$valueName = $valueName")
            }
        } else {
            primaryConstructor {
                addParameter<Int>("shift")
            }
            addProperty(valueName, valueCN, PUBLIC) {
                addKdoc("The raw $valueName used by Discord.")
                when (valueType) {
                    INT -> initializer("1 shl shift")
                    BIT_SET -> initializer("%M().also·{·it[shift]·=·true·}", EMPTY_BIT_SET)
                }
            }
        }
        addFunction("plus") {
            addModifiers(PUBLIC, OPERATOR)
            addParameter("flag", entityCN)
            returns(collectionName)
            addStatement(
                when (valueType) {
                    INT -> "return %T(this.$valueName·or·flag.$valueName)"
                    BIT_SET -> "return %T(this.$valueName·+·flag.$valueName)"
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
                    INT -> "return %T(this.$valueName·or·flags.$valueName)"
                    BIT_SET -> "return %T(this.$valueName·+·flags.$valueName)"
                },
                collectionName,
            )
        }
        addEntityEqualsHashCodeToString()
        if (wasEnum) {
            addDeprecatedEntityEnumArtifacts()
        }
        addClass("Unknown") {
            addSharedUnknownClassContent()
            primaryConstructor {
                addAnnotation(KORD_UNSAFE)
                addParameter<Int>("shift")
            }
            addSuperclassConstructorParameter("shift")
        }
        addEntityEntries()
        if (hasCombinerFlag) {
            addObject("All") {
                addKdoc("A combination of all [%T]s", entityCN)
                addModifiers(PUBLIC)
                superclass(entityCN)
                addSuperclassConstructorParameter("buildAll()")
            }
        }
        addCompanionObject {
            addSharedCompanionObjectContent()
            if (hasCombinerFlag) {
                addFunction("buildAll") {
                    addModifiers(PRIVATE)
                    returns(valueCN)
                    addComment("""We cannot inline this into the "All" object, because that causes a weird compiler bug""")
                    withControlFlow("return entries.fold(%L)·{·acc,·value·->", valueType.defaultParameterCode()) {
                        addStatement("acc·+·value.$valueName")
                    }
                }
            }
            if (wasEnum) {
                addDeprecatedEntityCompanionObjectEnumArtifacts()
            }
        }
    }
}

context(GenerationContext)
private fun FileSpec.Builder.addFactoryFunctions(collectionName: ClassName, builderName: ClassName) {
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
}
