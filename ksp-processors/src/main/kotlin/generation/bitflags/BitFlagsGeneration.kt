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
                withControlFlow("return buildSet") {
                    when (valueType) {
                        INT -> {
                            addStatement("var·remaining·=·$valueName")
                            addStatement("var·shift·=·0")
                            withControlFlow("while·(remaining·!=·0)") {
                                addStatement("if·((remaining·and·1)·!=·0)·add(%T.fromShift(shift))", entityCN)
                                addStatement("remaining·=·remaining·ushr·1")
                                addStatement("shift++")
                            }
                        }
                        BIT_SET -> withControlFlow("for·(shift·in·0..<$valueName.size)") {
                            addStatement("if·($valueName[shift])·add(%T.fromShift(shift))", entityCN)
                        }
                    }
                }
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
        addContains(parameterName = "flag", parameterType = entityCN)
        addContains(parameterName = "flags", parameterType = collectionName)
        addPlus(parameterName = "flag", parameterType = entityCN, collectionName)
        addPlus(parameterName = "flags", parameterType = collectionName, collectionName)
        addMinus(parameterName = "flag", parameterType = entityCN, collectionName)
        addMinus(parameterName = "flags", parameterType = collectionName, collectionName)
        addFunction("copy") {
            addModifiers(PUBLIC, INLINE)
            addParameter("block", type = LambdaTypeName.get(receiver = builderName, returnType = UNIT))
            returns(collectionName)
            addStatement("%M { callsInPlace(block, %M) }", CONTRACT, EXACTLY_ONCE)
            addStatement("return %T($valueName).apply(block).build()", builderName)
        }
        addEqualsAndHashCodeBasedOnClassAndSingleProperty(collectionName, property = valueName)
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
        val shiftTest = when (valueType) {
            INT -> "in 0..30" // Int actually supports shifting by 31, but that would result in <0
            BIT_SET -> ">= 0"
        }
        primaryConstructor {
            addParameter<Int>("shift")
        }
        addProperty<Int>("shift", PUBLIC) {
            addKdoc("The position of the bit that is set in this [%T]. This is always $shiftTest.", entityCN)
            initializer("shift")
        }
        addInitializerBlock {
            addStatement("require(shift·$shiftTest)·{ %P }", "shift has to be $shiftTest but was \$shift")
        }
        addProperty(valueName, valueCN, PUBLIC) {
            addKdoc("The raw $valueName used by Discord.")
            getter {
                when (valueType) {
                    INT -> addStatement("return 1 shl shift")
                    BIT_SET -> addStatement("return %M().also·{·it[shift]·=·true·}", EMPTY_BIT_SET)
                }
            }
        }

        addPlus(parameterName = "flag", parameterType = entityCN, collectionName)
        addPlus(parameterName = "flags", parameterType = collectionName, collectionName)
        addEqualsAndHashCodeBasedOnClassAndSingleProperty(entityCN, property = "shift", FINAL)
        addFunction("toString") {
            addModifiers(FINAL, OVERRIDE)
            returns<String>()
            addStatement(
                "return if·(this·is·Unknown) \"$entityName.Unknown(shift=\$shift)\" else " +
                    "\"$entityName.\${this::class.simpleName}\""
            )
        }
        if (wasEnum) {
            addDeprecatedEntityEnumArtifacts()
        }
        addClass("Unknown") {
            addSharedUnknownClassContent()
            primaryConstructor {
                addModifiers(INTERNAL)
                addParameter<Int>("shift")
            }
            addSuperclassConstructorParameter("shift")
        }
        addEntityEntries()
        addCompanionObject {
            addSharedCompanionObjectContent()
            addFunction("fromShift") {
                addKdoc(
                    "Returns an instance of [%1T] with [%1T.shift] equal to the specified [shift].\n\n" +
                        "@throws IllegalArgumentException if [shift] is not $shiftTest.",
                    entityCN,
                )
                addEntryOptIns()
                addParameter<Int>("shift")
                returns(entityCN)
                withControlFlow("return when·(shift)") {
                    for (entry in entriesDistinctByValue) {
                        addStatement("$valueFormat·->·${entry.nameWithSuppressedDeprecation}", entry.value)
                    }
                    addStatement("else·->·Unknown(shift)")
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
        addStatement("return %T().apply(builder).build()", builderName)
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
