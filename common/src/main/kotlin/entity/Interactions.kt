package dev.kord.common.entity

import dev.kord.common.entity.optional.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.*
import mu.KotlinLogging

val kordLogger = KotlinLogging.logger { }

@Serializable
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
class ApplicationCommandOption(
    val type: ApplicationCommandOptionType,
    val name: String,
    val description: String,
    val default: OptionalBoolean = OptionalBoolean.Missing,
    val required: OptionalBoolean = OptionalBoolean.Missing,
    /*
        We don't care about serializer type.
        [Choice] has it's own [ChoiceSerializer]. [NotSerializable] is a no-op serializer that h
     */
    val choices: Optional<List<Choice<@Serializable(NotSerializable::class) Any?>>> = Optional.Missing(),
    val options: Optional<List<ApplicationCommandOption>> = Optional.Missing(),
)

internal object NotSerializable : KSerializer<Any?> {
    override fun deserialize(decoder: Decoder) = TODO("Not yet implemented")
    override val descriptor: SerialDescriptor = String.serializer().descriptor
    override fun serialize(encoder: Encoder, value: Any?) = TODO("Not yet implemented")
}


@Serializable(ApplicationCommandOptionType.Serializer::class)
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
            val json = decoder as JsonDecoder
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
data class DiscordInteraction(
    val id: Snowflake,
    val type: InteractionType,
    val data: DiscordApplicationCommandInteractionData,
    @SerialName("guild_id")
    val guildId: Snowflake,
    @SerialName("channel_id")
    val channelId: Snowflake,
    val member: DiscordGuildMember,
    val token: String,
    val version: Int,
)

@Serializable(InteractionType.Serializer::class)
sealed class InteractionType(val type: Int) {
    object Ping : InteractionType(1)
    object ApplicationCommand : InteractionType(2)
    class Unknown(type: Int) : InteractionType(type)

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
data class DiscordApplicationCommandInteractionData(
    val id: Snowflake,
    val name: String,
    val options: Optional<List<Option>> = Optional.Missing(),
) {
    companion object;
    internal object Serializer : KSerializer<DiscordApplicationCommandInteractionData> {
        override val descriptor: SerialDescriptor
            get() = TODO("Not yet implemented")

        override fun deserialize(decoder: Decoder): DiscordApplicationCommandInteractionData {
            TODO("Not yet implemented")
        }

        override fun serialize(encoder: Encoder, value: DiscordApplicationCommandInteractionData) {
            TODO("Not yet implemented")
        }

    }
}

@Serializable
data class SubCommand(val name: String, val options: Optional<List<SubCommandOption>> = Optional.Missing())

@Serializable
data class SubCommandOption(
    val name: String,
    @SerialName("value")
    val value: Optional<OptionValue<@Serializable(NotSerializable::class) Any?>> = Optional.Missing(),
)

@Serializable
data class CommandGroup(
    val name: String,
    @SerialName("options")
    val subCommands: Optional<List<SubCommand>> = Optional.Missing(),
)

@Serializable(OptionValue.OptionValueSerializer::class)
sealed class OptionValue<T>(val value: T) {
    class IntValue(value: Int) : OptionValue<Int>(value)
    class StringValue(value: String) : OptionValue<String>(value)
    class BooleanValue(value: Boolean) : OptionValue<Boolean>(value)

    companion object;
    internal class OptionValueSerializer<T>(serializer: KSerializer<T>) : KSerializer<OptionValue<*>> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("OptionValue", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): OptionValue<*> {
            val value = (decoder as JsonDecoder).decodeJsonElement().jsonPrimitive
            return when {
                value.isString -> StringValue(value.toString())
                value.booleanOrNull != null -> BooleanValue(value.boolean)
                else -> IntValue(value.int)
            }
        }

        override fun serialize(encoder: Encoder, value: OptionValue<*>) {
            when (value) {
                is IntValue -> encoder.encodeInt(value.value)
                is StringValue -> encoder.encodeString(value.value)
            }
        }
    }
}

@Serializable(Option.Serializer::class)
data class Option(
    val name: String,
    val value: Optional<OptionValue<@Serializable(NotSerializable::class) Any?>> = Optional.Missing(),
    val groups: Optional<List<CommandGroup>> = Optional.Missing(),
    val subCommands: Optional<List<SubCommand>> = Optional.Missing(),
) {
    internal object Serializer : KSerializer<Option> {

        override val descriptor: SerialDescriptor
            get() = buildClassSerialDescriptor("Option") {
                element<String>("name")
                element<JsonPrimitive>("value", isOptional = true)
                element<JsonArray>("options", isOptional = true)
            }


        override fun deserialize(decoder: Decoder): Option {
            lateinit var name: String
            var value: Optional<JsonPrimitive> = Optional.Missing()

            var options: Optional<JsonArray> = Optional.Missing()

            with(decoder.beginStructure(descriptor) as JsonDecoder) {
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> name = decodeStringElement(descriptor, index)
                        1 -> value = decodeSerializableElement(descriptor,
                            index,
                            Optional.serializer(JsonPrimitive.serializer()))
                        2 -> options = decodeSerializableElement(descriptor, index, Optional.serializer(JsonArray.serializer()))

                        CompositeDecoder.DECODE_DONE -> break
                        else -> throw SerializationException("unknown index: $index")
                    }
                }
                endStructure(descriptor)
            }
            val realValue = value.map { wrappedValue ->
                if (wrappedValue.isString) OptionValue.StringValue(wrappedValue.toString())
                else if (wrappedValue.booleanOrNull != null) OptionValue.BooleanValue(wrappedValue.boolean)
                else OptionValue.IntValue(wrappedValue.int)
            }
            if(options is Optional.Missing) return Option(name, realValue as Optional<OptionValue<Any?>>)

            val serializedGroups = Json.decodeFromJsonElement(Optional.serializer(ListSerializer(CommandGroup.serializer())), options.value!!)

            val serializedCommand = Json.decodeFromJsonElement(Optional.serializer(ListSerializer(SubCommand.serializer())), options.value!!)

            return Option(name, realValue as Optional<OptionValue<Any?>>, serializedGroups, serializedCommand)

        }


        override fun serialize(encoder: Encoder, value: Option) {
            TODO("Not yet implemented")
        }

    }
}

@Serializable
data class DiscordInteractionResponse(
    val type: InteractionResponseType,
    val data: Optional<DiscordInteractionApplicationCommandCallbackData> = Optional.Missing(),
)

@Serializable(InteractionResponseType.Serializer::class)
sealed class InteractionResponseType(val type: Int) {
    object Pong : InteractionResponseType(1)
    object Acknowledge : InteractionResponseType(2)
    object ChannelMessage : InteractionResponseType(3)
    object ChannelMessageWithSource : InteractionResponseType(4)
    object ACKWithSource : InteractionResponseType(5)
    class Unknown(type: Int) : InteractionResponseType(type)

    companion object;

    internal object Serializer : KSerializer<InteractionResponseType> {

        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("InteractionResponseType", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): InteractionResponseType {
            return when (val type = decoder.decodeInt()) {
                1 -> Pong
                2 -> Acknowledge
                3 -> ChannelMessage
                4 -> ChannelMessageWithSource
                5 -> ACKWithSource
                else -> Unknown(type)
            }
        }

        override fun serialize(encoder: Encoder, value: InteractionResponseType) {
            encoder.encodeInt(value.type)
        }

    }
}


@Serializable
class DiscordInteractionApplicationCommandCallbackData(
    val tts: OptionalBoolean = OptionalBoolean.Missing,
    val content: String,
    val embeds: Optional<List<DiscordEmbed>> = Optional.Missing(),
    val allowedMentions: Optional<AllowedMentions> = Optional.Missing(),
)
