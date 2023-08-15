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

context(GenerationContext)
internal val BitFlags.collectionCN
    get() = ClassName(entityCN.packageName, entityName + 's')

context(GenerationContext)
internal val BitFlags.builderCN
    get() = collectionCN.nestedClass("Builder")

internal fun BitFlags.generateFileSpec(originatingFile: KSFile) = fileSpecForGenerationEntity(originatingFile) {
    addClass(collectionCN) {
        addCollectionDoc()
        addAnnotation<Serializable> {
            addMember("with·=·%T.Serializer::class", collectionCN)
        }
        primaryConstructor {
            addModifiers(INTERNAL)
            addParameter(valueName, valueCN)
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
        addContains(parameterName = "flags", parameterType = collectionCN)
        addPlus(parameterName = "flag", parameterType = entityCN)
        addPlus(parameterName = "flags", parameterType = collectionCN)
        addMinus(parameterName = "flag", parameterType = entityCN)
        addMinus(parameterName = "flags", parameterType = collectionCN)
        addFunction("copy") {
            addModifiers(PUBLIC, INLINE)
            addParameter("block", type = LambdaTypeName.get(receiver = builderCN, returnType = UNIT))
            returns(collectionCN)
            addStatement("%M { callsInPlace(block, %M) }", CONTRACT, EXACTLY_ONCE)
            addStatement("return %T($valueName).apply(block).build()", builderCN)
        }
        addEqualsAndHashCodeBasedOnClassAndSingleProperty(collectionCN, property = valueName)
        addFunction("toString") {
            addModifiers(OVERRIDE)
            returns<String>()
            addStatement("return \"${collectionCN.simpleName}(values=\$values)\"")
        }
        if (collectionWasDataClass) {
            addDeprecatedDataClassArtifacts()
        }
        addBuilder()
        addSerializer()
    }
    addFactoryFunctions()
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

        addPlus(parameterName = "flag", parameterType = entityCN)
        addPlus(parameterName = "flags", parameterType = collectionCN)
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

context(BitFlags, GenerationContext)
private fun FileSpec.Builder.addFactoryFunctions() {
    val factoryFunctionName = collectionCN.simpleName

    addFunction(factoryFunctionName) {
        addModifiers(PUBLIC, INLINE)
        addParameter("builder", type = LambdaTypeName.get(receiver = builderCN, returnType = UNIT)) {
            defaultValue("{}")
        }
        returns(collectionCN)

        addStatement("%M { callsInPlace(builder, %M) }", CONTRACT, EXACTLY_ONCE)
        addStatement("return %T().apply(builder).build()", builderCN)
    }

    addFunction(factoryFunctionName) {
        addModifiers(PUBLIC)
        addParameter("flags", entityCN, VARARG)
        returns(collectionCN)

        addStatement("return $factoryFunctionName·{ flags.forEach·{ +it } }")
    }

    addFunction(factoryFunctionName) {
        addModifiers(PUBLIC)
        addParameter("flags", collectionCN, VARARG)
        returns(collectionCN)

        addStatement("return $factoryFunctionName·{ flags.forEach·{ +it } }")
    }

    addFunction(factoryFunctionName) {
        addModifiers(PUBLIC)
        addParameter("flags", ITERABLE.parameterizedBy(entityCN))
        returns(collectionCN)

        addStatement("return $factoryFunctionName·{ flags.forEach·{ +it } }")
    }

    addFunction(factoryFunctionName) {
        jvmName("${factoryFunctionName}0")
        addModifiers(PUBLIC)
        addParameter("flags", ITERABLE.parameterizedBy(collectionCN))
        returns(collectionCN)

        addStatement("return $factoryFunctionName·{ flags.forEach·{ +it } }")
    }
}
