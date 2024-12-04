package dev.kord.ksp

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.*

@OptIn(KspExperimental::class)
internal inline fun <reified A : Annotation> KSAnnotated.getAnnotationsByType() = getAnnotationsByType(A::class)
