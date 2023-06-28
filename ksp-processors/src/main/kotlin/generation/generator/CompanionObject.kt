package dev.kord.ksp.generation.generator

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import dev.kord.ksp.*
import dev.kord.ksp.generation.*
import dev.kord.ksp.generation.GenerationEntity.BitFlags

context(GenerationEntity, ProcessingContext, FileSpec.Builder)
fun TypeSpec.Builder.addCompanionObject() = addCompanionObject {
    addModifiers(KModifier.PUBLIC)

    val additionalOptIns = entries.flatMap { it.additionalOptInMarkerAnnotations }
        .toSet()
        .map(ClassName::bestGuess)

    fun PropertySpec.Builder.addOptIns() {
        if (additionalOptIns.isNotEmpty()) {
            addAnnotation(OPT_IN) {
                val code = additionalOptIns
                    .map { CodeBlock.of("%T::class", it) }
                    .joinToCode()
                addMember(code)
            }
        }
    }

    addProperty("entries", LIST.parameterizedBy(entityCN), KModifier.PUBLIC) {
        addKdoc("A [List] of all known [%T]s.", entityCN)
        addOptIns()
        delegate {
            withControlFlow("lazy(mode·=·%M)", LazyThreadSafetyMode.PUBLICATION.asMemberName()) {
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

    if (this@GenerationEntity is BitFlags && this@GenerationEntity.hasCombinerFlag) {
        addFunction("buildAll") {
            addComment("""We cannot inline this into the "All" object, because that causes a weird compiler bug""")
            addModifiers(KModifier.PRIVATE)
            returns(valueCN)

            val (code, parameter) = this@GenerationEntity.valueType.defaultParameterBlock()
            addCode("""
                    return entries.fold($code)·{·acc,·value·->
                    ⇥ acc + value.$valueName
                    ⇤}
                """.trimIndent(), parameter)
        }
    }
}
