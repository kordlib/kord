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
import com.squareup.kotlinpoet.INT as INT_CLASS_NAME
import com.squareup.kotlinpoet.SET as SET_CLASS_NAME
import com.squareup.kotlinpoet.STRING as STRING_CLASS_NAME

private val PRIMITIVE_SERIAL_DESCRIPTOR = MemberName("kotlinx.serialization.descriptors", "PrimitiveSerialDescriptor")
private val KORD_EXPERIMENTAL = ClassName("dev.kord.common.annotation", "KordExperimental")
private val K_SERIALIZER = KSerializer::class.asClassName()

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

private fun ValuesPropertyType.toClassName() = when (this) {
    NONE -> error("did not expect $this")
    SET -> SET_CLASS_NAME
}

private fun ValuesPropertyType.toFromListConversion() = when (this) {
    NONE -> error("did not expect $this")
    SET -> ".toSet()"
}

internal fun KordEnum.generateFileSpec(originatingFile: KSFile): FileSpec {

    val packageName = originatingFile.packageName.asString()
    val enumName = ClassName(packageName, name)
    val valueTypeName = valueType.toClassName()
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

    // TODO remove eventually (always use "Serializer" then)
    val internalSerializerName = if (deprecatedSerializerName == "Serializer") "NewSerializer" else "Serializer"

    return FileSpec(packageName, fileName = name) {
        indent("    ")
        addFileComment("THIS FILE IS AUTO-GENERATED BY KordEnumProcessor.kt, DO NOT EDIT!")

        @OptIn(DelicateKotlinPoetApi::class) // `AnnotationSpec.get` is ok for `Suppress`
        addAnnotation(Suppress("RedundantVisibilityModifier", "IncorrectFormatting", "ReplaceArrayOfWithLiteral"))

        addClass(enumName) {

            // for ksp incremental processing
            addOriginatingKSFile(originatingFile)

            kDoc?.let { addKdoc(it) }
            addAnnotation<Serializable> {
                addMember("with·=·%T.$internalSerializerName::class", enumName)
            }
            addModifiers(PUBLIC, SEALED)
            primaryConstructor {
                addParameter(valueName, valueTypeName)
            }
            addProperty(valueName, valueTypeName, PUBLIC) {
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

            // TODO for all value types
            if (valueType == STRING) addFunction("toString") {
                addModifiers(FINAL, OVERRIDE)
                returns<String>()
                addStatement("return \"%T($valueName=\$$valueName)\"", enumName)
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


            fun TypeSpec.Builder.entry(entry: Entry) {
                entry.kDoc?.let { addKdoc(it) }
                if (entry.isKordExperimental) addAnnotation(KORD_EXPERIMENTAL)
                addModifiers(PUBLIC)
                superclass(enumName)
                addSuperclassConstructorParameter(valueFormat, entry.value)
            }

            for (entry in entries) {
                addObject(entry.name) {
                    entry(entry)
                }
            }

            for (entry in deprecatedEntries) {
                addObject(entry.name) {
                    entry(entry)
                    @OptIn(DelicateKotlinPoetApi::class) // `AnnotationSpec.get` is ok for `Deprecated`
                    addAnnotation(Deprecated(entry.deprecationMessage, entry.replaceWith, entry.deprecationLevel))
                }
            }


            addObject(internalSerializerName) {
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
                    addStatement("return encoder.encode$encodingPostfix(value.$valueName)")
                }

                addFunction("deserialize") {
                    addModifiers(OVERRIDE)
                    addParameter<Decoder>("decoder")
                    withControlFlow("return when·(val·$valueName·=·decoder.decode$encodingPostfix())") {
                        for (entry in relevantEntriesForSerializerAndCompanion) {
                            addStatement("$valueFormat·->·${entry.warningSuppressedName}", entry.value)
                        }
                        addStatement("else·->·Unknown($valueName)")
                    }
                }
            }


            // TODO bump deprecation level and remove eventually
            @OptIn(DelicateKotlinPoetApi::class)
            if (deprecatedSerializerName != null) {
                val name = this@generateFileSpec.name
                val deprecatedAnnotation = Deprecated(
                    "Use '$name.serializer()' instead.",
                    ReplaceWith("$name.serializer()", "$packageName.$name"),
                    level = WARNING,
                )
                val kSerializer = K_SERIALIZER.parameterizedBy(enumName)

                addObject(deprecatedSerializerName) {
                    addAnnotation(deprecatedAnnotation)
                    addModifiers(PUBLIC)
                    addSuperinterface(kSerializer, delegate = CodeBlock.of(internalSerializerName))

                    addFunction("serializer") {
                        addAnnotation(deprecatedAnnotation)
                        addModifiers(PUBLIC)
                        returns(kSerializer)
                        addStatement("return this")
                    }
                }
            }


            addCompanionObject {
                addModifiers(PUBLIC)

                addProperty("entries", LIST.parameterizedBy(enumName), PUBLIC) {
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

                // TODO bump deprecation level and remove eventually
                if (valuesPropertyName != null) {
                    addProperty(
                        valuesPropertyName,
                        valuesPropertyType.toClassName().parameterizedBy(enumName),
                        PUBLIC,
                    ) {
                        @OptIn(DelicateKotlinPoetApi::class) // `AnnotationSpec.get` is ok for `Deprecated`
                        addAnnotation(
                            Deprecated(
                                "Renamed to 'entries'.",
                                ReplaceWith("this.entries", imports = emptyArray()),
                                level = WARNING,
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
                    addProperty(deprecatedSerializerName, deprecatedSerializer, PUBLIC) {
                        addAnnotation(Suppress("DEPRECATION"))
                        addAnnotation(Deprecated("Binary compatibility", level = HIDDEN))
                        addAnnotation(JvmField())
                        initializer("%T", deprecatedSerializer)
                    }
                }
            }
        }
    }
}
