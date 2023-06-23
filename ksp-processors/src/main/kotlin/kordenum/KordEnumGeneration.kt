package dev.kord.ksp.kordenum

import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.KModifier.*
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import dev.kord.ksp.*
import dev.kord.ksp.GenerateKordEnum.ValueType
import dev.kord.ksp.GenerateKordEnum.ValueType.*
import dev.kord.ksp.kordenum.KordEnum.Entry
import dev.kord.ksp.kordenum.generator.addCompanionObject
import dev.kord.ksp.kordenum.generator.enum.addNormalEnum
import dev.kord.ksp.kordenum.generator.flags.addFlagEnum
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlin.DeprecationLevel.*
import com.squareup.kotlinpoet.INT as INT_CLASS_NAME
import com.squareup.kotlinpoet.STRING as STRING_CLASS_NAME

internal val PRIMITIVE_SERIAL_DESCRIPTOR = MemberName("kotlinx.serialization.descriptors", "PrimitiveSerialDescriptor")
internal val KORD_UNSAFE = ClassName("dev.kord.common.annotation", "KordUnsafe")
internal val K_SERIALIZER = KSerializer::class.asClassName()
internal val DISCORD_BIT_SET = ClassName("dev.kord.common", "DiscordBitSet")
internal val OPT_IN = ClassName("kotlin", "OptIn")

internal fun ValueType.defaultParameter(): CodeBlock {
    val (code, value) = defaultParameterBlock()

    return CodeBlock.of(code, value)
}

internal fun ValueType.defaultParameterBlock() = when (this) {
    INT -> "%L" to 0
    STRING -> "%S" to ""
    BITSET -> "%M()" to MemberName("dev.kord.common", "EmptyBitSet")
}

internal val Entry.warningSuppressedName
    get() = when (deprecated?.level) {
        null -> name
        WARNING -> """@Suppress("DEPRECATION")·$name"""
        ERROR, HIDDEN -> """@Suppress("DEPRECATION_ERROR")·$name"""
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

    val relevantEntriesForSerializerAndCompanion = entries
        .groupBy { it.value } // one entry per unique value is relevant
        .map { (_, group) -> group.firstOrNull { it.deprecated == null } ?: group.first() }

    val context = ProcessingContext(
        packageName,
        enumName,
        valueTypeName,
        encodingPostfix,
        valueFormat,
        relevantEntriesForSerializerAndCompanion
    )

    return with(context) {
        fileSpecGeneratedFrom<KordEnumProcessor>(enumName) {
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
    additionalImports.forEach {
        val import = ClassName.bestGuess(it)
        addImport(import.packageName, import.simpleName)
    }

    // KDoc for the kord enum
    run {
        val docLink = "See [%T]s in the [Discord·Developer·Documentation]($docUrl)."
        val combinedKDocFormat = if (kDoc != null) "$kDoc\n\n$docLink" else docLink
        addKdoc(combinedKDocFormat, enumName)
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
        if (valueType == BITSET) {
            addConstructor {
                addAnnotation(KORD_UNSAFE)
                addParameter(valueName, LONG, VARARG)
                callThisConstructor(CodeBlock.of("%T($valueName)", DISCORD_BIT_SET))
            }
        }
        superclass(enumName)
        addSuperclassConstructorParameter(valueName)
    }

    addEqualsAndHashCode(enumName, FINAL)

    addFunction("toString") {
        addModifiers(FINAL, OVERRIDE)
        returns<String>()
        addStatement("return \"%T.\${this::class.simpleName}($valueName=\$$valueName)\"", enumName)
    }

    for (entry in entries) {
        addObject(entry.name) {
            entry.kDoc?.let { addKdoc(it) }
            addAnnotations(entry.additionalOptInMarkerAnnotations.map { AnnotationSpec.builder(ClassName.bestGuess(it)).build() })
            @OptIn(DelicateKotlinPoetApi::class) // `AnnotationSpec.get` is ok for `Deprecated`
            entry.deprecated?.let { addAnnotation(it) }
            addModifiers(PUBLIC)
            superclass(enumName)
            addSuperclassConstructorParameter(valueFormat, entry.value)
        }
    }

    if (hasCombinerFlag) {
        addObject("All") {
            addKdoc("A combination of all [%T]s", enumName)
            addModifiers(PUBLIC)
            superclass(enumName)
            addSuperclassConstructorParameter("buildAll()")
        }
    }

    builder()
    addCompanionObject()
}

context (KordEnum, ProcessingContext)
internal fun TypeSpec.Builder.addEqualsAndHashCode(className: ClassName, vararg additionalParameters: KModifier) {
    addFunction("equals") {
        addModifiers(OVERRIDE, *additionalParameters)
        returns<Boolean>()
        addParameter<Any?>("other")
        addStatement("return this·===·other || (other·is·%T·&&·this.$valueName·==·other.$valueName)", className)
    }

    addFunction("hashCode") {
        addModifiers(OVERRIDE, *additionalParameters)
        returns<Int>()
        addStatement("return $valueName.hashCode()")
    }
}
