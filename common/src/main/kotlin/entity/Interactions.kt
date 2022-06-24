package dev.kord.common.entity

import dev.kord.common.Locale
import dev.kord.common.annotation.KordExperimental
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.value
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*

@Serializable
public data class DiscordApplicationCommand(
    val id: Snowflake,
    val type: Optional<ApplicationCommandType> = Optional.Missing(),
    @SerialName("application_id")
    val applicationId: Snowflake,
    val name: String,
    @SerialName("name_localizations")
    val nameLocalizations: Optional<Map<Locale, String>?> = Optional.Missing(),
    /**
     * Don't trust the docs: This is nullable on non chat input commands.
     */
    val description: String?,
    @SerialName("description_localizations")
    val descriptionLocalizations: Optional<Map<Locale, String>?> = Optional.Missing(),
    @SerialName("guild_id")
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    val options: Optional<List<ApplicationCommandOption>> = Optional.Missing(),
    @SerialName("default_member_permissions")
    val defaultMemberPermissions: Optional<Permissions?> = Optional.Missing(),
    @SerialName("dm_permission")
    val dmPermission: OptionalBoolean? = OptionalBoolean.Missing,
    @SerialName("default_permission")
    @Deprecated("'defaultPermission' is deprecated in favor of 'defaultMemberPermissions' and 'dmPermission'.")
    val defaultPermission: OptionalBoolean? = OptionalBoolean.Missing,
    val version: Snowflake
)

@Serializable(with = ApplicationCommandType.Serializer::class)
public sealed class ApplicationCommandType(public val value: Int) {
    /** The default code for unknown values. */
    public class Unknown(value: Int) : ApplicationCommandType(value)
    public object ChatInput : ApplicationCommandType(1)
    public object User : ApplicationCommandType(2)
    public object Message : ApplicationCommandType(3)

    internal object Serializer : KSerializer<ApplicationCommandType> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("type", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): ApplicationCommandType = when (val code = decoder.decodeInt()) {
            1 -> ChatInput
            2 -> User
            3 -> Message
            else -> Unknown(code)
        }

        override fun serialize(encoder: Encoder, value: ApplicationCommandType) = encoder.encodeInt(value.value)
    }

}

@Serializable
public data class ApplicationCommandOption(
    val type: ApplicationCommandOptionType,
    val name: String,
    @SerialName("name_localizations")
    val nameLocalizations: Optional<Map<Locale, String>?> = Optional.Missing(),
    val description: String,
    @SerialName("description_localizations")
    val descriptionLocalizations: Optional<Map<Locale, String>?> = Optional.Missing(),
    val default: OptionalBoolean = OptionalBoolean.Missing,
    val required: OptionalBoolean = OptionalBoolean.Missing,
    @OptIn(KordExperimental::class)
    val choices: Optional<List<Choice<@Serializable(NotSerializable::class) Any?>>> = Optional.Missing(),
    val autocomplete: OptionalBoolean = OptionalBoolean.Missing,
    val options: Optional<List<ApplicationCommandOption>> = Optional.Missing(),
    @SerialName("channel_types")
    val channelTypes: Optional<List<ChannelType>> = Optional.Missing(),
    @SerialName("min_value")
    val minValue: Optional<JsonPrimitive> = Optional.Missing(),
    @SerialName("max_value")
    val maxValue: Optional<JsonPrimitive> = Optional.Missing(),
)

/**
 * A serializer whose sole purpose is to provide a No-Op serializer for [Any].
 * The serializer is used when the generic type is neither known nor relevant to the serialization process
 *
 * e.g: `Choice<@Serializable(NotSerializable::class) Any?>`
 * The serialization is handled by [Choice] serializer instead where we don't care about the generic type.
 */
@KordExperimental
public object NotSerializable : KSerializer<Any?> {
    override fun deserialize(decoder: Decoder): Nothing = error("This operation is not supported.")
    override val descriptor: SerialDescriptor = String.serializer().descriptor
    override fun serialize(encoder: Encoder, value: Any?): Nothing = error("This operation is not supported.")
}


@Serializable(ApplicationCommandOptionType.Serializer::class)
public sealed class ApplicationCommandOptionType(public val type: Int) {

    public object SubCommand : ApplicationCommandOptionType(1)
    public object SubCommandGroup : ApplicationCommandOptionType(2)
    public object String : ApplicationCommandOptionType(3)
    public object Integer : ApplicationCommandOptionType(4)
    public object Boolean : ApplicationCommandOptionType(5)
    public object User : ApplicationCommandOptionType(6)
    public object Channel : ApplicationCommandOptionType(7)
    public object Role : ApplicationCommandOptionType(8)
    public object Mentionable : ApplicationCommandOptionType(9)
    public object Number : ApplicationCommandOptionType(10)
    public object Attachment : ApplicationCommandOptionType(11)
    public class Unknown(type: Int) : ApplicationCommandOptionType(type)

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
                9 -> Mentionable
                10 -> Number
                11 -> Attachment
                else -> Unknown(type)
            }
        }

        override fun serialize(encoder: Encoder, value: ApplicationCommandOptionType) {
            encoder.encodeInt(value.type)
        }
    }


}

private val LocalizationSerializer =
    Optional.serializer(MapSerializer(Locale.serializer(), String.serializer()).nullable)

@Serializable(Choice.Serializer::class)
public sealed class Choice<out T> {
    public abstract val name: String
    public abstract val nameLocalizations: Optional<Map<Locale, String>?>
    public abstract val value: T

    public data class IntChoice(
        override val name: String,
        override val nameLocalizations: Optional<Map<Locale, String>?>,
        override val value: Long
    ) : Choice<Long>()

    public data class NumberChoice(
        override val name: String,
        override val nameLocalizations: Optional<Map<Locale, String>?>,
        override val value: Double
    ) : Choice<Double>()

    public data class StringChoice(
        override val name: String,
        override val nameLocalizations: Optional<Map<Locale, String>?>,
        override val value: String
    ) : Choice<String>()

    internal object Serializer : KSerializer<Choice<*>> {

        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Choice") {
            element<String>("name")
            element<JsonPrimitive>("value")
            element<Map<Locale, String>?>("name_localizations", isOptional = true)
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {

            lateinit var name: String
            var nameLocalizations: Optional<Map<Locale, String>?> = Optional.Missing()
            lateinit var value: JsonPrimitive

            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> name = decodeStringElement(descriptor, index)
                    1 -> value = decodeSerializableElement(descriptor, index, JsonPrimitive.serializer())
                    2 -> nameLocalizations = decodeSerializableElement(descriptor, index, LocalizationSerializer)

                    CompositeDecoder.DECODE_DONE -> break
                    else -> throw SerializationException("unknown index: $index")
                }
            }

            when {
                value.isString -> StringChoice(name, nameLocalizations, value.content)
                else -> value.longOrNull?.let { IntChoice(name, nameLocalizations, it) }
                    ?: value.doubleOrNull?.let { NumberChoice(name, nameLocalizations, it) }
                    ?: throw SerializationException("Illegal choice value: $value")
            }
        }

        override fun serialize(encoder: Encoder, value: Choice<*>) = encoder.encodeStructure(descriptor) {

            encodeStringElement(descriptor, 0, value.name)

            when (value) {
                is IntChoice -> encodeLongElement(descriptor, 1, value.value)
                is NumberChoice -> encodeDoubleElement(descriptor, 1, value.value)
                is StringChoice -> encodeStringElement(descriptor, 1, value.value)
            }

            if (value.nameLocalizations !is Optional.Missing) {
                encodeSerializableElement(descriptor, 2, LocalizationSerializer, value.nameLocalizations)
            }
        }
    }
}

@Serializable
public data class ResolvedObjects(
    val members: Optional<Map<Snowflake, DiscordInteractionGuildMember>> = Optional.Missing(),
    val users: Optional<Map<Snowflake, DiscordUser>> = Optional.Missing(),
    val roles: Optional<Map<Snowflake, DiscordRole>> = Optional.Missing(),
    val channels: Optional<Map<Snowflake, DiscordChannel>> = Optional.Missing(),
    val messages: Optional<Map<Snowflake, DiscordMessage>> = Optional.Missing(),
    val attachments: Optional<Map<Snowflake, DiscordAttachment>> = Optional.Missing()
)

@Serializable
public data class DiscordInteraction(
    val id: Snowflake,
    @SerialName("application_id")
    val applicationId: Snowflake,
    val type: InteractionType,
    val data: InteractionCallbackData,
    @SerialName("guild_id")
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("channel_id")
    val channelId: Snowflake,
    val member: Optional<DiscordInteractionGuildMember> = Optional.Missing(),
    val user: Optional<DiscordUser> = Optional.Missing(),
    val token: String,
    val version: Int,
    @Serializable(with = MaybeMessageSerializer::class)
    val message: Optional<DiscordMessage> = Optional.Missing(),
    val locale: Optional<Locale> = Optional.Missing(),
    @SerialName("guild_locale")
    val guildLocale: Optional<Locale> = Optional.Missing(),
) {

    /**
     * Serializer that handles incomplete messages in [DiscordInteraction.message]. Discards
     * any incomplete messages as missing optionals.
     */
    private object MaybeMessageSerializer :
        KSerializer<Optional<DiscordMessage>> by Optional.serializer(DiscordMessage.serializer()) {

        override fun deserialize(decoder: Decoder): Optional<DiscordMessage> {
            decoder as JsonDecoder

            val element = decoder.decodeJsonElement().jsonObject

            //check if required fields are present, if not, discard the data
            return if (
                element["channel_id"] == null ||
                element["author"] == null
            ) {
                Optional.Missing()
            } else {
                decoder.json.decodeFromJsonElement(
                    Optional.serializer(DiscordMessage.serializer()), element
                )
            }
        }


    }
}


@Serializable(InteractionType.Serializer::class)
public sealed class InteractionType(public val type: Int) {
    public object Ping : InteractionType(1)
    public object ApplicationCommand : InteractionType(2)

    /*
     * don't trust the docs:
     *
     * this type exists and is needed for components even though it's not documented
     */
    public object Component : InteractionType(3)

    public object AutoComplete : InteractionType(4)
    public object ModalSubmit : InteractionType(5)
    public class Unknown(type: Int) : InteractionType(type)

    override fun toString(): String = when (this) {
        Ping -> "InteractionType.Ping($type)"
        ApplicationCommand -> "InteractionType.ApplicationCommand($type)"
        Component -> "InteractionType.ComponentInvoke($type)"
        AutoComplete -> "InteractionType.AutoComplete($type)"
        ModalSubmit -> "InteractionType.ModalSubmit($type)"
        is Unknown -> "InteractionType.Unknown($type)"
    }

    internal object Serializer : KSerializer<InteractionType> {

        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("InteractionType", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): InteractionType {
            return when (val type = decoder.decodeInt()) {
                1 -> Ping
                2 -> ApplicationCommand
                3 -> Component
                4 -> AutoComplete
                5 -> ModalSubmit
                else -> Unknown(type)
            }
        }

        override fun serialize(encoder: Encoder, value: InteractionType) {
            encoder.encodeInt(value.type)
        }

    }
}

@Serializable
public data class InteractionCallbackData(
    val id: OptionalSnowflake = OptionalSnowflake.Missing,
    val type: Optional<ApplicationCommandType> = Optional.Missing(),
    @SerialName("target_id")
    val targetId: OptionalSnowflake = OptionalSnowflake.Missing,
    val name: Optional<String> = Optional.Missing(),
    val resolved: Optional<ResolvedObjects> = Optional.Missing(),
    val options: Optional<List<Option>> = Optional.Missing(),
    @SerialName("guild_id")
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("custom_id")
    val customId: Optional<String> = Optional.Missing(),
    @SerialName("component_type")
    val componentType: Optional<ComponentType> = Optional.Missing(),
    val values: Optional<List<String>> = Optional.Missing(),
    val components: Optional<List<DiscordComponent>> = Optional.Missing()
)

@Serializable(with = Option.Serializer::class)
public sealed class Option {
    public abstract val name: String
    public abstract val type: ApplicationCommandOptionType

    internal object Serializer : KSerializer<Option> {

        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Kord.Option") {
            element("name", String.serializer().descriptor, isOptional = false)
            element("value", JsonElement.serializer().descriptor, isOptional = true)
            element("options", JsonArray.serializer().descriptor, isOptional = true)
            element("type", ApplicationCommandOptionType.serializer().descriptor, isOptional = false)
            element("focused", String.serializer().descriptor, isOptional = true)
        }

        override fun deserialize(decoder: Decoder): Option {
            decoder as? JsonDecoder ?: error("Option can only be deserialized with a JsonDecoder")
            val json = decoder.json

            var name = ""
            var jsonValue: JsonElement? = null
            var jsonOptions: JsonArray? = null
            var type: ApplicationCommandOptionType? = null
            var focused: OptionalBoolean = OptionalBoolean.Missing
            decoder.decodeStructure(descriptor) {
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> name = decodeStringElement(descriptor, index)
                        1 -> jsonValue = decodeSerializableElement(descriptor, index, JsonElement.serializer())
                        2 -> jsonOptions = decodeSerializableElement(descriptor, index, JsonArray.serializer())
                        3 -> type =
                            decodeSerializableElement(descriptor, index, ApplicationCommandOptionType.serializer())
                        4 -> focused =
                            decodeSerializableElement(descriptor, index, OptionalBoolean.serializer())
                        CompositeDecoder.DECODE_DONE -> return@decodeStructure
                        else -> throw SerializationException("unknown index: $index")
                    }
                }
            }

            requireNotNull(type) { "'type' expected for $name but was absent" }

            return when (type) {
                ApplicationCommandOptionType.SubCommand -> {
                    val options = if (jsonOptions == null) Optional.Missing()
                    else Optional.Value(jsonOptions!!.map {
                        json.decodeFromJsonElement(serializer(), it) as CommandArgument<*>
                    })

                    SubCommand(name, options)
                }

                ApplicationCommandOptionType.SubCommandGroup -> {
                    val options = if (jsonOptions == null) Optional.Missing()
                    else Optional.Value(jsonOptions!!.map {
                        json.decodeFromJsonElement(serializer(), it) as SubCommand
                    })

                    CommandGroup(name, options)
                }
                ApplicationCommandOptionType.Boolean,
                ApplicationCommandOptionType.Channel,
                ApplicationCommandOptionType.Integer,
                ApplicationCommandOptionType.Number,
                ApplicationCommandOptionType.Mentionable,
                ApplicationCommandOptionType.Role,
                ApplicationCommandOptionType.String,
                ApplicationCommandOptionType.Attachment,
                ApplicationCommandOptionType.User -> CommandArgument.Serializer.deserialize(
                    json, jsonValue!!, name, type!!, focused
                )
                else -> error("unknown ApplicationCommandOptionType $type")
            }
        }

        override fun serialize(encoder: Encoder, value: Option) {
            when (value) {
                is CommandArgument<*> -> CommandArgument.Serializer.serialize(encoder, value)
                is CommandGroup -> encoder.encodeStructure(descriptor) {
                    encodeSerializableElement(
                        descriptor, 0, String.serializer(), value.name
                    )
                    encodeSerializableElement(
                        descriptor, 2, Optional.serializer(ListSerializer(Serializer)), value.options
                    )

                    encodeSerializableElement(
                        descriptor, 3, ApplicationCommandOptionType.serializer(), value.type
                    )
                }
                is SubCommand -> encoder.encodeStructure(descriptor) {
                    encodeSerializableElement(
                        descriptor, 0, String.serializer(), value.name
                    )
                    encodeSerializableElement(
                        descriptor, 2, Optional.serializer(ListSerializer(Serializer)), value.options
                    )

                    encodeSerializableElement(
                        descriptor, 3, ApplicationCommandOptionType.serializer(), value.type
                    )
                }
            }
        }
    }
}

@Serializable
public data class SubCommand(
    override val name: String,
    val options: Optional<List<CommandArgument<@Contextual Any?>>> = Optional.Missing()
) : Option() {
    override val type: ApplicationCommandOptionType
        get() = ApplicationCommandOptionType.SubCommand
}


@Serializable(with = CommandArgument.Serializer::class)
public sealed class CommandArgument<out T> : Option() {

    public abstract val value: T
    public abstract val focused: OptionalBoolean

    public data class StringArgument(
        override val name: String,
        override val value: String,
        override val focused: OptionalBoolean = OptionalBoolean.Missing
    ) : CommandArgument<String>() {
        override val type: ApplicationCommandOptionType
            get() = ApplicationCommandOptionType.String
    }

    public data class IntegerArgument(
        override val name: String,
        override val value: Long,
        override val focused: OptionalBoolean = OptionalBoolean.Missing
    ) : CommandArgument<Long>() {
        override val type: ApplicationCommandOptionType
            get() = ApplicationCommandOptionType.Integer
    }

    public data class NumberArgument(
        override val name: String,
        override val value: Double,
        override val focused: OptionalBoolean = OptionalBoolean.Missing
    ) : CommandArgument<Double>() {
        override val type: ApplicationCommandOptionType
            get() = ApplicationCommandOptionType.Number
    }

    public data class BooleanArgument(
        override val name: String,
        override val value: Boolean,
        override val focused: OptionalBoolean = OptionalBoolean.Missing
    ) : CommandArgument<Boolean>() {
        override val type: ApplicationCommandOptionType
            get() = ApplicationCommandOptionType.Boolean
    }

    public data class UserArgument(
        override val name: String,
        override val value: Snowflake,
        override val focused: OptionalBoolean = OptionalBoolean.Missing
    ) : CommandArgument<Snowflake>() {
        override val type: ApplicationCommandOptionType
            get() = ApplicationCommandOptionType.User
    }

    public data class ChannelArgument(
        override val name: String,
        override val value: Snowflake,
        override val focused: OptionalBoolean = OptionalBoolean.Missing
    ) : CommandArgument<Snowflake>() {
        override val type: ApplicationCommandOptionType
            get() = ApplicationCommandOptionType.Channel
    }

    public data class RoleArgument(
        override val name: String,
        override val value: Snowflake,
        override val focused: OptionalBoolean = OptionalBoolean.Missing
    ) : CommandArgument<Snowflake>() {
        override val type: ApplicationCommandOptionType
            get() = ApplicationCommandOptionType.Role
    }

    public data class MentionableArgument(
        override val name: String,
        override val value: Snowflake,
        override val focused: OptionalBoolean = OptionalBoolean.Missing
    ) : CommandArgument<Snowflake>() {
        override val type: ApplicationCommandOptionType
            get() = ApplicationCommandOptionType.Mentionable
    }

    public data class AttachmentArgument(
        override val name: String,
        override val value: Snowflake,
        override val focused: OptionalBoolean = OptionalBoolean.Missing
    ) : CommandArgument<Snowflake>() {
        override val type: ApplicationCommandOptionType
            get() = ApplicationCommandOptionType.Attachment
    }

    /**
     * Representation of a partial user input of an auto completed argument.
     *
     * @property name the name of the property
     * @property type the type of the backing argument (not the type of [value] as the user can enter anything)
     * @property value whatever the user already typed into the argument field
     * @property focused always true, since this is an auto complete argument
     */
    public data class AutoCompleteArgument(
        override val name: String,
        override val type: ApplicationCommandOptionType,
        override val value: String,
        override val focused: OptionalBoolean
    ) : CommandArgument<String>()

    internal object Serializer : KSerializer<CommandArgument<*>> {

        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("kord.CommandArgument") {
            element("name", String.serializer().descriptor)
            element("value", JsonElement.serializer().descriptor)
            element("type", ApplicationCommandOptionType.serializer().descriptor)
        }

        override fun serialize(encoder: Encoder, value: CommandArgument<*>) {
            encoder.encodeStructure(descriptor) {
                encodeStringElement(descriptor, 0, value.name)
                encodeSerializableElement(descriptor, 2, ApplicationCommandOptionType.serializer(), value.type)
                when (value) {
                    is BooleanArgument -> encodeBooleanElement(descriptor, 1, value.value)
                    is ChannelArgument -> encodeSerializableElement(
                        descriptor,
                        1,
                        Snowflake.serializer(),
                        value.value
                    )
                    is RoleArgument -> encodeSerializableElement(
                        descriptor,
                        1,
                        Snowflake.serializer(),
                        value.value
                    )
                    is MentionableArgument -> encodeSerializableElement(
                        descriptor,
                        1,
                        Snowflake.serializer(),
                        value.value
                    )
                    is UserArgument -> encodeSerializableElement(
                        descriptor,
                        1,
                        Snowflake.serializer(),
                        value.value
                    )
                    is IntegerArgument -> encodeLongElement(descriptor, 1, value.value)
                    is NumberArgument -> encodeDoubleElement(descriptor, 1, value.value)
                    is AttachmentArgument -> encodeSerializableElement(
                        descriptor,
                        1,
                        Snowflake.serializer(),
                        value.value
                    )
                    is AutoCompleteArgument, is StringArgument -> encodeStringElement(
                        descriptor,
                        1,
                        value.value as String
                    )
                }
            }
        }

        fun deserialize(
            json: Json,
            element: JsonElement,
            name: String,
            type: ApplicationCommandOptionType,
            focused: OptionalBoolean
        ): CommandArgument<*> {
            // Discord allows the user to put anything into auto complete,
            // so we cannot deserialize this with the expected type
            if (focused.value == true) {
                return AutoCompleteArgument(
                    name, type, json.decodeFromJsonElement(String.serializer(), element), focused
                )
            }
            return when (type) {
                ApplicationCommandOptionType.Boolean -> BooleanArgument(
                    name, json.decodeFromJsonElement(Boolean.serializer(), element), focused
                )
                ApplicationCommandOptionType.String -> StringArgument(
                    name, json.decodeFromJsonElement(String.serializer(), element), focused
                )
                ApplicationCommandOptionType.Integer -> IntegerArgument(
                    name, json.decodeFromJsonElement(Long.serializer(), element), focused
                )

                ApplicationCommandOptionType.Number -> NumberArgument(
                    name, json.decodeFromJsonElement(Double.serializer(), element), focused
                )
                ApplicationCommandOptionType.Channel -> ChannelArgument(
                    name, json.decodeFromJsonElement(Snowflake.serializer(), element), focused
                )
                ApplicationCommandOptionType.Mentionable -> MentionableArgument(
                    name, json.decodeFromJsonElement(Snowflake.serializer(), element), focused
                )
                ApplicationCommandOptionType.Role -> RoleArgument(
                    name, json.decodeFromJsonElement(Snowflake.serializer(), element), focused
                )
                ApplicationCommandOptionType.User -> UserArgument(
                    name, json.decodeFromJsonElement(Snowflake.serializer(), element), focused
                )
                ApplicationCommandOptionType.Attachment -> AttachmentArgument(
                    name, json.decodeFromJsonElement(Snowflake.serializer(), element), focused
                )
                ApplicationCommandOptionType.SubCommand,
                ApplicationCommandOptionType.SubCommandGroup,
                is ApplicationCommandOptionType.Unknown -> error("unknown CommandArgument type ${type.type}")
            }
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            this as JsonDecoder

            var name = ""
            var element: JsonElement? = null
            var type: ApplicationCommandOptionType? = null
            while (true) {
                when (val index = decodeElementIndex(Option.Serializer.descriptor)) {
                    0 -> name = decodeSerializableElement(descriptor, index, String.serializer())
                    1 -> element = decodeSerializableElement(descriptor, index, JsonElement.serializer())
                    2 -> type = decodeSerializableElement(
                        descriptor,
                        index,
                        ApplicationCommandOptionType.serializer()
                    )

                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("unknown index: $index")
                }
            }

            requireNotNull(element)
            requireNotNull(type)
            deserialize(json, element, name, type, OptionalBoolean.Missing)
        }
    }
}

public data class CommandGroup(
    override val name: String,
    val options: Optional<List<SubCommand>> = Optional.Missing(),
) : Option() {
    override val type: ApplicationCommandOptionType
        get() = ApplicationCommandOptionType.SubCommandGroup
}

public fun CommandArgument<*>.int(): Long {
    return value as? Long ?: error("$value wasn't an int.")
}


public fun CommandArgument<*>.string(): String {
    return value.toString()
}


public fun CommandArgument<*>.boolean(): Boolean {
    return value as? Boolean ?: error("$value wasn't a Boolean.")
}


public fun CommandArgument<*>.snowflake(): Snowflake {
    val id = string().toULongOrNull() ?: error("$value wasn't a Snowflake")
    return Snowflake(id)
}

@Serializable(InteractionResponseType.Serializer::class)

public sealed class InteractionResponseType(public val type: Int) {
    public object Pong : InteractionResponseType(1)
    public object ChannelMessageWithSource : InteractionResponseType(4)
    public object DeferredChannelMessageWithSource : InteractionResponseType(5)
    public object DeferredUpdateMessage : InteractionResponseType(6)
    public object UpdateMessage : InteractionResponseType(7)
    public object ApplicationCommandAutoCompleteResult : InteractionResponseType(8)
    public object Modal : InteractionResponseType(9)
    public class Unknown(type: Int) : InteractionResponseType(type)

    internal object Serializer : KSerializer<InteractionResponseType> {

        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("InteractionResponseType", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): InteractionResponseType {
            return when (val type = decoder.decodeInt()) {
                1 -> Pong
                4 -> ChannelMessageWithSource
                5 -> DeferredChannelMessageWithSource
                6 -> DeferredUpdateMessage
                7 -> UpdateMessage
                8 -> ApplicationCommandAutoCompleteResult
                9 -> Modal
                else -> Unknown(type)
            }
        }

        override fun serialize(encoder: Encoder, value: InteractionResponseType) {
            encoder.encodeInt(value.type)
        }
    }
}


@Serializable
public data class DiscordGuildApplicationCommandPermissions(
    val id: Snowflake,
    @SerialName("application_id")
    val applicationId: Snowflake,
    @SerialName("guild_id")
    val guildId: Snowflake,
    val permissions: List<DiscordGuildApplicationCommandPermission>
)

@Serializable
public data class DiscordGuildApplicationCommandPermission(
    val id: Snowflake,
    val type: Type,
    val permission: Boolean
) {
    @Serializable(with = Type.Serializer::class)
    public sealed class Type(public val value: Int) {
        public object Role : Type(1)
        public object User : Type(2)
        public object Channel : Type(3)
        public class Unknown(value: Int) : Type(value)

        public object Serializer : KSerializer<Type> {
            override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("type", PrimitiveKind.INT)

            override fun deserialize(decoder: Decoder): Type =
                when (val value = decoder.decodeInt()) {
                    1 -> Role
                    2 -> User
                    3 -> Channel
                    else -> Unknown(value)
                }

            override fun serialize(encoder: Encoder, value: Type): Unit = encoder.encodeInt(value.value)
        }
    }
}

@Serializable
public data class DiscordAutoComplete<T>(
    val choices: List<Choice<T>>
)

@Serializable
public data class DiscordModal(
    val title: String,
    @SerialName("custom_id")
    val customId: String,
    val components: List<DiscordComponent>,
)
