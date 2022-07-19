package dev.kord.ksp.kordenum

import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.KModifier.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import dev.kord.ksp.*
import dev.kord.ksp.GenerateKordEnum.ValueType
import dev.kord.ksp.GenerateKordEnum.ValueType.INT
import dev.kord.ksp.GenerateKordEnum.ValueType.STRING
import dev.kord.ksp.GenerateKordEnum.ValuesPropertyType
import dev.kord.ksp.GenerateKordEnum.ValuesPropertyType.NONE
import dev.kord.ksp.GenerateKordEnum.ValuesPropertyType.SET
import dev.kord.ksp.kordenum.KordEnum.Entry
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.DeprecationLevel.*
import kotlin.LazyThreadSafetyMode.PUBLICATION

private val PRIMITIVE_SERIAL_DESCRIPTOR = MemberName("kotlinx.serialization.descriptors", "PrimitiveSerialDescriptor")

private val Entry.warningSuppressedName
    get() = when {
        isDeprecated -> "@Suppress(\"${
            when (deprecationLevel) {
                WARNING -> "DEPRECATION"
                ERROR, HIDDEN -> "DEPRECATION_ERROR"
            }
        }\")·$name"
        else -> name
    }

private fun ValueType.toKClass() = when (this) {
    INT -> Int::class
    STRING -> String::class
}

private fun ValueType.toEncodingPostfix() = when (this) {
    INT -> "Int"
    STRING -> "String"
}

private fun ValueType.toFormat() = when (this) {
    INT -> "%L"
    STRING -> "%S"
}

private fun ValueType.toPrimitiveKind() = when (this) {
    INT -> PrimitiveKind.INT::class
    STRING -> PrimitiveKind.STRING::class
}

private fun ValuesPropertyType.toClassName() = when (this) {
    NONE -> error("did not expect NONE")
    SET -> ClassName("kotlin.collections", "Set")
}

private fun ValuesPropertyType.toFromListConversion() = when (this) {
    NONE -> error("did not expect NONE")
    SET -> ".toSet()"
}

internal fun KordEnum.generateFileSpec(originatingFile: KSFile): FileSpec {

    val packageName = originatingFile.packageName.asString()
    val enumName = ClassName(packageName, name)
    val valueKClass = valueType.toKClass()
    val encodingPostfix = valueType.toEncodingPostfix()
    val valueFormat = valueType.toFormat()

    val relevantEntriesForSerializerAndCompanion = run {

        // don't keep deprecated entries with a non-deprecated replacement
        val nonDeprecatedValues = entries.map { it.value }.toSet()

        entries
            .plus(deprecatedEntries.filter { it.value !in nonDeprecatedValues })
            .sortedWith { e1, e2 ->
                @Suppress("UNCHECKED_CAST") // values are of same type
                (e1.value as Comparable<Comparable<*>>).compareTo(e2.value)
            }
    }

    return fileSpec(packageName, fileName = name) {
        indent("    ")
        addAnnotation<Suppress> {
            addMember("%S, %S, %S", "UnusedImport", "RedundantVisibilityModifier", "IncorrectFormatting")
        }

        // /** <kDoc> */
        // @Serializable(with = <enumName>.Serializer::class)
        // public sealed class <enumName>(public val <valueName>: <valueKClass>)
        addClass(enumName) {

            // for ksp incremental processing
            addOriginatingKSFile(originatingFile)

            kDoc?.let { addKdoc(it) }
            addAnnotation<Serializable> {
                addMember("with·=·%T.Serializer::class", enumName)
            }
            addModifiers(PUBLIC, SEALED)
            primaryConstructor {
                addParameter(valueName, valueKClass)
            }
            addProperty(valueName, valueKClass, PUBLIC) {
                initializer(valueName)
            }

            // final override fun equals
            addFunction("equals") {
                addModifiers(FINAL, OVERRIDE)
                returns<Boolean>()
                addParameter<Any?>("other")
                addStatement("return this·===·other || (other·is·%T && this.$valueName·==·other.$valueName)", enumName)
            }

            // final override fun hashCode
            addFunction("hashCode") {
                addModifiers(FINAL, OVERRIDE)
                returns<Int>()
                addStatement("return $valueName.hashCode()")
            }


            // /** An unknown [<enumName>]. */
            // public class Unknown(<valueName>: <valueKClass>) : <enumName>(<valueName>)
            addClass("Unknown") {
                addKdoc("An unknown [%T].", enumName)
                addModifiers(PUBLIC)
                primaryConstructor {
                    addParameter(valueName, valueKClass)
                }
                superclass(enumName)
                addSuperclassConstructorParameter(valueName)
            }


            fun TypeSpec.Builder.entry(entry: Entry) {
                entry.kDoc?.let { addKdoc(it) }
                addModifiers(PUBLIC)
                superclass(enumName)
                addSuperclassConstructorParameter(valueFormat, entry.value)
            }

            // /** <entry.kDoc> */
            // public object <entry.name> : <enumName>(<entry.value>)
            for (entry in entries) {
                addObject(entry.name) {
                    entry(entry)
                }
            }

            // /** <entry.kDoc> */
            // @Deprecated(<entry.deprecationMessage>, <entry.replaceWith>, level = <entry.deprecationLevel>)
            // public object <entry.name> : <enumName>(<entry.value>)
            for (entry in deprecatedEntries) {
                addObject(entry.name) {
                    entry(entry)
                    addAnnotation<Deprecated> {
                        addMember("%S", entry.deprecationMessage)

                        // replacement if present
                        entry.replaceWith?.let { replaceWith ->
                            addMember("%L", annotationSpec<ReplaceWith> {
                                addMember("%S", replaceWith.expression)
                                val imports = replaceWith.imports
                                if (imports.isNotEmpty()) {
                                    addMember(imports.joinToString { "%S" }, *imports)
                                }
                            })
                        }

                        addMember("level·=·%M", entry.deprecationLevel.asMemberName())
                    }
                }
            }


            // internal object Serializer : KSerializer<<enumName>>
            addObject("Serializer") {
                addModifiers(INTERNAL)
                addSuperinterface(KSerializer::class.asClassName().parameterizedBy(enumName))

                // override val descriptor
                addProperty<SerialDescriptor>("descriptor", OVERRIDE) {
                    initializer(
                        "%M(%S, %T)",
                        PRIMITIVE_SERIAL_DESCRIPTOR,
                        enumName.canonicalName,
                        valueType.toPrimitiveKind(),
                    )
                }

                // override fun serialize
                addFunction("serialize") {
                    addModifiers(OVERRIDE)
                    addParameter<Encoder>("encoder")
                    addParameter("value", enumName)
                    addStatement("return encoder.encode$encodingPostfix(value.$valueName)")
                }

                // override fun deserialize
                addFunction("deserialize") {
                    addModifiers(OVERRIDE)
                    addParameter<Decoder>("decoder")
                    beginControlFlow("return when·(val·$valueName·=·decoder.decode$encodingPostfix())")
                    for (entry in relevantEntriesForSerializerAndCompanion) {
                        addStatement("$valueFormat·->·${entry.warningSuppressedName}", entry.value)
                    }
                    addStatement("else·->·Unknown($valueName)")
                    endControlFlow()
                }
            }


            // public companion object
            addCompanionObject {
                addModifiers(PUBLIC)

                // public val entries
                addProperty("entries", List::class.asClassName().parameterizedBy(enumName), PUBLIC) {
                    delegate {
                        beginControlFlow("lazy(mode·=·%M)", PUBLICATION.asMemberName())
                        addStatement("listOf(")
                        withIndent {
                            for (entry in relevantEntriesForSerializerAndCompanion) {
                                addStatement("${entry.warningSuppressedName},")
                            }
                        }
                        addStatement(")")
                        endControlFlow()
                    }
                }

                // @Deprecated(
                //     "Renamed to 'entries'.",
                //     ReplaceWith("this.entries"),
                //     level = <valuesPropertyDeprecationLevel>,
                // )
                // public val <entriesPropertyName>
                if (valuesPropertyName != null) {
                    addProperty(
                        valuesPropertyName,
                        valuesPropertyType.toClassName().parameterizedBy(enumName),
                        PUBLIC,
                    ) {
                        addAnnotation<Deprecated> {
                            addMember("%S", "Renamed to 'entries'.")
                            addMember("ReplaceWith(%S)", "this.entries")
                            addMember("level·=·%M", valuesPropertyDeprecationLevel.asMemberName())
                        }
                        getter {
                            addStatement("return entries${valuesPropertyType.toFromListConversion()}")
                        }
                    }
                }
            }
        }
    }
}
