package dev.kord.ksp.generation

import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.KModifier.*
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import dev.kord.ksp.*
import dev.kord.ksp.generation.GenerationEntity.*
import dev.kord.ksp.generation.generator.enum.addKordEnum
import dev.kord.ksp.generation.generator.flags.addBitFlags
import dev.kord.ksp.generation.generator.addCompanionObject
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlin.DeprecationLevel.*

internal val PRIMITIVE_SERIAL_DESCRIPTOR = MemberName("kotlinx.serialization.descriptors", "PrimitiveSerialDescriptor")
internal val KORD_UNSAFE = ClassName("dev.kord.common.annotation", "KordUnsafe")
internal val K_SERIALIZER = KSerializer::class.asClassName()
internal val DISCORD_BIT_SET = ClassName("dev.kord.common", "DiscordBitSet")
internal val OPT_IN = ClassName("kotlin", "OptIn")

internal fun BitFlags.ValueType.defaultParameter(): CodeBlock {
    val (code, value) = defaultParameterBlock()

    return CodeBlock.of(code, value)
}

internal fun BitFlags.ValueType.defaultParameterBlock() = when (this) {
    BitFlags.ValueType.INT -> "%L" to 0
    BitFlags.ValueType.BIT_SET -> "%M()" to MemberName("dev.kord.common", "EmptyBitSet")
}

internal val Entry.warningSuppressedName
    get() = when (deprecated?.level) {
        null -> name
        WARNING -> """@Suppress("DEPRECATION")·$name"""
        ERROR, HIDDEN -> """@Suppress("DEPRECATION_ERROR")·$name"""
    }

internal fun ValueType.toClassName() = when (this) {
    KordEnum.ValueType.INT, BitFlags.ValueType.INT -> INT
    KordEnum.ValueType.STRING -> STRING
    BitFlags.ValueType.BIT_SET -> DISCORD_BIT_SET
}

internal fun KordEnum.ValueType.toEncodingPostfix() = when (this) {
    KordEnum.ValueType.INT -> "Int"
    KordEnum.ValueType.STRING -> "String"
}

internal fun ValueType.toFormat() = when (this) {
    KordEnum.ValueType.INT, BitFlags.ValueType.INT -> "%L"
    KordEnum.ValueType.STRING -> "%S"
    BitFlags.ValueType.BIT_SET -> "%LL"
}

internal fun ValueType.toPrimitiveKind() = when (this) {
    KordEnum.ValueType.INT, BitFlags.ValueType.INT -> PrimitiveKind.INT::class
    KordEnum.ValueType.STRING, BitFlags.ValueType.BIT_SET -> PrimitiveKind.STRING::class
}

internal fun GenerationEntity.generateFileSpec(originatingFile: KSFile): FileSpec {

    val packageName = originatingFile.packageName.asString()
    val entityCN = ClassName(packageName, name)
    val valueCN = valueType.toClassName()

    val valueFormat = valueType.toFormat()

    val relevantEntriesForSerializerAndCompanion = entries
        .groupBy { it.value } // one entry per unique value is relevant
        .map { (_, group) -> group.firstOrNull { it.deprecated == null } ?: group.first() }

    val context =
        ProcessingContext(packageName, entityCN, valueCN, valueFormat, relevantEntriesForSerializerAndCompanion)

    return with(context) {
        fileSpecGeneratedFrom<GenerationProcessor>(entityCN) {
            addClass(entityCN) {
                // for ksp incremental processing
                addOriginatingKSFile(originatingFile)

                when (this@generateFileSpec) {
                    is KordEnum -> addKordEnum()
                    is BitFlags -> addBitFlags()
                }
            }
        }
    }
}

context(GenerationEntity, ProcessingContext, FileSpec.Builder)
internal inline fun TypeSpec.Builder.addEntity(builder: TypeSpecBuilder) {
    additionalImports.forEach {
        val import = ClassName.bestGuess(it)
        addImport(import.packageName, import.simpleName)
    }

    run {
        val docLink = "See [%T]s in the [Discord·Developer·Documentation]($docUrl)."
        val combinedKDocFormat = if (kDoc != null) "$kDoc\n\n$docLink" else docLink
        addKdoc(combinedKDocFormat, entityCN)
    }

    addModifiers(PUBLIC, SEALED)
    primaryConstructor {
        addParameter(valueName, valueCN)
    }
    addProperty(valueName, valueCN, PUBLIC) {
        addKdoc("The raw $valueName used by Discord.")
        initializer(valueName)
    }

    addClass("Unknown") {
        addKdoc(
            "An unknown [%1T].\n\nThis is used as a fallback for [%1T]s that haven't been added to Kord yet.",
            entityCN,
        )
        addModifiers(PUBLIC)
        primaryConstructor {
            addAnnotation(KORD_UNSAFE)
            addParameter(valueName, valueCN)
        }
        if (valueType == BitFlags.ValueType.BIT_SET) {
            addConstructor {
                addAnnotation(KORD_UNSAFE)
                addParameter(valueName, LONG, VARARG)
                callThisConstructor(CodeBlock.of("%T($valueName)", DISCORD_BIT_SET))
            }
        }
        superclass(entityCN)
        addSuperclassConstructorParameter(valueName)
    }

    addEqualsAndHashCode(entityCN, FINAL)

    addFunction("toString") {
        addModifiers(FINAL, OVERRIDE)
        returns<String>()
        addStatement("return \"%T.\${this::class.simpleName}($valueName=\$$valueName)\"", entityCN)
    }

    for (entry in entries) {
        addObject(entry.name) {
            entry.kDoc?.let { addKdoc(it) }
            addAnnotations(entry.additionalOptInMarkerAnnotations.map { AnnotationSpec.builder(ClassName.bestGuess(it)).build() })
            @OptIn(DelicateKotlinPoetApi::class) // `AnnotationSpec.get` is ok for `Deprecated`
            entry.deprecated?.let { addAnnotation(it) }
            addModifiers(PUBLIC)
            superclass(entityCN)
            addSuperclassConstructorParameter(valueFormat, entry.value)
        }
    }

    if (this@GenerationEntity is BitFlags && this@GenerationEntity.hasCombinerFlag) {
        addObject("All") {
            addKdoc("A combination of all [%T]s", entityCN)
            addModifiers(PUBLIC)
            superclass(entityCN)
            addSuperclassConstructorParameter("buildAll()")
        }
    }

    builder()
    addCompanionObject()
}

context (GenerationEntity, ProcessingContext)
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
