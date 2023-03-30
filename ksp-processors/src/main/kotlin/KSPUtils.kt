package dev.kord.ksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.*
import kotlin.reflect.KProperty1

internal inline fun <reified A : Annotation> Resolver.getSymbolsWithAnnotation(inDepth: Boolean = false) =
    getSymbolsWithAnnotation(A::class.qualifiedName!!, inDepth)

internal fun Resolver.getNewClasses() = getNewFiles()
    .flatMap { it.declarations.filterIsInstance<KSClassDeclaration>() }

internal inline fun <reified A : Annotation> KSAnnotation.isOfType() =
    isOfType(A::class.qualifiedName!!.substringAfterLast('.'), A::class.simpleName!!)

internal fun KSAnnotation.isOfType(qualifier: String, simpleName: String) = shortName.asString() == simpleName
        && annotationType.resolve().declaration.qualifiedName?.asString() == "$qualifier.$simpleName"

internal class AnnotationArguments private constructor(private val map: Map<String, Any>) {
    internal operator fun get(parameter: KProperty1<out Annotation, Any>) = map[parameter.name]

    internal companion object {
        internal val KSAnnotation.annotationArguments
            get() = AnnotationArguments(arguments.associate { it.name!!.getShortName() to it.value!! })
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
