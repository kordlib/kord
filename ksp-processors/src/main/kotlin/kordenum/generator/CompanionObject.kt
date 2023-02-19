package dev.kord.ksp.kordenum.generator

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.jvm.jvmField
import dev.kord.ksp.*
import dev.kord.ksp.addCompanionObject
import dev.kord.ksp.addProperty
import dev.kord.ksp.delegate
import dev.kord.ksp.kordenum.*
import dev.kord.ksp.kordenum.KordEnum
import dev.kord.ksp.kordenum.ProcessingContext
import dev.kord.ksp.kordenum.toClassName
import dev.kord.ksp.kordenum.warningSuppressedName
import dev.kord.ksp.withControlFlow

context(KordEnum, ProcessingContext, FileSpec.Builder)
fun TypeSpec.Builder.addCompanionObject() = addCompanionObject {
    addModifiers(KModifier.PUBLIC)

    addProperty("entries", LIST.parameterizedBy(enumName), KModifier.PUBLIC) {
        addKdoc("A [List] of all known [%T]s.", enumName)
        delegate {
            withControlFlow("lazy(mode·=·%M)", LazyThreadSafetyMode.PUBLICATION.asMemberName()) {
                addStatement("listOf(")
                withIndent {
                    for (entry in relevantEntriesForSerializerAndCompanion) {
                        addStatement("${entry.warningSuppressedName},")
                    }
                }
                addStatement(")")
            }
        }
    }

    if (hasCombinerFlag) {
        addFunction("buildAll") {
            addComment("""We cannot inline this into the "All" object, because that causes a weird compiler bug""")
            addModifiers(KModifier.PRIVATE)
            returns(valueTypeName)

            val (code, parameter) = valueType.defaultParameterBlock()
            addCode("""
                    return entries.fold($code)·{·acc,·value·->
                    ⇥ acc + value.$valueName
                    ⇤}
                """.trimIndent(), parameter)
        }
    }

    // TODO bump deprecation level and remove eventually
    if (valuesPropertyName != null) {
        addProperty(
            valuesPropertyName,
            valuesPropertyType.toClassName().parameterizedBy(enumName),
            KModifier.PUBLIC,
        ) {
            @OptIn(DelicateKotlinPoetApi::class) // `AnnotationSpec.get` is ok for `Deprecated`
            addAnnotation(
                Deprecated(
                    "Renamed to 'entries'.",
                    ReplaceWith("this.entries", imports = emptyArray()),
                    level = DeprecationLevel.ERROR,
                )
            )
            getter {
                addStatement("return entries${valuesPropertyType.toFromListConversion()}")
            }
        }
    }

    // TODO remove eventually
    if (deprecatedSerializerName != null) {
        val deprecatedSerializer = enumName.nestedClass(deprecatedSerializerName)

        @OptIn(DelicateKotlinPoetApi::class)
        addProperty(deprecatedSerializerName, deprecatedSerializer, KModifier.PUBLIC) {
            addAnnotation(Suppress("DEPRECATION_ERROR"))
            addAnnotation(Deprecated("Binary compatibility", level = DeprecationLevel.HIDDEN))
            jvmField()
            initializer("%T", deprecatedSerializer)
        }
    }
}
