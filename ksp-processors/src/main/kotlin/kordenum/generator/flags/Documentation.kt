package dev.kord.ksp.kordenum.generator.flags

import com.squareup.kotlinpoet.*
import dev.kord.ksp.kordenum.KordEnum
import dev.kord.ksp.kordenum.ProcessingContext

private val COPY_METHOD = MemberName("dev.kord.common.entity.flags", "copy")

private val docString = """
 Convenience container of multiple [%L][%T] which can be combined into one.
 
 ## Creating a collection of message flags
 You can create an [%T] object using the following methods
 ```kotlin
 // From flags
 val flags1 = %T(%T, %T)
 // From an iterable
 val flags2 = %T(listOf(%T, %T))
 // Using a builder
 val flags3 = %T {
  +%T
  -%T
 }
 ```
 
 ## Modifying existing flags
 You can crate a modified copy of a [%T] instance using the [%M] method
 
 ```kotlin
 flags.copy {
  +%T
 }
 ```
 
 ## Mathematical operators
 All [%T] objects can use +/- operators
 
 ```kotlin
 val flags = %T(%T)
 val flags2 = flags + %T
 val otherFlags = flags - %T
 val flags3 = flags + otherFlags
 ```
 
 ## Checking for a flag
 You can use the [contains] operator to check whether a collection contains a specific flag
 ```kotlin
 val hasFlag = %T in %O.%F
 val hasFlags = %T(%T, %T)·in·%O.%F
 ```
 
 ## Unknown flag
 
 Whenever a newly added flag has not been added to Kord yet it will get deserialized as [%T].
 You can also use that to check for an yet unsupported flag
 ```kotlin
 val hasFlags = %T(1 shl 69) in %O.%F
 ```
 @see %T
 @see %T
 @property code numeric value of all [%T]s
""".trimIndent()

context(KordEnum, ProcessingContext, FileSpec.Builder)
internal fun TypeSpec.Builder.addFlagsDoc(collectionName: ClassName, builderName: ClassName) {
    val possibleValues = entries.map { enumName.nestedClass(it.name) }
    val unknown = enumName.nestedClass("Unknown")
    addKdoc(
        CodeBlock.of(
            docString.replace("%O", flagsDescriptor.objectName).replace("%F", flagsDescriptor.fieldName),
            collectionName.simpleName,
            enumName,
            collectionName,
            collectionName,
            possibleValues.getSafe(0),
            possibleValues.getSafe(1),
            collectionName,
            possibleValues.getSafe(0),
            possibleValues.getSafe(1),
            collectionName,
            possibleValues.getSafe(0),
            possibleValues.getSafe(1),
            collectionName,
            COPY_METHOD,
            possibleValues.getSafe(0),
            collectionName,
            collectionName,
            possibleValues.getSafe(0),
            possibleValues.getSafe(1),
            possibleValues.getSafe(2),
            possibleValues.getSafe(0),
            collectionName,
            possibleValues.getSafe(3),
            possibleValues.getSafe(4),
            unknown,
            unknown,
            enumName,
            builderName,
            enumName
        )
    )
}

private fun <T> List<T>.getSafe(index: Int) = get(index.coerceAtMost(lastIndex))
