package dev.kord.common.entity.optional

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Represents a value that encapsulates all [three possible states of a value in the Discord API](https://discord.com/developers/docs/reference#nullable-and-optional-resource-fields).
 * Specifically:
 *
 * * [Missing] - a field that was not present in the serialized entity
 * * [Null] - a field that was assigned null in the serialized entity
 * * [Value] - a field that was assigned a non-null value in the serialized entity.
 *
 * The base class is  (de)serializable with kotlinx.serialization and should be used as follows:
 *
 * * `Optional<T>` - a field that is only optional but not nullable.
 * * `Optional<T?>` - A field that is both optional and nullable.
 * * A field that is only nullable should be represented as `T?` instead.
 *
 * Trying to deserialize `null` as `Optional<T>` will result in a [SerializationException] being thrown.
 *
 * Note that kotlinx.serialization does **not** call serializers for values that are not
 * present in the serialized format. `Optional` fields should have a default value of `Optional.Missing`:
 *
 * ```kotlin
 * @Serializable
 * class DiscordUser(
 *     val id: Long,
 *     val username: String,
 *     val bot: Optional<Boolean?> = Optional.Missing()
 * )
 * ```
 */
@Serializable(with = OptionalSerializer::class)
public sealed class Optional<out T> {

    /**
     * The value this optional wraps.
     * * Both [Missing] and [Null] will always return `null`.
     * * [Value] will always return a non-null value.
     */
    public open val value: T?
        get() = throw UnsupportedOperationException("This is implemented in implementation classes")

    /**
     * Represents a field that was not present in the serialized entity.
     */
    public class Missing<out T> private constructor() : Optional<T>() {

        /**
         * The value this optional wraps, always `null`.
         */
        override val value: T?
            get() = null

        override fun toString(): String = "Optional.Missing"

        override fun equals(other: Any?): Boolean {
            return other is Missing<*>
        }

        override fun hashCode(): Int = 0

        public companion object {
            private val constantNull = Missing<Nothing>()

            public operator fun <T : Any> invoke(): Missing<T> = constantNull
        }
    }

    /**
     * Represents a field that was assigned null in the serialized entity.
     */
    public class Null<out T> private constructor() : Optional<T?>() {

        /**
         * The value this optional wraps, always `null`.
         */
        override val value: T?
            get() = null

        override fun toString(): String = "Optional.Null"

        override fun equals(other: Any?): Boolean {
            return other is Null<*>
        }

        override fun hashCode(): Int = 0

        public companion object {
            private val constantNull = Null<Nothing>()

            public operator fun <T : Any> invoke(): Null<T> = constantNull
        }
    }

    /**
     * Represents a field that was assigned a non-null value in the serialized entity.
     * Equality and hashcode is implemented through its [value].
     *
     * @param value the value this optional wraps.
     */
    public class Value<T : Any>(override val value: T) : Optional<T>() {
        override fun toString(): String = "Optional.Something(content=$value)"

        /**
         * Destructures this optional to its [value].
         */
        public operator fun component1(): T = value

        override fun equals(other: Any?): Boolean {
            val value = other as? Value<*> ?: return false
            return value.value == this.value
        }

        override fun hashCode(): Int = value.hashCode()
    }

    public companion object {

        public fun <T, C : Collection<T>> missingOnEmpty(value: C): Optional<C> =
            if (value.isEmpty()) Missing()
            else Value(value)

        /**
         * Returns a [Missing] optional of type [T].
         */
        public operator fun <T : Any> invoke(): Missing<T> = Missing()

        /**
         * Returns a [Value] optional of type [T] with the given [value].
         */
        public operator fun <T : Any> invoke(value: T): Value<T> = Value(value)

        /**
         * Returns an [Optional] that is either [value] on a non-null [value], or [Null] on `null`.
         */
        @JvmName("invokeNullable")
        public operator fun <T : Any> invoke(value: T?): Optional<T?> = when (value) {
            null -> Null()
            else -> Value(value)
        }
    }

    internal class OptionalSerializer<T>(private val contentSerializer: KSerializer<T>) : KSerializer<Optional<T>> {
        override val descriptor: SerialDescriptor = contentSerializer.descriptor

        @OptIn(ExperimentalSerializationApi::class)
        override fun deserialize(decoder: Decoder): Optional<T> {
            /**
             * let's clear up any inconsistencies, an Optional cannot be <T: Any> and be represented as nullable.
             */
            if (!descriptor.isNullable && !decoder.decodeNotNullMark()) {
                throw SerializationException("descriptor for ${descriptor.serialName} was not nullable but null mark was encountered")
            }

            /**
             * This is rather ugly; I can't figure out a way to convince the compiler that <T> isn't nullable,
             * we have personally proven above that the serializer cannot return null so we'll just act as if we
             * know what we're doing.
             */
            val optional: Optional<T?> = when {
                !decoder.decodeNotNullMark() -> {
                    decoder.decodeNull()
                    Null<Nothing>()
                }
                else -> Optional(decoder.decodeSerializableValue(contentSerializer))
            }

            @Suppress("UNCHECKED_CAST")
            return optional as Optional<T>
        }

        @OptIn(ExperimentalSerializationApi::class)
        override fun serialize(encoder: Encoder, value: Optional<T>) = when (value) {
            is Missing<*> -> throw SerializationException("missing values cannot be serialized")
            is Null<*> -> encoder.encodeNull()
            is Value -> encoder.encodeSerializableValue(contentSerializer, value.value)
        }
    }
}

public fun <T : Any> Optional<T>.switchOnMissing(value: T): Optional<T> = when (this) {
    is Missing -> Value(value)
    is Null<*>, is Value -> this
}

public fun <T : Any> Optional<T>.switchOnMissing(value: Optional<T>): Optional<T> = when (this) {
    is Missing -> value
    is Null<*>, is Value -> this
}

public fun <E> Optional<List<E>>.orEmpty(): List<E> = when (this) {
    is Missing, is Null<*> -> emptyList()
    is Value -> value
}

public fun <E> Optional<Set<E>>.orEmpty(): Set<E> = when (this) {
    is Missing, is Null<*> -> emptySet()
    is Value -> value
}

@Suppress("UNCHECKED_CAST")
public inline fun <E, T> Optional<List<E>>.mapList(mapper: (E) -> T): Optional<List<T>> = when (this) {
    is Missing, is Null<*> -> this as Optional<List<T>>
    is Value -> Value(value.map(mapper))
}


@Suppress("UNCHECKED_CAST")
public inline fun <K, V, R> Optional<Map<K, V>>.mapValues(mapper: (Map.Entry<K, V>) -> R): Optional<Map<K, R>> = when (this) {
    is Missing, is Null<*> -> this as Optional<Map<K, R>>
    is Value -> Value(value.mapValues(mapper))
}


@Suppress("UNCHECKED_CAST")
public inline fun <E> Optional<List<E>>.filterList(mapper: (E) -> Boolean): Optional<List<E>> = when (this) {
    is Missing, is Null<*> -> this
    is Value -> Value(value.filter(mapper))
}

@Suppress("UNCHECKED_CAST")
public inline fun <reified R> Optional<List<*>>.filterInstanceOfList(): Optional<List<R>> = when (this) {
    is Missing, is Null<*> -> this as Optional<List<R>>
    is Value -> Value(value.filterIsInstance<R>())
}


@Suppress("UNCHECKED_CAST")
public inline fun <E : Any, T : Any> Optional<E>.map(mapper: (E) -> T): Optional<T> = when (this) {
    is Missing, is Null<*> -> this as Optional<T>
    is Value -> Value(mapper(value))
}

/**
 * Applies the [mapper] to the optional if it is a [Value], returns the same optional otherwise.
 */
@Suppress("UNCHECKED_CAST")
public inline fun <E : Any, T : Any> Optional<E>.flatMap(mapper: (E) -> Optional<T>): Optional<T> = when (this) {
    is Missing, is Null<*> -> this as Optional<T>
    is Value -> mapper(value)
}

@Suppress("UNCHECKED_CAST")
@JvmName("mapNullableOptional")
public inline fun <E : Any, T : Any> Optional<E?>.map(mapper: (E) -> T): Optional<T?> = when (this) {
    is Missing, is Null<*> -> this as Optional<T>
    is Value -> Value(mapper(value!!))
}

@Suppress("UNCHECKED_CAST")
public inline fun <E, T> Optional<E>.mapNullable(mapper: (E) -> T): Optional<T?> = when (this) {
    is Missing, is Null<*> -> this as Optional<T>
    is Value -> Optional(mapper(value))
}

@Suppress("UNCHECKED_CAST")
public inline fun <E : Any, T> Optional<E?>.mapNotNull(mapper: (E) -> T): Optional<T?> = when (this) {
    is Missing -> this as Optional<T?>
    is Null<*> -> this as Optional<T?>
    is Value -> Optional(mapper(value!!))
}

public inline fun <E> Optional<List<E>>.firstOrNull(predicate: (E) -> Boolean): E? = when (this) {
    is Missing, is Null<*> -> null
    is Value -> value.firstOrNull(predicate)
}


public inline fun <E> Optional<List<E>>.first(predicate: (E) -> Boolean): E = firstOrNull(predicate)!!


public inline fun <E : Any> Optional<E>.mapSnowflake(mapper: (E) -> Snowflake): OptionalSnowflake = when (this) {
    is Missing, is Null<*> -> OptionalSnowflake.Missing
    is Value -> OptionalSnowflake.Value(mapper(value))
}

@JvmName("mapNullableSnowflake")
public inline fun <E : Any> Optional<E?>.mapSnowflake(mapper: (E) -> Snowflake): OptionalSnowflake = when (this) {
    is Missing, is Null<*> -> OptionalSnowflake.Missing
    is Value -> OptionalSnowflake.Value(mapper(value!!))
}

public inline fun <T, R : Any> Optional<T>.unwrap(mapper: (T) -> R): R? = when (this) {
    is Missing, is Null<*> -> null
    is Value -> mapper(value)
}

@Suppress("UNCHECKED_CAST")
public fun <T : Any> Optional<T?>.coerceToMissing(): Optional<T> = when (this) {
    is Missing, is Null -> Missing()
    is Value -> this as Value<T>
}

@Suppress("RemoveRedundantQualifierName")
public fun <T : Any> T.optional(): Optional.Value<T> = Optional.Value(this)

public fun <T : Any?> T?.optional(): Optional<T?> = Optional(this)

public fun Optional<Boolean>.toPrimitive(): OptionalBoolean = when (this) {
    is Value -> OptionalBoolean.Value(value)
    else -> OptionalBoolean.Missing
}
