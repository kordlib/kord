package dev.kord.ksp.generation.generator.flags

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import dev.kord.ksp.generation.GenerationEntity.BitFlags
import dev.kord.ksp.generation.ProcessingContext

/**
 * Template doc string with following variables
 * - `%1L` Collection class name as a literal
 * - `%2T` entity class name
 * - `%3T` collection class name,
 * - `%4T` 1st place-holder entry
 * - `%5T` 2nd place-holder entry
 * - `%6T` 3rd place-holder entry
 * - `%7T` reference to Unknown class
 * - `%8T` reference to Builder class
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
 You can crate a modified copy of a [%3T] instance using the [copy] method
 
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
 
 Whenever a newly added flag has not been added to Kord yet it will get deserialized as [%7T].
 You can also use that to check for an yet unsupported flag
 ```kotlin
 val hasFlags = %7T(1 shl 69) in %O.%F
 ```
 @see %2T
 @see %8T
 @property code numeric value of all [%3T]s
""".trimIndent()

context(BitFlags, ProcessingContext, FileSpec.Builder)
internal fun TypeSpec.Builder.addFlagsDoc(collectionName: ClassName, builderName: ClassName) {
    val possibleValues = entries.map { entityCN.nestedClass(it.name) }
    val unknown = entityCN.nestedClass("Unknown")
    val withReplacedVariables = docString
        .replace("%O", flagsDescriptor.objectName)
        .replace("%F", flagsDescriptor.flagsFieldName)
        .replace("%A", flagsDescriptor.article)
        .replace("%N", flagsDescriptor.name)
    addKdoc(
        CodeBlock.of(
            withReplacedVariables,
            collectionName.simpleName, // %1L
            entityCN, // %2T
            collectionName, // %3T
            possibleValues.getSafe(0), // %4T
            possibleValues.getSafe(1), // %5T
            possibleValues.getSafe(1), // %6T
            unknown, // %7T
            builderName, // %8T
        )
    )
}

private fun <T> List<T>.getSafe(index: Int) = get(index.coerceAtMost(lastIndex))
