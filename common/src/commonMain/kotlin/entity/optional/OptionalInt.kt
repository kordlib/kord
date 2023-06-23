package dev.kord.common.entity.optional

import dev.kord.common.entity.optional.OptionalInt.Missing
import dev.kord.common.entity.optional.OptionalInt.Value
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Represents a value that encapsulate [the optional and value state of an Int in the Discord API](https://discord.com/developers/docs/reference#nullable-and-optional-resource-fields).
 * Specifically:

 * * [Missing] - an Int field that was not present in the serialized entity.
 * * [Value] - an Int field that was assigned a non-null value in the serialized entity.
 *
 * > Note that there is no nullable variant present. Use Int? or `OptionalInt?` for this case instead.
 *
 * The base class is (de)serializable with kotlinx.serialization.
 *
 * Note that kotlinx.serialization does **not** call serializers for values that are not
 * present in the serialized format. `Optional` fields should have a default value of `OptionalInt.Missing`:
 *
 * ```kotlin
 * @Serializable
 * class DiscordUser(
 *     val id: Long,
 *     val username: String,
 *     val bot: OptionalInt = OptionalInt.Missing
 * )
 * ```
 */
@Serializable(with = OptionalInt.Serializer::class)
public sealed class OptionalInt {

    public val asNullable: Int?
        get() = when (this) {
            Missing -> null
            is Value -> value
        }

    public val asOptional: Optional<Int>
        get() = when (this) {
            Missing -> Optional.Missing()
            is Value -> Optional.Value(value)
        }

    /**
     * returns [default] if the optional is [Missing], or [Value.value] if is [Value].
     */
    public fun orElse(default: Int): Int = when (this) {
        Missing -> default
        is Value -> value
    }

    /**
     * Represents an Int field that was not present in the serialized entity.
     */
    public object Missing : OptionalInt() {
        override fun toString(): String = "OptionalInt.Missing"
    }

    /**
     * Represents a field that was assigned a non-null value in the serialized entity.
     * Equality and hashcode is implemented through its [value].
     *
     * @param value the value this optional wraps.
     */
    public class Value(public val value: Int) : OptionalInt() {

        /**
         * Destructures this optional to its [value].
         */
        public operator fun component1(): Int = value

        override fun toString(): String = "Optional.Value(value=$value)"

        override fun equals(other: Any?): Boolean {
            val value = other as? Value ?: return false
            return value.value == this.value
        }

        override fun hashCode(): Int = value.hashCode()
    }

    internal object Serializer : KSerializer<OptionalInt> {

        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("Kord.OptionalInt", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): OptionalInt = Value(decoder.decodeInt())

        override fun serialize(encoder: Encoder, value: OptionalInt) = when (value) {
            Missing -> throw SerializationException("missing values cannot be serialized")
            is Value -> encoder.encodeInt(value.value)
        }

    }

}

/**
 * returns `null` if this is `null` or [OptionalInt.Missing], calls [OptionalInt.Value.value] otherwise.
 */
public val OptionalInt?.value: Int?
    get() = when (this) {
        is Value -> value
        Missing, null -> null
    }

/**
 * returns `null` if this is `null`, calls [OptionalInt.asNullable] otherwise.
 */
public val OptionalInt?.asNullable: Int? get() = this?.asNullable

/**
 * returns [default] if this is `null`, calls [OptionalInt.asNullable] otherwise.
 */
public fun OptionalInt?.orElse(default: Int): Int = this?.orElse(default) ?: default

/**
 * returns the value of the optional or throws a [IllegalStateException] if the optional doesn't contain a value or is `null`.
 */
public fun OptionalInt?.getOrThrow(): Int = when (this) {
    Missing, null -> throw IllegalStateException("Optional did not contain a value")
    is Value -> value
}


@Suppress("RemoveRedundantQualifierName")
public fun Int.optionalInt(): OptionalInt.Value = OptionalInt.Value(this)

public inline fun OptionalInt.map(mapper: (Int) -> Int): OptionalInt = when (this) {
    Missing -> Missing
    is Value -> Value(mapper(value))
}
