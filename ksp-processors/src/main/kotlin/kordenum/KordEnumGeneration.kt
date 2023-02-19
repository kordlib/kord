package dev.kord.ksp.kordenum

import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.KModifier.*
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import dev.kord.ksp.*
import dev.kord.ksp.GenerateKordEnum.ValueType
import dev.kord.ksp.GenerateKordEnum.ValueType.*
import dev.kord.ksp.GenerateKordEnum.ValuesPropertyType
import dev.kord.ksp.GenerateKordEnum.ValuesPropertyType.NONE
import dev.kord.ksp.GenerateKordEnum.ValuesPropertyType.SET
import dev.kord.ksp.kordenum.KordEnum.Entry
import dev.kord.ksp.kordenum.generator.addCompanionObject
import dev.kord.ksp.kordenum.generator.enum.addNormalEnum
import dev.kord.ksp.kordenum.generator.flags.addFlagEnum
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlin.DeprecationLevel.*
import com.squareup.kotlinpoet.INT as INT_CLASS_NAME
import com.squareup.kotlinpoet.SET as SET_CLASS_NAME
import com.squareup.kotlinpoet.STRING as STRING_CLASS_NAME

internal val PRIMITIVE_SERIAL_DESCRIPTOR = MemberName("kotlinx.serialization.descriptors", "PrimitiveSerialDescriptor")
internal val KORD_EXPERIMENTAL = ClassName("dev.kord.common.annotation", "KordExperimental")
internal val KORD_UNSAFE = ClassName("dev.kord.common.annotation", "KordUnsafe")
internal val K_SERIALIZER = KSerializer::class.asClassName()
internal val DISCORD_BIT_SET = ClassName("dev.kord.common", "DiscordBitSet")
internal val OPT_IN = ClassName("kotlin", "OptIn")

internal val Entry.warningSuppressedName
    get() = when {
        isDeprecated -> "@Suppress(\"${
            when (deprecationLevel) {
                WARNING -> "DEPRECATION"
                ERROR, HIDDEN -> "DEPRECATION_ERROR"
            }
        }\")·$name"

        else -> name
    }

internal fun ValueType.toClassName() = when (this) {
    INT -> INT_CLASS_NAME
    STRING -> STRING_CLASS_NAME
    BITSET -> DISCORD_BIT_SET
}

internal fun ValueType.toEncodingPostfix() = when (this) {
    INT -> "Int"
    STRING -> "String"
    BITSET -> "DiscordBitSet"
}

internal fun ValueType.toFormat() = when (this) {
    INT -> "%L"
    BITSET -> "%LL"
    STRING -> "%S"
}

internal fun ValuesPropertyType.toClassName() = when (this) {
    NONE -> error("did not expect $this")
    SET -> SET_CLASS_NAME
}

internal fun ValuesPropertyType.toFromListConversion() = when (this) {
    NONE -> error("did not expect $this")
    SET -> ".toSet()"
}

internal fun ValueType.toPrimitiveKind() = when (this) {
    INT -> PrimitiveKind.INT::class
    STRING, BITSET -> PrimitiveKind.STRING::class
}


internal fun KordEnum.generateFileSpec(originatingFile: KSFile): FileSpec {

    val packageName = originatingFile.packageName.asString()
    val enumName = ClassName(packageName, name)
    val valueTypeName = valueType.toClassName()
    val encodingPostfix = valueType.toEncodingPostfix()
    val valueFormat = valueType.toFormat()

    val relevantEntriesForSerializerAndCompanion = run {
        // don't keep deprecated entries with a non-deprecated replacement
        val nonDeprecated = entries
        val nonDeprecatedValues = nonDeprecated.map { it.value }
        val deprecatedToKeep = deprecatedEntries.filter { it.value !in nonDeprecatedValues }

        // merge nonDeprecated and deprecatedToKeep, preserving their order
        val (result, taken) = nonDeprecated.fold(emptyList<Entry>() to 0) { (acc, taken), entry ->
            val smallerDeprecated = deprecatedToKeep.drop(taken).takeWhile { it < entry }
            (acc + smallerDeprecated + entry) to (taken + smallerDeprecated.size)
        }

        return@run result + deprecatedToKeep.drop(taken) // add all deprecated that weren't taken yet
    }

    val context = ProcessingContext(
        packageName,
        enumName,
        valueTypeName,
        encodingPostfix,
        valueFormat,
        relevantEntriesForSerializerAndCompanion
    )
    return with(context) {
        fileSpecGeneratedFrom<KordEnumProcessor>(packageName, fileName = name) {
            addClass(enumName) {
                // for ksp incremental processing
                addOriginatingKSFile(originatingFile)

                if (!isFlags) {
                    addNormalEnum()
                } else {
                    addFlagEnum()
                }
            }
        }
    }
}

context(KordEnum, ProcessingContext, FileSpec.Builder)
internal inline fun TypeSpec.Builder.addEnum(
    additionalValuePropertyModifiers: Iterable<KModifier> = emptyList(),
    builder: TypeSpecBuilder = {}
) {
    // KDoc for the kord enum
    run {
        val docLink = docUrl?.let { url -> "See [%T]s in the [Discord·Developer·Documentation]($url)." }
        val combinedKDocFormat = when {
            kDoc != null && docLink != null -> "$kDoc\n\n$docLink"
            else -> kDoc ?: docLink
        }
        combinedKDocFormat?.let { format -> addKdoc(format, enumName) }
    }

    addModifiers(PUBLIC, SEALED)
    primaryConstructor {
        addParameter(valueName, valueTypeName)
    }
    addProperty(valueName, valueTypeName, *(additionalValuePropertyModifiers + PUBLIC).toTypedArray()) {
        addKdoc("The raw $valueName used by Discord.")
        initializer(valueName)
    }

    addClass("Unknown") {
        addKdoc(
            "An unknown [%1T].\n\nThis is used as a fallback for [%1T]s that haven't been added to Kord yet.",
            enumName,
        )
        addModifiers(PUBLIC)
        primaryConstructor {
            addAnnotation(KORD_UNSAFE)
            addParameter(valueName, valueTypeName)
        }
        superclass(enumName)
        addSuperclassConstructorParameter(valueName)
    }

    addEqualsAndHashCode(FINAL)

    addFunction("toString") {
        addModifiers(FINAL, OVERRIDE)
        returns<String>()
        addStatement("return \"%T.\${this::class.simpleName}($valueName=\$$valueName)\"", enumName)
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

    builder()
    addCompanionObject()
}

context (ProcessingContext)
private fun TypeSpec.Builder.entry(entry: Entry) {
    entry.kDoc?.let { addKdoc(it) }
    if (entry.isKordExperimental) addAnnotation(KORD_EXPERIMENTAL)
    addModifiers(PUBLIC)
    superclass(enumName)
    addSuperclassConstructorParameter(valueFormat, entry.value)
}

context (KordEnum, ProcessingContext)
internal fun TypeSpec.Builder.addEqualsAndHashCode(vararg additionalParameters: KModifier) {
    addFunction("equals") {
        addModifiers(OVERRIDE, *additionalParameters)
        returns<Boolean>()
        addParameter<Any?>("other")
        addStatement("return this·===·other || (other·is·%T·&&·this.$valueName·==·other.$valueName)", enumName)
    }

    addFunction("hashCode") {
        addModifiers(OVERRIDE, *additionalParameters)
        returns<Int>()
        addStatement("return $valueName.hashCode()")
    }
}
