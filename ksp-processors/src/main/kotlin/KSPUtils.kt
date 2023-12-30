package dev.kord.ksp

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.findActualType
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.isDefault
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.*
import kotlin.reflect.KProperty1

internal inline fun <reified A : Annotation> Resolver.getSymbolsWithAnnotation(inDepth: Boolean = false) =
    getSymbolsWithAnnotation(A::class.qualifiedName!!, inDepth)

internal fun Resolver.getNewClasses() = getNewFiles().flatMap { it.declarations.filterIsInstance<KSClassDeclaration>() }

internal inline fun <reified A : Annotation> KSAnnotation.isOfType() = isOfType(A::class.qualifiedName!!)

internal fun KSAnnotation.isOfType(qualifiedName: String) = annotationType.resolve()
    .declaration.let { if (it is KSTypeAlias) it.findActualType() else it }
    .qualifiedName?.asString() == qualifiedName

@OptIn(KspExperimental::class)
internal inline fun <reified A : Annotation> KSAnnotated.getAnnotationsByType() = getAnnotationsByType(A::class)

internal class AnnotationArguments<A : Annotation> private constructor(
    private val arguments: Map<String, KSValueArgument>,
) {
    private fun getArgument(parameter: KProperty1<A, Any>) = arguments.getValue(parameter.name)
    private val KProperty1<A, Any>.value get() = getArgument(this).value

    fun isDefault(parameter: KProperty1<A, Any>) = getArgument(parameter).isDefault()

    // can't return non-nullable values because of https://github.com/google/ksp/issues/885
    operator fun get(parameter: KProperty1<A, Annotation>) = parameter.value as KSAnnotation?
    operator fun get(parameter: KProperty1<A, Array<out Annotation>>) =
        @Suppress("UNCHECKED_CAST") (parameter.value as List<KSAnnotation>?)

    companion object {
        fun <A : Annotation> KSAnnotation.arguments() =
            AnnotationArguments<A>(arguments.associateBy { it.name!!.asString() })
    }
}

@Suppress("RecursivePropertyAccessor")
internal val KSReferenceElement.isClassifierReference: Boolean
    get() = when (this) {
        is KSDynamicReference, is KSCallableReference -> false
        is KSClassifierReference -> true
        is KSDefNonNullReference -> enclosedType.isClassifierReference
        is KSParenthesizedReference -> element.isClassifierReference
        else -> error("Unexpected KSReferenceElement: $this")
    }
