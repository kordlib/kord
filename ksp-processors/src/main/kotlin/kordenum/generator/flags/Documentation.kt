package dev.kord.ksp.kordenum.generator.flags

import com.squareup.kotlinpoet.*
import dev.kord.ksp.kordenum.KordEnum
import dev.kord.ksp.kordenum.ProcessingContext

private val copyMethod = MemberName("dev.kord.common.entity.flags", "copy")

/**
 * Template doc string with following variables
 * - `1L` Collection class name as a literal
 * - `2T` enum type name
 * - `3T` collection class name,
 * - `4T` 1 th place-holder enum value
 * - `5T` 2 th place-holder enum value
 * - `6T` 3 th place-holder enum value
 * - `7M` reference to BitFlags.copy() method
 * - `8T` reference to Unknown class
 * - `9T` reference to Builder class
 * - `%O` typical name of an object having this kind of flags
 * - `%F` name of flags field on `%O`
 * - `%A` article for flags name
 * - `%N` lowercase flags name
 *
 * @see addFlagsDoc
 */
private val docString = """
 Convenience container of multiple [%1L][%2T] which can be combined into one.
 
 ## Creating a collection of message flags
 You can create an [%3T] object using the following methods
 ```kotlin
 // From flags
 val flags1 = %3T(%4T, %5T)
 // From an iterable
 val flags2 = %3T(listOf(%4T, %5T))
 // Using a builder
 val flags3 = %3T {
  +%4T
  -%5T
 }
 ```
 
 ## Modifying existing %Ns
 You can crate a modified copy of a [%3T] instance using the [%7M] method
 
 ```kotlin
 flags.copy {
  +%4T
 }
 ```
 
 ## Mathematical operators
 All [%3T] objects can use +/- operators
 
 ```kotlin
 val flags = %3T(%4T)
 val flags2 = flags + %5T
 val otherFlags = flags - %6T
 val flags3 = flags + otherFlags
 ```
 
 ## Checking for %A %N
 You can use the [contains] operator to check whether a collection contains a specific flag
 ```kotlin
 val hasFlag = %4T in %O.%F
 val hasFlags = %2T(%5T, %6T)·in·%O.%F
 ```
 
 ## Unknown %N
 
 Whenever a newly added flag has not been added to Kord yet it will get deserialized as [%8T].
 You can also use that to check for an yet unsupported flag
 ```kotlin
 val hasFlags = %8T(1 shl 69) in %O.%F
 ```
 @see %2T
 @see %9T
 @property code numeric value of all [%3T]s
""".trimIndent()

context(KordEnum, ProcessingContext, FileSpec.Builder)
internal fun TypeSpec.Builder.addFlagsDoc(collectionName: ClassName, builderName: ClassName) {
    val possibleValues = entries.map { enumName.nestedClass(it.name) }
    val unknown = enumName.nestedClass("Unknown")
    val withReplacedVariables = docString
        .replace("%O", flagsDescriptor.objectName)
        .replace("%F", flagsDescriptor.fieldName)
        .replace("%A", flagsDescriptor.article)
        .replace("%N", flagsDescriptor.name)
    addKdoc(
        CodeBlock.of(
            withReplacedVariables,
            collectionName.simpleName, // %1L
            enumName, // %2T
            collectionName, // %3T
            possibleValues.getSafe(0), // %4T
            possibleValues.getSafe(1), // %5T
            possibleValues.getSafe(1), // %6T
            copyMethod, // %7M
            unknown, // %8T
            builderName, // %9T
        )
    )
}

private fun <T> List<T>.getSafe(index: Int) = get(index.coerceAtMost(lastIndex))
