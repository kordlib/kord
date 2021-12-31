package dev.kord.common.entity.optional

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Represents a value that encapsulate [the optional and value state of a Long in the Discord API](https://discord.com/developers/docs/reference#nullable-and-optional-resource-fields).
 * Specifically:

 * * [Missing] - a Long field that was not present in the serialized entity.
 * * [Value] - a Long field that was assigned a non-null value in the serialized entity.
 *
 * > Note that there is no nullable variant present. Use Long? or `OptionalLong?` for this case instead.
 *
 * The base class is (de)serializable with kotlinx.serialization.
 *
 * Note that kotlinx.serialization does **not** call serializers for values that are not
 * present in the serialized format. `Optional` fields should have a default value of `OptionalLong.Missing`:
 *
 * ```kotlin
 * @Serializable
 * class DiscordUser(
 *     val id: Long,
 *     val username: String,
 *     val bot: OptionalLong = OptionalLong.Missing
 * )
 * ```
 */
@Serializable(with = OptionalLong.Serializer::class)
public sealed class OptionalLong {

    public val asNullable: Long?
        get() = when (this) {
            Missing -> null
            is Value -> value
        }

    public val asOptional: Optional<Long>
        get() = when (this) {
            Missing -> Optional.Missing()
            is Value -> Optional.Value(value)
        }

    /**
     * returns [default] if the optional is [Missing], or [Value.value] if is [Value].
     */
    public fun orElse(default: Long): Long = when (this) {
        Missing -> default
        is Value -> value
    }

    /**
     * Represents a Long field that was not present in the serialized entity.
     */
    public object Missing : OptionalLong() {
        override fun toString(): String = "OptionalLong.Missing"
    }

    /**
     * Represents a field that was assigned a non-null value in the serialized entity.
     * Equality and hashcode is implemented through its [value].
     *
     * @param value the value this optional wraps.
     */
    public class Value(public val value: Long) : OptionalLong() {

        /**
         * Destructures this optional to its [value].
         */
        public operator fun component1(): Long = value

        override fun toString(): String = "Optional.Value(value=$value)"

        override fun equals(other: Any?): Boolean {
            val value = other as? Value ?: return false
            return value.value == this.value
        }

        override fun hashCode(): Int = value.hashCode()
    }

    internal object Serializer : KSerializer<OptionalLong> {

        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("Kord.OptionalLong", PrimitiveKind.LONG)

        override fun deserialize(decoder: Decoder): OptionalLong = Value(decoder.decodeLong())

        override fun serialize(encoder: Encoder, value: OptionalLong) = when (value) {
            Missing -> Unit//ignore value
            is Value -> encoder.encodeLong(value.value)
        }

    }

}

/**
 * returns `null` if this is `null`, calls [OptionalLong.asNullable] otherwise.
 */
public val OptionalLong?.asNullable: Long? get() = this?.asNullable

/**
 * returns `null` if this is `null`, calls [OptionalLong.asNullable] otherwise.
 */
public val OptionalLong?.value: Long? get() = this?.asNullable

/**
 * returns [default] if this is `null`, calls [OptionalLong.asNullable] otherwise.
 */
public fun OptionalLong?.orElse(default: Long): Long = this?.orElse(default) ?: default

public fun Long?.optional(): OptionalLong = if (this == null) OptionalLong.Missing else OptionalLong.Value(this)
