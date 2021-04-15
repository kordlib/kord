package dev.kord.common.entity.optional

import dev.kord.common.entity.Snowflake
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
 * > Note that there is no nullable variant present. Use Long? or `OptionalSnowflake?` for this case instead.
 *
 * The base class is (de)serializable with kotlinx.serialization.
 *
 * Note that kotlinx.serialization does **not** call serializers for values that are not
 * present in the serialized format. `Optional` fields should have a default value of `OptionalSnowflake.Missing`:
 *
 * ```kotlin
 * @Serializable
 * class DiscordUser(
 *     val id: Long,
 *     val username: String,
 *     val bot: OptionalSnowflake = OptionalSnowflake.Missing
 * )
 * ```
 */
@Serializable(with = OptionalSnowflake.Serializer::class)
sealed class OptionalSnowflake {

    open val value: Snowflake?
        get() = when (this) {
            Missing -> null
            is Value -> value
        }

    val asOptional: Optional<Snowflake>
        get() = when (this) {
            Missing -> Optional.Missing()
            is Value -> Optional.Value(value)
        }

    /**
     * returns [default] if the optional is [Missing], or [Value.value] if is [Value].
     */
    fun orElse(default: Snowflake): Snowflake = when (this) {
        Missing -> default
        is Value -> value
    }

    /**
     * Represents a Long field that was not present in the serialized entity.
     */
    object Missing : OptionalSnowflake() {
        override fun toString(): String = "OptionalSnowflake.Missing"
    }

    /**
     * Represents a field that was assigned a non-null value in the serialized entity.
     * Equality and hashcode is implemented through its [value].
     *
     * @param longValue the value this optional wraps.
     */
    class Value(private val longValue: Long) : OptionalSnowflake() {

        constructor(value: Snowflake) : this(value.value)

        override val value: Snowflake get() = Snowflake(longValue)

        /**
         * Destructures this optional to its [value].
         */
        operator fun component1(): Snowflake = value

        override fun toString(): String = "OptionalSnowflake.Value(snowflake=$value)"

        override fun equals(other: Any?): Boolean {
            val value = other as? Value ?: return false
            return value.value == this.value
        }

        override fun hashCode(): Int = value.hashCode()
    }

    internal object Serializer : KSerializer<OptionalSnowflake> {

        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("Kord.OptionalSnowflake", PrimitiveKind.LONG)

        override fun deserialize(decoder: Decoder): OptionalSnowflake = Value(decoder.decodeLong())

        override fun serialize(encoder: Encoder, value: OptionalSnowflake) = when (value) {
            Missing -> Unit//ignore value
            is Value -> encoder.encodeLong(value.value.value)
        }

    }

}

/**
 * returns `null` if this is `null` or [OptionalSnowflake.Missing], calls [OptionalSnowflake.Value.value] otherwise.
 */
val OptionalSnowflake?.value: Snowflake?
    get() = when (this) {
        is OptionalSnowflake.Value -> value
        OptionalSnowflake.Missing, null -> null
    }

fun Snowflake.optionalSnowflake(): OptionalSnowflake.Value = OptionalSnowflake.Value(this.value)

@JvmName("optionalNullable")
fun Snowflake?.optionalSnowflake(): OptionalSnowflake.Value? = this?.optionalSnowflake()

inline fun <T : Any> OptionalSnowflake.map(mapper: (Snowflake) -> T): Optional<T> = when (this) {
    OptionalSnowflake.Missing -> Optional.Missing()
    is OptionalSnowflake.Value -> Optional.Value(mapper(value))
}