package dev.kord.ksp

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.MemberName.Companion.member
import kotlin.reflect.KClass

// standalone

internal inline fun fileSpec(packageName: String, fileName: String, builder: FileSpec.Builder.() -> Unit) =
    FileSpec.builder(packageName, fileName).apply(builder).build()

internal inline fun <reified A : Annotation> annotationSpec(builder: AnnotationSpec.Builder.() -> Unit) =
    AnnotationSpec.builder(A::class).apply(builder).build()


// FileSpec.Builder

internal inline fun FileSpec.Builder.addClass(className: ClassName, builder: TypeSpec.Builder.() -> Unit) =
    addType(TypeSpec.classBuilder(className).apply(builder).build())

internal inline fun <reified A : Annotation> FileSpec.Builder.addAnnotation(
    builder: AnnotationSpec.Builder.() -> Unit,
) = addAnnotation(AnnotationSpec.builder(A::class).apply(builder).build())


// TypeSpec.Builder

internal inline fun <reified A : Annotation> TypeSpec.Builder.addAnnotation(
    builder: AnnotationSpec.Builder.() -> Unit,
) = addAnnotation(AnnotationSpec.Companion.builder(A::class).apply(builder).build())

internal inline fun TypeSpec.Builder.primaryConstructor(builder: FunSpec.Builder.() -> Unit) =
    primaryConstructor(FunSpec.constructorBuilder().apply(builder).build())

internal inline fun TypeSpec.Builder.addProperty(
    name: String,
    type: KClass<*>,
    vararg modifiers: KModifier,
    builder: PropertySpec.Builder.() -> Unit,
) = addProperty(PropertySpec.builder(name, type, *modifiers).apply(builder).build())

internal inline fun <reified T> TypeSpec.Builder.addProperty(
    name: String,
    vararg modifiers: KModifier,
    builder: PropertySpec.Builder.() -> Unit,
) = addProperty(PropertySpec.builder(name, typeNameOf<T>(), *modifiers).apply(builder).build())

internal inline fun TypeSpec.Builder.addProperty(
    name: String,
    type: TypeName,
    vararg modifiers: KModifier,
    builder: PropertySpec.Builder.() -> Unit,
) = addProperty(PropertySpec.builder(name, type, *modifiers).apply(builder).build())

internal inline fun TypeSpec.Builder.addFunction(name: String, builder: FunSpec.Builder.() -> Unit) =
    addFunction(FunSpec.builder(name).apply(builder).build())

internal inline fun TypeSpec.Builder.addClass(name: String, builder: TypeSpec.Builder.() -> Unit) =
    addType(TypeSpec.classBuilder(name).apply(builder).build())

internal inline fun TypeSpec.Builder.addObject(name: String, builder: TypeSpec.Builder.() -> Unit) =
    addType(TypeSpec.objectBuilder(name).apply(builder).build())

internal inline fun TypeSpec.Builder.addCompanionObject(name: String? = null, builder: TypeSpec.Builder.() -> Unit) =
    addType(TypeSpec.companionObjectBuilder(name).apply(builder).build())


// FunSpec.Builder

internal inline fun <reified T> FunSpec.Builder.returns() = returns(typeNameOf<T>())

internal inline fun <reified T> FunSpec.Builder.addParameter(name: String) = addParameter(name, typeNameOf<T>())


// PropertySpec.Builder

internal inline fun PropertySpec.Builder.delegate(builder: CodeBlock.Builder.() -> Unit) =
    delegate(CodeBlock.builder().apply(builder).build())


// other

internal inline fun <reified E : Enum<E>> E.asMemberName() = E::class.member(name)
