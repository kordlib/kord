package dev.kord.ksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import kotlin.reflect.KProperty1

internal inline fun <reified A : Annotation> Resolver.getSymbolsWithAnnotation(inDepth: Boolean = false) =
    getSymbolsWithAnnotation(A::class.qualifiedName!!, inDepth)

internal inline fun <reified A : Annotation> KSAnnotation.isOfType() = shortName.asString() == A::class.simpleName!!
        && annotationType.resolve().declaration.qualifiedName?.asString() == A::class.qualifiedName!!

internal class AnnotationArguments private constructor(private val map: Map<String, Any>) {
    internal operator fun get(parameter: KProperty1<out Annotation, Any>) = map[parameter.name]

    internal companion object {
        internal val KSAnnotation.annotationArguments
            get() = AnnotationArguments(arguments.associate { it.name!!.getShortName() to it.value!! })
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
