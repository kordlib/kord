package dev.kord.ksp

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.MemberName.Companion.member
import kotlin.annotation.AnnotationTarget.TYPE

// for scope control, see https://kotlinlang.org/docs/type-safe-builders.html#scope-control-dslmarker
@DslMarker
@Target(TYPE)
internal annotation class KotlinPoetDsl

internal typealias FileSpecBuilder = (@KotlinPoetDsl FileSpec.Builder).() -> Unit
internal typealias TypeSpecBuilder = (@KotlinPoetDsl TypeSpec.Builder).() -> Unit
internal typealias AnnotationSpecBuilder = (@KotlinPoetDsl AnnotationSpec.Builder).() -> Unit
internal typealias FunSpecBuilder = (@KotlinPoetDsl FunSpec.Builder).() -> Unit
internal typealias PropertySpecBuilder = (@KotlinPoetDsl PropertySpec.Builder).() -> Unit
internal typealias CodeBlockBuilder = (@KotlinPoetDsl CodeBlock.Builder).() -> Unit

// standalone

internal inline fun FileSpec(packageName: String, fileName: String, builder: FileSpecBuilder) =
    FileSpec.builder(packageName, fileName).apply(builder).build()


// FileSpec.Builder

internal inline fun FileSpec.Builder.addClass(className: ClassName, builder: TypeSpecBuilder) =
    addType(TypeSpec.classBuilder(className).apply(builder).build())

@DelicateKotlinPoetApi("See 'AnnotationSpec.get'")
internal fun FileSpec.Builder.addAnnotation(annotation: Annotation, includeDefaultValues: Boolean = false) =
    addAnnotation(AnnotationSpec.get(annotation, includeDefaultValues))


// TypeSpec.Builder

internal inline fun <reified A : Annotation> TypeSpec.Builder.addAnnotation(builder: AnnotationSpecBuilder) =
    addAnnotation(AnnotationSpec.builder(A::class).apply(builder).build())

@DelicateKotlinPoetApi("See 'AnnotationSpec.get'")
internal fun TypeSpec.Builder.addAnnotation(annotation: Annotation, includeDefaultValues: Boolean = false) =
    addAnnotation(AnnotationSpec.get(annotation, includeDefaultValues))

internal inline fun TypeSpec.Builder.primaryConstructor(builder: FunSpecBuilder) =
    primaryConstructor(FunSpec.constructorBuilder().apply(builder).build())

internal inline fun <reified T> TypeSpec.Builder.addProperty(
    name: String,
    vararg modifiers: KModifier,
    builder: PropertySpecBuilder,
) = addProperty(PropertySpec.builder(name, typeNameOf<T>(), *modifiers).apply(builder).build())

internal inline fun TypeSpec.Builder.addProperty(
    name: String,
    type: TypeName,
    vararg modifiers: KModifier,
    builder: PropertySpecBuilder,
) = addProperty(PropertySpec.builder(name, type, *modifiers).apply(builder).build())

internal inline fun TypeSpec.Builder.addFunction(name: String, builder: FunSpecBuilder) =
    addFunction(FunSpec.builder(name).apply(builder).build())

internal inline fun TypeSpec.Builder.addClass(name: String, builder: TypeSpecBuilder) =
    addType(TypeSpec.classBuilder(name).apply(builder).build())

internal inline fun TypeSpec.Builder.addObject(name: String, builder: TypeSpecBuilder) =
    addType(TypeSpec.objectBuilder(name).apply(builder).build())

internal inline fun TypeSpec.Builder.addCompanionObject(name: String? = null, builder: TypeSpecBuilder) =
    addType(TypeSpec.companionObjectBuilder(name).apply(builder).build())


// FunSpec.Builder

internal inline fun <reified T> FunSpec.Builder.returns() = returns(typeNameOf<T>())

internal inline fun <reified T> FunSpec.Builder.addParameter(name: String, vararg modifiers: KModifier) =
    addParameter(name, typeNameOf<T>(), *modifiers)

internal inline fun FunSpec.Builder.withControlFlow(controlFlow: String, vararg args: Any, builder: FunSpecBuilder) =
    beginControlFlow(controlFlow, *args).apply(builder).endControlFlow()


// PropertySpec.Builder

internal inline fun PropertySpec.Builder.delegate(builder: CodeBlockBuilder) =
    delegate(CodeBlock.builder().apply(builder).build())

@DelicateKotlinPoetApi("See 'AnnotationSpec.get'")
internal fun PropertySpec.Builder.addAnnotation(annotation: Annotation, includeDefaultValues: Boolean = false) =
    addAnnotation(AnnotationSpec.get(annotation, includeDefaultValues))

internal inline fun PropertySpec.Builder.getter(builder: FunSpecBuilder) =
    getter(FunSpec.getterBuilder().apply(builder).build())


// CodeBlock.Builder

internal inline fun CodeBlock.Builder.withControlFlow(
    controlFlow: String,
    vararg args: Any?,
    builder: CodeBlockBuilder,
) = beginControlFlow(controlFlow, *args).apply(builder).endControlFlow()


// other

internal inline fun <reified E : Enum<E>> E.asMemberName() = E::class.member(name)
