package dev.kord.ksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import kotlin.reflect.KProperty1

internal inline fun <reified A : Annotation> Resolver.getSymbolsWithAnnotation(inDepth: Boolean = false) =
    getSymbolsWithAnnotation(A::class.qualifiedName!!, inDepth)

internal fun Resolver.getNewClasses() = getNewFiles().flatMap { it.declarations.filterIsInstance<KSClassDeclaration>() }

internal inline fun <reified A : Annotation> KSAnnotation.isOfType() = isOfType(A::class.qualifiedName!!)

internal fun KSAnnotation.isOfType(qualifiedName: String) =
    shortName.asString() == qualifiedName.substringAfterLast('.')
        && annotationType.resolve().declaration.qualifiedName?.asString() == qualifiedName

internal class AnnotationArguments private constructor(private val map: Map<String, Any?>) {
    private inline fun <reified V : Any> get(parameter: KProperty1<out Annotation, V>) = map[parameter.name] as V?

    internal inline fun <reified V : Any> getSafe(parameter: KProperty1<out Annotation, V>) =
        get(parameter) ?: error("Missing required parameter: $parameter")

    // https://github.com/google/ksp/issues/885
    internal inline fun <reified V : Any> getOrDefault(parameter: KProperty1<out Annotation, V>, defaultValue: V) =
        get(parameter) ?: defaultValue

    internal fun getRaw(parameter: KProperty1<out Annotation, Any>) = map[parameter.name]

    internal companion object {
        internal val KSAnnotation.annotationArguments
            get() = AnnotationArguments(arguments.associate { it.name!!.getShortName() to it.value })
    }
}

/**
 * The binary name of this class-like declaration on the JVM, as specified by
 * [The Java® Language Specification](https://docs.oracle.com/javase/specs/jls/se8/html/jls-13.html#jls-13.1).
 */
internal val KSClassDeclaration.jvmBinaryName: String
    get() = when (val parent = parentDeclaration) {
        // this is a top-level class-like declaration -> canonical name / fully qualified name (same for top-level)
        null -> this.qualifiedName!!.asString()

        // this is a member class-like declaration -> binary name of immediately enclosing declaration + $ + simple name
        is KSClassDeclaration -> parent.jvmBinaryName + '$' + this.simpleName.asString()

        is KSFunctionDeclaration, is KSPropertyDeclaration -> error(
            "jvmBinaryName isn't implemented for local/anonymous class-like declarations but $this seems to be one"
        )
        else -> error("$this has an unexpected parentDeclaration: $parent")
    }

internal val KSClassDeclaration.companionObject
    get() = declarations.filterIsInstance<KSClassDeclaration>().firstOrNull { it.isCompanionObject }

@Suppress("RecursivePropertyAccessor")
internal val KSReferenceElement.isClassifierReference: Boolean
    get() = when (this) {
        is KSDynamicReference, is KSCallableReference -> false
        is KSClassifierReference -> true
        is KSDefNonNullReference -> enclosedType.isClassifierReference
        is KSParenthesizedReference -> element.isClassifierReference
        else -> error("Unexpected KSReferenceElement: $this")
    }
