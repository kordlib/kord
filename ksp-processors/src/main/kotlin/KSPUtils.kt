package dev.kord.ksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotation
import kotlin.reflect.KProperty1

internal inline fun <reified A : Annotation> Resolver.getSymbolsWithAnnotation(inDepth: Boolean = false) =
    getSymbolsWithAnnotation(A::class.qualifiedName!!, inDepth)

internal inline fun <reified A : Annotation> KSAnnotation.isOfType() = shortName.asString() == A::class.simpleName!!
        && annotationType.resolve().declaration.qualifiedName?.asString() == A::class.qualifiedName!!

internal fun KSAnnotation.argumentsToMap() = arguments.associate { it.name!!.getShortName() to it.value!! }

internal operator fun Map<String, Any>.get(parameter: KProperty1<*, *>) = get(parameter.name)
