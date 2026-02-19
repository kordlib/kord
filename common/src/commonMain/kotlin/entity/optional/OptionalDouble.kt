package dev.kord.common.entity.optional

import dev.kord.common.entity.optional.OptionalDouble.Missing
import dev.kord.common.entity.optional.OptionalDouble.Value
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
 * > Note that there is no nullable variant present. Use Int? or `OptionalDouble?` for this case instead.
 *
 * The base class is (de)serializable with kotlinx.serialization.
 *
 * Note that kotlinx.serialization does **not** call serializers for values that are not
 * present in the serialized format. `Optional` fields should have a default value of `OptionalDouble.Missing`:
 *
 * ```kotlin
 * @Serializable
 * public data class CreateSoundboardSoundRequest(
 *   val name: String,
 *   val sound: String,
 *   val volume: OptionalDouble? = OptionalDouble.Missing,
 *   @SerialName("emoji_id")
 *   val emojiId: OptionalSnowflake? = OptionalSnowflake.Missing,
 *   @SerialName("emoji_name")
 *   val emojiName: Optional<String?> = Optional.Missing(),
 * )
 * ```
 */
@Serializable(with = OptionalDouble.Serializer::class)
public sealed class OptionalDouble {

    public val asNullable: Double?
        get() = when (this) {
            Missing -> null
            is Value -> value
        }

    public val asOptional: Optional<Double>
        get() = when (this) {
            Missing -> Optional.Missing()
            is Value -> Optional.Value(value)
        }

    /**
     * returns [default] if the optional is [Missing], or [Value.value] if is [Value].
     */
    public fun orElse(default: Double): Double = when (this) {
        Missing -> default
        is Value -> value
    }

    /**
     * Represents an Int field that was not present in the serialized entity.
     */
    public object Missing : OptionalDouble() {
        override fun toString(): String = "OptionalDouble.Missing"
    }

    /**
     * Represents a field that was assigned a non-null value in the serialized entity.
     * Equality and hashcode is implemented through its [value].
     *
     * @param value the value this optional wraps.
     */
    public class Value(public val value: Double) : OptionalDouble() {

        /**
         * Destructures this optional to its [value].
         */
        public operator fun component1(): Double = value

        override fun toString(): String = "Optional.Value(value=$value)"

        override fun equals(other: Any?): Boolean {
            val value = other as? Value ?: return false
            return value.value == this.value
        }

        override fun hashCode(): Int = value.hashCode()
    }

    internal object Serializer : KSerializer<OptionalDouble> {

        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("Kord.OptionalDouble", PrimitiveKind.DOUBLE)

        override fun deserialize(decoder: Decoder): OptionalDouble = Value(decoder.decodeDouble())

        override fun serialize(encoder: Encoder, value: OptionalDouble) = when (value) {
            Missing -> throw SerializationException("missing values cannot be serialized")
            is Value -> encoder.encodeDouble(value.value)
        }

    }

}

/**
 * returns `null` if this is `null` or [OptionalDouble.Missing], calls [OptionalDouble.Value.value] otherwise.
 */
public val OptionalDouble?.value: Double?
    get() = when (this) {
        is Value -> value
        Missing, null -> null
    }

/**
 * returns `null` if this is `null`, calls [OptionalDouble.asNullable] otherwise.
 */
public val OptionalDouble?.asNullable: Double? get() = this?.asNullable

/**
 * returns [default] if this is `null`, calls [OptionalDouble.asNullable] otherwise.
 */
public fun OptionalDouble?.orElse(default: Int): Int = this?.orElse(default) ?: default

/**
 * returns the value of the optional or throws a [IllegalStateException] if the optional doesn't contain a value or is `null`.
 */
public fun OptionalDouble?.getOrThrow(): Double = when (this) {
    Missing, null -> throw IllegalStateException("Optional did not contain a value")
    is Value -> value
}


@Suppress("RemoveRedundantQualifierName")
public fun Double.optionalDouble(): OptionalDouble.Value = OptionalDouble.Value(this)

public inline fun OptionalDouble.map(mapper: (Double) -> Double): OptionalDouble = when (this) {
    Missing -> Missing
    is Value -> Value(mapper(value))
}
