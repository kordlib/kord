package dev.kord.common.entity

import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.optional.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*
import mu.KotlinLogging

val kordLogger = KotlinLogging.logger { }

@Serializable
@KordPreview
data class DiscordApplicationCommand(
    val id: Snowflake,
    @SerialName("application_id")
    val applicationId: Snowflake,
    val name: String,
    val description: String,
    @SerialName("guild_id")
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    val options: Optional<List<ApplicationCommandOption>> = Optional.Missing(),
)

@Serializable
@KordPreview
class ApplicationCommandOption(
    val type: ApplicationCommandOptionType,
    val name: String,
    val description: String,
    val default: OptionalBoolean = OptionalBoolean.Missing,
    val required: OptionalBoolean = OptionalBoolean.Missing,
    @OptIn(KordExperimental::class)
    val choices: Optional<List<Choice<@Serializable(NotSerializable::class) Any?>>> = Optional.Missing(),
    val options: Optional<List<ApplicationCommandOption>> = Optional.Missing(),
)

/**
 * A serializer whose sole purpose is to provide a No-Op serializer for [Any].
 * The serializer is used when the generic type is neither known nor relevant to the serialization process
 *
 * e.g: `Choice<@Serializable(NotSerializable::class) Any?>`
 * The serialization is handled by [Choice] serializer instead where we don't care about the generic type.
 */
@KordExperimental
object NotSerializable : KSerializer<Any?> {
    override fun deserialize(decoder: Decoder) = error("This operation is not supported.")
    override val descriptor: SerialDescriptor = String.serializer().descriptor
    override fun serialize(encoder: Encoder, value: Any?) = error("This operation is not supported.")
}


@Serializable(ApplicationCommandOptionType.Serializer::class)
@KordPreview
sealed class ApplicationCommandOptionType(val type: Int) {


    object SubCommand : ApplicationCommandOptionType(1)
    object SubCommandGroup : ApplicationCommandOptionType(2)
    object String : ApplicationCommandOptionType(3)
    object Integer : ApplicationCommandOptionType(4)
    object Boolean : ApplicationCommandOptionType(5)
    object User : ApplicationCommandOptionType(6)
    object Channel : ApplicationCommandOptionType(7)
    object Role : ApplicationCommandOptionType(8)
    class Unknown(type: Int) : ApplicationCommandOptionType(type)

    companion object;

    internal object Serializer : KSerializer<ApplicationCommandOptionType> {

        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("ApplicationCommandOptionType", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): ApplicationCommandOptionType {
            return when (val type = decoder.decodeInt()) {
                1 -> SubCommand
                2 -> SubCommandGroup
                3 -> String
                4 -> Integer
                5 -> Boolean
                6 -> User
                7 -> Channel
                8 -> Role
                else -> Unknown(type)
            }
        }

        override fun serialize(encoder: Encoder, value: ApplicationCommandOptionType) {
            encoder.encodeInt(value.type)
        }
    }


}

@Serializable(Choice.ChoiceSerializer::class)
@KordPreview
sealed class Choice<out T> {
    abstract val name: String
    abstract val value: T

    class IntChoice(override val name: String, override val value: Int) : Choice<Int>()
    class StringChoice(override val name: String, override val value: String) : Choice<String>()
    internal class ChoiceSerializer<T>(serializer: KSerializer<T>) : KSerializer<Choice<*>> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Choice") {
            element<String>("name")
            element<String>("value")
        }

        override fun deserialize(decoder: Decoder): Choice<*> {
            lateinit var name: String
            lateinit var value: JsonPrimitive
            with(decoder.beginStructure(descriptor) as JsonDecoder) {
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> name = decodeStringElement(descriptor, index)
                        1 -> value = decodeJsonElement().jsonPrimitive

                        CompositeDecoder.DECODE_DONE -> break
                        else -> throw SerializationException("unknown index: $index")
                    }
                }
                endStructure(descriptor)
            }
            return if (value.isString) StringChoice(name, value.toString()) else IntChoice(name, value.int)
        }

        override fun serialize(encoder: Encoder, value: Choice<*>) {
            encoder.encodeStructure(descriptor) {
                encodeStringElement(descriptor, 0, value.name)
                if (value is IntChoice) encodeIntElement(descriptor, 1, value.value)
                else encodeStringElement(descriptor, 1, value.value.toString())
            }
        }
    }
}

@Serializable
@KordPreview
data class ResolvedObjects(
    val members: Optional<Map<Snowflake, DiscordGuildMember>> = Optional.Missing(),
    val users: Optional<Map<Snowflake, DiscordUser>> = Optional.Missing(),
    val roles: Optional<Map<Snowflake, DiscordRole>> = Optional.Missing(),
    val channels: Optional<Map<Snowflake, DiscordChannel>> = Optional.Missing()
)

@Serializable
@KordPreview
data class DiscordInteraction(
    val id: Snowflake,
    @SerialName("application_id")
    val applicationId: Snowflake,
    val type: InteractionType,
    val data: DiscordApplicationCommandInteractionData,
    @SerialName("guild_id")
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("channel_id")
    val channelId: Snowflake,
    val member: Optional<DiscordInteractionGuildMember> = Optional.Missing(),
    val user: Optional<DiscordUser> = Optional.Missing(),
    val token: String,
    val version: Int,
)

@Serializable(InteractionType.Serializer::class)
@KordPreview
sealed class InteractionType(val type: Int) {
    object Ping : InteractionType(1)
    object ApplicationCommand : InteractionType(2)
    class Unknown(type: Int) : InteractionType(type)

    override fun toString(): String = when (this) {
        Ping -> "InteractionType.Ping($type)"
        ApplicationCommand -> "InteractionType.ApplicationCommand($type)"
        is Unknown -> "InteractionType.Unknown($type)"
    }

    companion object;
    internal object Serializer : KSerializer<InteractionType> {

        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("InteractionType", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): InteractionType {
            return when (val type = decoder.decodeInt()) {
                1 -> Ping
                2 -> ApplicationCommand
                else -> Unknown(type)
            }
        }

        override fun serialize(encoder: Encoder, value: InteractionType) {
            encoder.encodeInt(value.type)
        }

    }
}

@Serializable
@KordPreview
data class DiscordApplicationCommandInteractionData(
    val id: Snowflake,
    val name: String,
    val resolved: Optional<ResolvedObjects> = Optional.Missing(),
    val options: Optional<List<Option>> = Optional.Missing()
)

@Serializable(with = Option.Serializer::class)
@KordPreview
sealed class Option {
    abstract val name: String

    internal object Serializer : KSerializer<Option> {

        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Kord.Option") {
            element("name", String.serializer().descriptor, isOptional = false)
            element("value", JsonElement.serializer().descriptor, isOptional = true)
            element("options", JsonArray.serializer().descriptor, isOptional = true)
        }

        override fun deserialize(decoder: Decoder): Option {
            decoder as? JsonDecoder ?: error("Option can only be deserialize with a JsonDecoder")
            val json = decoder.json

            var name = ""
            var jsonValue: JsonPrimitive? = null
            var jsonOptions: JsonArray? = null
            decoder.decodeStructure(descriptor) {
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> name = decodeStringElement(descriptor, index)
                        1 -> jsonValue = decodeSerializableElement(descriptor, index, JsonPrimitive.serializer())
                        2 -> jsonOptions = decodeSerializableElement(descriptor, index, JsonArray.serializer())

                        CompositeDecoder.DECODE_DONE -> return@decodeStructure
                        else -> throw SerializationException("unknown index: $index")
                    }
                }
            }

            jsonValue?.let { value -> // name + value == command option, i.e. an argument
                return CommandArgument(name, DiscordOptionValue(value))
            }

            if (jsonOptions == null) { // name -value -options == can only be sub command
                return SubCommand(name, Optional.Missing())
            }

            //options are present, either a subcommand or a group, we'll have to look at its children
            val nestedOptions = jsonOptions?.map { json.decodeFromJsonElement(serializer(), it) } ?: emptyList()

            if (nestedOptions.isEmpty()) { //only subcommands can have no children
                return SubCommand(name, Optional(emptyList()))
            }

            val onlyArguments =
                nestedOptions.all { it is CommandArgument } //only subcommand can have options at this point
            if (onlyArguments) return SubCommand(name, Optional(nestedOptions.filterIsInstance<CommandArgument>()))

            val onlySubCommands = nestedOptions.all { it is SubCommand } //only groups can have options at this point
            if (onlySubCommands) return CommandGroup(name, Optional(nestedOptions.filterIsInstance<SubCommand>()))

            error("option mixed option arguments and option subcommands: $jsonOptions")
        }

        override fun serialize(encoder: Encoder, value: Option) {
            TODO("Not yet implemented")
        }
    }
}

@Serializable
@KordPreview
data class SubCommand(
    override val name: String,
    val options: Optional<List<CommandArgument>> = Optional.Missing()
) : Option()

@Serializable
@KordPreview
data class CommandArgument(
    override val name: String,
    @OptIn(KordExperimental::class)
    val value: DiscordOptionValue<@Serializable(NotSerializable::class) Any?>,
) : Option()

@Serializable
@KordPreview
data class CommandGroup(
    override val name: String,
    val options: Optional<List<SubCommand>> = Optional.Missing(),
) : Option()

@Serializable(DiscordOptionValue.OptionValueSerializer::class)
@KordPreview
sealed class DiscordOptionValue<out T>(val value: T) {
    class IntValue(value: Int) : DiscordOptionValue<Int>(value)
    class StringValue(value: String) : DiscordOptionValue<String>(value)
    class BooleanValue(value: Boolean) : DiscordOptionValue<Boolean>(value)

    override fun toString(): String = when (this) {
        is IntValue -> "OptionValue.IntValue($value)"
        is StringValue -> "OptionValue.StringValue($value)"
        is BooleanValue -> "OptionValue.BooleanValue($value)"
    }

    internal class OptionValueSerializer<T>(serializer: KSerializer<T>) : KSerializer<DiscordOptionValue<*>> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("OptionValue", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): DiscordOptionValue<*> {
            val value = (decoder as JsonDecoder).decodeJsonElement().jsonPrimitive
            return when {
                value.isString -> StringValue(value.toString())
                value.booleanOrNull != null -> BooleanValue(value.boolean)
                else -> IntValue(value.int)
            }
        }

        override fun serialize(encoder: Encoder, value: DiscordOptionValue<*>) {
            when (value) {
                is IntValue -> encoder.encodeInt(value.value)
                is StringValue -> encoder.encodeString(value.value)
                is BooleanValue -> encoder.encodeBoolean(value.value)
            }
        }
    }
}
@KordPreview
fun DiscordOptionValue(value: JsonPrimitive): DiscordOptionValue<Any> = when {
    value.isString -> DiscordOptionValue.StringValue(value.content)
    value.booleanOrNull != null -> DiscordOptionValue.BooleanValue(value.boolean)
    value.intOrNull != null -> DiscordOptionValue.IntValue(value.int)
    else -> throw SerializationException("unknown value type for option")
}


@KordPreview
fun DiscordOptionValue<*>.int(): Int {
    return value as? Int ?: error("$value wasn't an Int.")
}


@KordPreview
fun DiscordOptionValue<*>.string(): String {
    return value.toString()
}

@KordPreview
fun DiscordOptionValue<*>.boolean(): Boolean {
    return value as? Boolean ?: error("$value wasn't a Boolean.")
}

@KordPreview
fun DiscordOptionValue<*>.snowflake(): Snowflake {
    val id = string().toLongOrNull() ?: error("$value wasn't a Snowflake")
    return Snowflake(id)
}


@Serializable(InteractionResponseType.Serializer::class)
@KordPreview
sealed class InteractionResponseType(val type: Int) {
    object Pong : InteractionResponseType(1)
    object ChannelMessageWithSource : InteractionResponseType(4)
    object DeferredChannelMessageWithSource : InteractionResponseType(5)
    class Unknown(type: Int) : InteractionResponseType(type)

    companion object;

    internal object Serializer : KSerializer<InteractionResponseType> {

        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("InteractionResponseType", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): InteractionResponseType {
            return when (val type = decoder.decodeInt()) {
                1 -> Pong
                4 -> ChannelMessageWithSource
                5 -> DeferredChannelMessageWithSource
                else -> Unknown(type)
            }
        }

        override fun serialize(encoder: Encoder, value: InteractionResponseType) {
            encoder.encodeInt(value.type)
        }

    }
}
