package dev.kord.ksp.generation.bitflags

import com.squareup.kotlinpoet.DelicateKotlinPoetApi
import com.squareup.kotlinpoet.KModifier.OPERATOR
import com.squareup.kotlinpoet.KModifier.PUBLIC
import com.squareup.kotlinpoet.TypeSpec
import dev.kord.ksp.addAnnotation
import dev.kord.ksp.addFunction
import dev.kord.ksp.addParameter
import dev.kord.ksp.generation.GenerationEntity.BitFlags
import dev.kord.ksp.generation.shared.GenerationContext

// TODO bump LEVEL and remove this file eventually
private val LEVEL = DeprecationLevel.ERROR

context(BitFlags, GenerationContext)
@OptIn(DelicateKotlinPoetApi::class)
internal fun TypeSpec.Builder.addDeprecatedDataClassArtifacts() {
    addFunction("component1") {
        addKdoc("@suppress")
        addAnnotation(
            Deprecated(
                "${collectionCN.simpleName} is no longer a data class.",
                ReplaceWith("this.$valueName", imports = emptyArray()),
                LEVEL,
            )
        )
        addModifiers(PUBLIC, OPERATOR)
        returns(valueCN)
        addStatement("return $valueName")
    }
    addFunction("copy") {
        addKdoc("@suppress")
        addAnnotation(Suppress("DeprecatedCallableAddReplaceWith"))
        addAnnotation(
            Deprecated(
                "${collectionCN.simpleName} is no longer a data class. Deprecated without a replacement.",
                level = LEVEL,
            )
        )
        addParameter(valueName, valueCN) {
            defaultValue("this.$valueName")
        }
        returns(collectionCN)
        addStatement("return %T($valueName)", collectionCN)
    }
}
