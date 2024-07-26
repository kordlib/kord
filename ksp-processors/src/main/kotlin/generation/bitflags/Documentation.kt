package dev.kord.ksp.generation.bitflags

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.TypeSpec
import dev.kord.ksp.generation.GenerationEntity.BitFlags
import dev.kord.ksp.generation.shared.GenerationContext

context(BitFlags, GenerationContext)
internal fun TypeSpec.Builder.addCollectionKDoc() = addKdoc(
    docStringFormat,
    entityCN, // %1T: flag ClassName
    collectionCN, // %2T: collection ClassName
    entityCN.nestedClass(entriesDistinctByValue[0].name), // %3T: entry 0 ClassName
    // %4L: CodeBlock with entry 1 ClassName or `Flag.fromShift(22)`
    entriesDistinctByValue.getOrNull(1)?.let { CodeBlock.of("%T", entityCN.nestedClass(it.name)) }
        ?: CodeBlock.of("%T.fromShift(22)", entityCN),
    entityCN.nestedClass("Unknown"), // %5T: Unknown ClassName
    builderCN, // %6T: Builder ClassName
)

context(GenerationContext)
private val BitFlags.docStringFormat: String
    get() {
        val collection = collectionCN.simpleName.replaceFirstChar(Char::lowercase)
        return """
            A collection of multiple [%1T]s.
            
            ##·Creating·an·instance·of·[%2T]
            
            You can create an instance of [%2T] using the following methods:
            ```kotlin
            //·from·individual·%1Ts
            val·${collection}1·=·%2T(%3T,·%4L)
            
            //·from·an·Iterable
            val·iterable:·Iterable<%1T>·=·TODO()
            val·${collection}2·=·%2T(iterable)
            
            //·using·a·builder
            val·${collection}3·=·%2T·{
                +${collection}2
                +%3T
                -%4L
            }
            ```
            
            ##·Modifying·an·existing·instance·of·[%2T]
            
            You can create a modified copy of an existing instance of [%2T] using the [copy] method:
            ```kotlin
            $collection.copy·{
                +%3T
            }
            ```
            
            ##·Mathematical·operators
            
            All [%2T] objects can use `+`/`-` operators:
            ```kotlin
            val·${collection}1·=·$collection·+·%3T
            val·${collection}2·=·$collection·-·%4L
            val·${collection}3·=·${collection}1·+·${collection}2
            ```
            
            ##·Checking·for·[%1T]s
            
            You can use the [contains] operator to check whether an instance of [%2T] contains specific [%1T]s:
            ```kotlin
            val·has%1T·=·%3T·in·$collection
            val·has%2T·=·%2T(%3T,·%4L)·in·$collection
            ```
            
            ##·Unknown·[%1T]s
            
            Whenever [%1T]s haven't been added to Kord yet, they will be deserialized as instances of [%5T].
            
            You can also use [%1T.fromShift] to check for [unknown][%5T] [%1T]s.
            ```kotlin
            val·hasUnknown%1T·=·%1T.fromShift(23)·in·$collection
            ```
            
            @see·%1T
            @see·%6T
        """.trimIndent()
    }
