package dev.kord.common.entity.optional

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Represents a value that encapsulate [the optional and value state of a Boolean in the Discord API](https://discord.com/developers/docs/reference#nullable-and-optional-resource-fields).
 * Specifically:

 * * [Missing] - a Boolean field that was not present in the serialized entity.
 * * [Value] - a Boolean field that was assigned a non-null value in the serialized entity.
 *
 * > Note that there is no nullable variant present. Use Boolean? or `OptionalBoolean?` for this case instead.
 *
 * These classes are fully (de)serializable with kotlinx.serialization.
 *
 * Note that kotlinx.serialization does **not** call serializers for values that are not
 * present in the serialized format. `Optional` fields should have a default value of `OptionalBoolean.Missing`:
 *
 * ```kotlin
 * @Serializable
 * class DiscordUser(
 *     val id: Boolean,
 *     val username: String,
 *     val bot: OptionalBoolean = OptionalBoolean.Missing
 * )
 * ```
 */
@Serializable(with = OptionalBoolean.Serializer::class)
public sealed class OptionalBoolean {

    public val discordBoolean: Boolean get() = orElse(false)

    public operator fun not(): OptionalBoolean = when (this) {
        Missing -> this
        is Value -> Value(!value)
    }

    public val asNullable: Boolean?
        get() = when (this) {
            Missing -> null
            is Value -> value
        }

    public val asOptional: Optional<Boolean>
        get() = when (this) {
            Missing -> Optional.Missing()
            is Value -> Optional.Value(value)
        }

    /**
     * returns [default] if the optional is [Missing], or [Value.value] if is [Value].
     */
    public fun orElse(default: Boolean): Boolean = when (this) {
        Missing -> default
        is Value -> value
    }

    /**
     * Represents a Boolean field that was not present in the serialized entity.
     */
    public object Missing : OptionalBoolean() {
        override fun toString(): String = "OptionalBoolean.Missing"
    }

    /**
     * Represents a field that was assigned a non-null value in the serialized entity.
     * Equality and hashcode is implemented through its [value].
     *
     * @param value the value this optional wraps.
     */
    public class Value(public val value: Boolean) : OptionalBoolean() {

        /**
         * Destructures this optional to its [value].
         */
        public operator fun component1(): Boolean = value

        override fun toString(): String = "Optional.Value(value=$value)"

        override fun equals(other: Any?): Boolean {
            val value = other as? Value ?: return false
            return value.value == this.value
        }

        override fun hashCode(): Int = value.hashCode()
    }

    internal object Serializer : KSerializer<OptionalBoolean> {

        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("Kord.OptionalBoolean", PrimitiveKind.BOOLEAN)

        override fun deserialize(decoder: Decoder): OptionalBoolean = Value(decoder.decodeBoolean())

        override fun serialize(encoder: Encoder, value: OptionalBoolean) = when (value) {
            Missing -> Unit//ignore value
            is Value -> encoder.encodeBoolean(value.value)
        }

    }

}

/**
 * returns `null` if this is `null` or [OptionalBoolean.Missing], calls [OptionalBoolean.Value.value] otherwise.
 */
public val OptionalBoolean?.value: Boolean?
    get() = when (this) {
        is OptionalBoolean.Value -> value
        OptionalBoolean.Missing, null -> null
    }

/**
 * returns `null` if this is `null`, calls [OptionalBoolean.asNullable] otherwise.
 */
public val OptionalBoolean?.asNullable: Boolean? get() = this?.asNullable

/**
 * returns [default] if this is `null`, calls [OptionalBoolean.asNullable] otherwise.
 */
public fun OptionalBoolean?.orElse(default: Boolean): Boolean = this?.orElse(default) ?: default

public fun Boolean.optional(): OptionalBoolean.Value = OptionalBoolean.Value(this)
