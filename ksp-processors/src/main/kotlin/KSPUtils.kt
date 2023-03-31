package dev.kord.ksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.*
import kotlin.reflect.KProperty1

internal inline fun <reified A : Annotation> Resolver.getSymbolsWithAnnotation(inDepth: Boolean = false) =
    getSymbolsWithAnnotation(A::class.qualifiedName!!, inDepth)

internal fun Resolver.getNewClasses() = getNewFiles().flatMap { it.declarations.filterIsInstance<KSClassDeclaration>() }

internal inline fun <reified A : Annotation> KSAnnotation.isOfType() = isOfType(A::class.qualifiedName!!)

internal fun KSAnnotation.isOfType(qualifiedName: String) =
    shortName.asString() == qualifiedName.substringAfterLast('.')
        && annotationType.resolve().declaration.qualifiedName?.asString() == qualifiedName

internal class AnnotationArguments private constructor(private val map: Map<String, Any?>) {
    private inline fun <reified V> get(parameter: KProperty1<out Annotation, V>) = map[parameter.name] as V?

    internal inline fun <reified V>  getSafe(parameter: KProperty1<out Annotation, V>) = get(parameter) ?: error("Missing required parameter: $parameter")
    // https://github.com/google/ksp/issues/885
    internal inline fun <reified V>  getOrDefault(parameter: KProperty1<out Annotation, V>, defaultValue: V) = get(parameter) ?: defaultValue

    internal fun getRaw(parameter: KProperty1<out Annotation, Any>) = map[parameter.name]

    internal companion object {
        internal val KSAnnotation.annotationArguments: AnnotationArguments
            get() {
                return AnnotationArguments(arguments
                    .associate { it.name!!.getShortName() to it.value })
            }
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
