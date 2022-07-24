package dev.kord.ksp

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.MemberName.Companion.member

// standalone

internal inline fun FileSpec(packageName: String, fileName: String, builder: FileSpec.Builder.() -> Unit) =
    FileSpec.builder(packageName, fileName).apply(builder).build()


// FileSpec.Builder

internal inline fun FileSpec.Builder.addClass(className: ClassName, builder: TypeSpec.Builder.() -> Unit) =
    addType(TypeSpec.classBuilder(className).apply(builder).build())

@DelicateKotlinPoetApi("See 'AnnotationSpec.get'")
internal fun FileSpec.Builder.addAnnotation(annotation: Annotation, includeDefaultValues: Boolean = false) =
    addAnnotation(AnnotationSpec.get(annotation, includeDefaultValues))


// TypeSpec.Builder

internal inline fun <reified A : Annotation> TypeSpec.Builder.addAnnotation(
    builder: AnnotationSpec.Builder.() -> Unit,
) = addAnnotation(AnnotationSpec.builder(A::class).apply(builder).build())

@DelicateKotlinPoetApi("See 'AnnotationSpec.get'")
internal fun TypeSpec.Builder.addAnnotation(annotation: Annotation, includeDefaultValues: Boolean = false) =
    addAnnotation(AnnotationSpec.get(annotation, includeDefaultValues))

internal inline fun TypeSpec.Builder.primaryConstructor(builder: FunSpec.Builder.() -> Unit) =
    primaryConstructor(FunSpec.constructorBuilder().apply(builder).build())

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

internal inline fun <reified T> FunSpec.Builder.addParameter(name: String, vararg modifiers: KModifier) =
    addParameter(name, typeNameOf<T>(), *modifiers)

internal inline fun FunSpec.Builder.withControlFlow(
    controlFlow: String,
    vararg args: Any,
    builder: FunSpec.Builder.() -> Unit,
) = beginControlFlow(controlFlow, *args).apply(builder).endControlFlow()


// PropertySpec.Builder

internal inline fun PropertySpec.Builder.delegate(builder: CodeBlock.Builder.() -> Unit) =
    delegate(CodeBlock.builder().apply(builder).build())

@DelicateKotlinPoetApi("See 'AnnotationSpec.get'")
internal fun PropertySpec.Builder.addAnnotation(annotation: Annotation, includeDefaultValues: Boolean = false) =
    addAnnotation(AnnotationSpec.get(annotation, includeDefaultValues))

internal inline fun PropertySpec.Builder.getter(builder: FunSpec.Builder.() -> Unit) =
    getter(FunSpec.getterBuilder().apply(builder).build())


// CodeBlock.Builder

internal inline fun CodeBlock.Builder.withControlFlow(
    controlFlow: String,
    vararg args: Any?,
    builder: CodeBlock.Builder.() -> Unit,
) = beginControlFlow(controlFlow, *args).apply(builder).endControlFlow()


// other

internal inline fun <reified E : Enum<E>> E.asMemberName() = E::class.member(name)
