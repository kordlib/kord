package dev.kord.ksp

import com.squareup.kotlinpoet.Annotatable
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.DelicateKotlinPoetApi
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName.Companion.member
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeSpecHolder
import com.squareup.kotlinpoet.typeNameOf
import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.TYPE

// for scope control, see https://kotlinlang.org/docs/type-safe-builders.html#scope-control-dslmarker
@DslMarker
@Retention(SOURCE)
@Target(TYPE)
internal annotation class KotlinPoetDsl

internal typealias FileSpecBuilder = (@KotlinPoetDsl FileSpec.Builder).() -> Unit
internal typealias TypeSpecBuilder = (@KotlinPoetDsl TypeSpec.Builder).() -> Unit
internal typealias AnnotationSpecBuilder = (@KotlinPoetDsl AnnotationSpec.Builder).() -> Unit
internal typealias FunSpecBuilder = (@KotlinPoetDsl FunSpec.Builder).() -> Unit
internal typealias PropertySpecBuilder = (@KotlinPoetDsl PropertySpec.Builder).() -> Unit
internal typealias ParameterSpecBuilder = (@KotlinPoetDsl ParameterSpec.Builder).() -> Unit
internal typealias CodeBlockBuilder = (@KotlinPoetDsl CodeBlock.Builder).() -> Unit


// miscellaneous

internal inline fun <reified E : Enum<E>> E.asMemberName() = E::class.member(name)

internal inline fun FileSpec(packageName: String, fileName: String, builder: FileSpecBuilder) =
    FileSpec.builder(packageName, fileName).apply(builder).build()


// extensions for `Annotatable.Builder`

@DelicateKotlinPoetApi("See 'AnnotationSpec.get'")
internal fun <T : Annotatable.Builder<T>> T.addAnnotation(
    annotation: Annotation,
    includeDefaultValues: Boolean = false,
) = addAnnotation(AnnotationSpec.get(annotation, includeDefaultValues))

internal inline fun <T : Annotatable.Builder<T>> T.addAnnotation(type: ClassName, builder: AnnotationSpecBuilder) =
    addAnnotation(AnnotationSpec.builder(type).apply(builder).build())

internal inline fun <reified A : Annotation> Annotatable.Builder<*>.addAnnotation(builder: AnnotationSpecBuilder = {}) =
    addAnnotation(AnnotationSpec.builder(A::class).apply(builder).build())


// extensions for `TypeSpecHolder.Builder`

internal inline fun <T : TypeSpecHolder.Builder<T>> T.addClass(name: String, builder: TypeSpecBuilder) =
    addType(TypeSpec.classBuilder(name).apply(builder).build())

internal inline fun <T : TypeSpecHolder.Builder<T>> T.addClass(className: ClassName, builder: TypeSpecBuilder) =
    addType(TypeSpec.classBuilder(className).apply(builder).build())

internal inline fun <T : TypeSpecHolder.Builder<T>> T.addObject(name: String, builder: TypeSpecBuilder) =
    addType(TypeSpec.objectBuilder(name).apply(builder).build())


// extensions for `FileSpec.Builder`

internal inline fun FileSpec.Builder.addFunction(name: String, builder: FunSpecBuilder) =
    addFunction(FunSpec.builder(name).apply(builder).build())


// extensions for `TypeSpec.Builder`

internal inline fun TypeSpec.Builder.addCompanionObject(name: String? = null, builder: TypeSpecBuilder) =
    addType(TypeSpec.companionObjectBuilder(name).apply(builder).build())

internal inline fun TypeSpec.Builder.addFunction(name: String, builder: FunSpecBuilder) =
    addFunction(FunSpec.builder(name).apply(builder).build())

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

internal inline fun TypeSpec.Builder.primaryConstructor(builder: FunSpecBuilder) =
    primaryConstructor(FunSpec.constructorBuilder().apply(builder).build())

internal inline fun TypeSpec.Builder.addInitializerBlock(builder: CodeBlockBuilder) =
    addInitializerBlock(CodeBlock.builder().apply(builder).build())

internal inline fun TypeSpec.Builder.addConstructor(builder: FunSpecBuilder) =
    addFunction(FunSpec.constructorBuilder().apply(builder).build())


// extensions for `FunSpec.Builder`

internal inline fun FunSpec.Builder.addParameter(
    name: String,
    type: TypeName,
    vararg modifiers: KModifier,
    builder: ParameterSpecBuilder,
) = addParameter(ParameterSpec.builder(name, type, *modifiers).apply(builder).build())

internal inline fun <reified T> FunSpec.Builder.addParameter(name: String, vararg modifiers: KModifier) =
    addParameter(name, typeNameOf<T>(), *modifiers)

internal inline fun <reified T> FunSpec.Builder.returns() = returns(typeNameOf<T>())

internal inline fun FunSpec.Builder.withControlFlow(controlFlow: String, vararg args: Any, builder: FunSpecBuilder) =
    beginControlFlow(controlFlow, *args).apply(builder).endControlFlow()


// extensions for `PropertySpec.Builder`

internal inline fun PropertySpec.Builder.delegate(builder: CodeBlockBuilder) =
    delegate(CodeBlock.builder().apply(builder).build())

internal inline fun PropertySpec.Builder.getter(builder: FunSpecBuilder) =
    getter(FunSpec.getterBuilder().apply(builder).build())


// extensions for `CodeBlock.Builder`

internal inline fun CodeBlock.Builder.withControlFlow(
    controlFlow: String,
    vararg args: Any?,
    builder: CodeBlockBuilder,
) = beginControlFlow(controlFlow, *args).apply(builder).endControlFlow()
