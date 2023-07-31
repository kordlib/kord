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
import dev.kord.ksp.kordenum.KordEnum.Entry
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.DeprecationLevel.*
import kotlin.LazyThreadSafetyMode.PUBLICATION
import com.squareup.kotlinpoet.INT as INT_CLASS_NAME
import com.squareup.kotlinpoet.STRING as STRING_CLASS_NAME

private val PRIMITIVE_SERIAL_DESCRIPTOR = MemberName("kotlinx.serialization.descriptors", "PrimitiveSerialDescriptor")
private val K_SERIALIZER = KSerializer::class.asClassName()

private val Entry.warningSuppressedName
    get() = when (deprecated?.level) {
        null -> name
        WARNING -> """@Suppress("DEPRECATION")·$name"""
        ERROR, HIDDEN -> """@Suppress("DEPRECATION_ERROR")·$name"""
    }

private fun ValueType.toClassName() = when (this) {
    INT -> INT_CLASS_NAME
    STRING -> STRING_CLASS_NAME
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

internal fun KordEnum.generateFileSpec(originatingFile: KSFile): FileSpec {

    val packageName = originatingFile.packageName.asString()
    val enumName = ClassName(packageName, name)
    val valueTypeName = valueType.toClassName()
    val encodingPostfix = valueType.toEncodingPostfix()
    val valueFormat = valueType.toFormat()

    val relevantEntriesForSerializerAndCompanion = entries
        .groupBy { it.value } // one entry per unique value is relevant
        .map { (_, group) -> group.firstOrNull { it.deprecated == null } ?: group.first() }

    return FileSpec(enumName) {
        indent("    ")
        addFileComment("THIS FILE IS AUTO-GENERATED, DO NOT EDIT!")

        @OptIn(DelicateKotlinPoetApi::class) // `AnnotationSpec.get` is ok for `Suppress`
        addAnnotation(
            Suppress(
                "RedundantVisibilityModifier",
                "IncorrectFormatting",
                "ReplaceArrayOfWithLiteral",
                "SpellCheckingInspection",
                "GrazieInspection",
            )
        )

        addKotlinDefaultImports(includeJvm = false, includeJs = false)

        addClass(enumName) {

            // for ksp incremental processing
            addOriginatingKSFile(originatingFile)

            // KDoc for the kord enum
            run {
                val docLink = "See [%T]s in the [Discord·Developer·Documentation]($docUrl)."
                val combinedKDocFormat = if (kDoc != null) "$kDoc\n\n$docLink" else docLink
                addKdoc(combinedKDocFormat, enumName)
            }

            addAnnotation<Serializable> {
                addMember("with·=·%T.Serializer::class", enumName)
            }
            addModifiers(PUBLIC, SEALED)
            primaryConstructor {
                addParameter(valueName, valueTypeName)
            }
            addProperty(valueName, valueTypeName, PUBLIC) {
                addKdoc("The raw $valueName used by Discord.")
                initializer(valueName)
            }

            addFunction("equals") {
                addModifiers(FINAL, OVERRIDE)
                returns<Boolean>()
                addParameter<Any?>("other")
                addStatement("return this·===·other || (other·is·%T·&&·this.$valueName·==·other.$valueName)", enumName)
            }

            addFunction("hashCode") {
                addModifiers(FINAL, OVERRIDE)
                returns<Int>()
                addStatement("return $valueName.hashCode()")
            }

            addFunction("toString") {
                addModifiers(FINAL, OVERRIDE)
                returns<String>()
                addStatement("return \"%T.\${this::class.simpleName}($valueName=\$$valueName)\"", enumName)
            }


            addClass("Unknown") {
                addKdoc(
                    "An unknown [%1T].\n\nThis is used as a fallback for [%1T]s that haven't been added to Kord yet.",
                    enumName,
                )
                addModifiers(PUBLIC)
                primaryConstructor {
                    addParameter(valueName, valueTypeName)
                }
                superclass(enumName)
                addSuperclassConstructorParameter(valueName)
            }


            for (entry in entries) {
                addObject(entry.name) {
                    entry.kDoc?.let { addKdoc(it) }
                    @OptIn(DelicateKotlinPoetApi::class) // `AnnotationSpec.get` is ok for `Deprecated`
                    entry.deprecated?.let { addAnnotation(it) }
                    addModifiers(PUBLIC)
                    superclass(enumName)
                    addSuperclassConstructorParameter(valueFormat, entry.value)
                }
            }


            addObject("Serializer") {
                addModifiers(INTERNAL)
                addSuperinterface(K_SERIALIZER.parameterizedBy(enumName))

                addProperty<SerialDescriptor>("descriptor", OVERRIDE) {
                    initializer(
                        "%M(%S, %T)",
                        PRIMITIVE_SERIAL_DESCRIPTOR,
                        enumName.canonicalName,
                        valueType.toPrimitiveKind(),
                    )
                }

                addFunction("serialize") {
                    addModifiers(OVERRIDE)
                    addParameter<Encoder>("encoder")
                    addParameter("value", enumName)
                    addStatement("encoder.encode$encodingPostfix(value.$valueName)")
                }

                addFunction("deserialize") {
                    addModifiers(OVERRIDE)
                    returns(enumName)
                    addParameter<Decoder>("decoder")
                    withControlFlow("return when·(val·$valueName·=·decoder.decode$encodingPostfix())") {
                        for (entry in relevantEntriesForSerializerAndCompanion) {
                            addStatement("$valueFormat·->·${entry.warningSuppressedName}", entry.value)
                        }
                        addStatement("else·->·Unknown($valueName)")
                    }
                }
            }


            addCompanionObject {
                addModifiers(PUBLIC)

                addProperty("entries", LIST.parameterizedBy(enumName), PUBLIC) {
                    addKdoc("A [List] of all known [%T]s.", enumName)
                    delegate {
                        withControlFlow("lazy(mode·=·%M)", PUBLICATION.asMemberName()) {
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
            }
        }
    }
}
