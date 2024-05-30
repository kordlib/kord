package dev.kord.ksp.generation.bitflags

import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.SET
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import dev.kord.ksp.*
import dev.kord.ksp.generation.GenerationEntity.BitFlags
import dev.kord.ksp.generation.GenerationEntity.BitFlags.ValueType.BIT_SET
import dev.kord.ksp.generation.GenerationEntity.BitFlags.ValueType.INT
import dev.kord.ksp.generation.shared.*
import kotlinx.serialization.Serializable
import com.squareup.kotlinpoet.INT as INT_CN

context(GenerationContext)
internal val BitFlags.collectionCN
    get() = ClassName(entityCN.packageName, entityName + 's')

context(GenerationContext)
internal val BitFlags.builderCN
    get() = collectionCN.nestedClass("Builder")

internal fun BitFlags.generateFileSpec(originatingFile: KSFile) = fileSpecForGenerationEntity(originatingFile) {
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
                    INT -> addStatement("return 1·shl·shift")
                    BIT_SET -> addStatement("return %M().also·{·it[shift]·=·true·}", EMPTY_BIT_SET)
                }
            }
        }
        addPlus(parameterName = "flag", parameterType = entityCN)
        addPlus(parameterName = "flags", parameterType = collectionCN)
        addEqualsAndHashCodeBasedOnClassAndSingleProperty(entityCN, property = "shift", isFinal = true)
        addEntityToString(property = "shift")
        addUnknownClass(constructorParameterName = "shift", constructorParameterType = INT_CN)
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
        }
    }
    addClass(collectionCN) {
        addCollectionKDoc()
        addAnnotation<JvmInline> {}
        addAnnotation<Serializable> {}
        addModifiers(VALUE)
        primaryConstructor {
            addModifiers(INTERNAL)
            addParameter(valueName, valueCN)
        }
        addProperty(valueName, valueCN, PUBLIC) {
            addKdoc("The raw $valueName used by Discord.")
            initializer(valueName)
        }
        addProperty("values", type = SET.parameterizedBy(entityCN), PUBLIC) {
            addKdoc("A [Set] of all [%T]s contained in this instance of [%T].", entityCN, collectionCN)
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
        addContains(parameterName = "flag", parameterType = entityCN)
        addContains(parameterName = "flags", parameterType = collectionCN)
        addPlus(parameterName = "flag", parameterType = entityCN)
        addPlus(parameterName = "flags", parameterType = collectionCN)
        addMinus(parameterName = "flag", parameterType = entityCN)
        addMinus(parameterName = "flags", parameterType = collectionCN)
        addCopy()
        if (collectionHadCopy0) {
            addCopy0()
        }
        addFunction("toString") {
            addModifiers(OVERRIDE)
            returns<String>()
            addStatement("return \"${collectionCN.simpleName}(values=\$values)\"")
        }
        addBuilder()
        if (collectionHadNewCompanion) {
            addDeprecatedNewCompanion()
        }
    }
    addFactoryFunctions()
}
