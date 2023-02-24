@file:GenerateKordEnum(
    name = "ApplicationCommandType", valueType = INT,
    docUrl = "https://discord.com/developers/docs/interactions/application-commands#application-command-object-application-command-types",
    entries = [
        Entry("ChatInput", intValue = 1, kDoc = "A text-based command that shows up when a user types `/`."),
        Entry("User", intValue = 2, kDoc = "A UI-based command that shows up when you right-click or tap on a user."),
        Entry(
            "Message", intValue = 3,
            kDoc = "A UI-based command that shows up when you right-click or tap on a message.",
        ),
    ],
)

@file:GenerateKordEnum(
    name = "ApplicationCommandOptionType", valueType = INT, valueName = "type",
    docUrl = "https://discord.com/developers/docs/interactions/application-commands#application-command-object-application-command-option-type",
    entries = [
        Entry("SubCommand", intValue = 1),
        Entry("SubCommandGroup", intValue = 2),
        Entry("String", intValue = 3),
        Entry("Integer", intValue = 4, kDoc = "Any integer between `-2^53` and `2^53`."),
        Entry("Boolean", intValue = 5),
        Entry("User", intValue = 6),
        Entry("Channel", intValue = 7, kDoc = "Includes all channel types + categories."),
        Entry("Role", intValue = 8),
        Entry("Mentionable", intValue = 9, kDoc = "Includes users and roles."),
        Entry("Number", intValue = 10, kDoc = "Any double between `-2^53` and `2^53`."),
        Entry("Attachment", intValue = 11),
    ],
)

@file:GenerateKordEnum(
    name = "InteractionType", valueType = INT, valueName = "type",
    docUrl = "https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-object-interaction-type",
    entries = [
        Entry("Ping", intValue = 1),
        Entry("ApplicationCommand", intValue = 2),
        Entry("Component", intValue = 3),
        Entry("AutoComplete", intValue = 4),
        Entry("ModalSubmit", intValue = 5),
    ],
)

@file:GenerateKordEnum(
    name = "InteractionResponseType", valueType = INT, valueName = "type",
    docUrl = "https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-response-object-interaction-callback-type",
    entries = [
        Entry("Pong", intValue = 1, kDoc = "ACK a [Ping][dev.kord.common.entity.InteractionType.Ping]."),
        Entry("ChannelMessageWithSource", intValue = 4, kDoc = "Respond to an interaction with a message."),
        Entry(
            "DeferredChannelMessageWithSource", intValue = 5,
            kDoc = "ACK an interaction and edit a response later, the user sees a loading state.",
        ),
        Entry(
            "DeferredUpdateMessage", intValue = 6,
            kDoc = "For components, ACK an interaction and edit the original message later; the user does not see a " +
                    "loading state.",
        ),
        Entry("UpdateMessage", intValue = 7, kDoc = "For components, edit the message the component was attached to."),
        Entry(
            "ApplicationCommandAutoCompleteResult", intValue = 8,
            kDoc = "Respond to an autocomplete interaction with suggested choices.",
        ),
        Entry("Modal", intValue = 9, kDoc = "Respond to an interaction with a popup modal."),
    ],
)

@file:GenerateKordEnum(
    name = "ApplicationCommandPermissionType", valueType = INT,
    docUrl = "https://discord.com/developers/docs/interactions/application-commands#application-command-permissions-object-application-command-permission-type",
    entries = [
        Entry("Role", intValue = 1),
        Entry("User", intValue = 2),
        Entry("Channel", intValue = 3),
    ],
)

package dev.kord.common.entity

import dev.kord.common.Locale
import dev.kord.common.annotation.KordExperimental
import dev.kord.common.entity.optional.*
import dev.kord.ksp.GenerateKordEnum
import dev.kord.ksp.GenerateKordEnum.Entry
import dev.kord.ksp.GenerateKordEnum.ValueType.INT
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*
import kotlin.DeprecationLevel.ERROR
import kotlin.jvm.JvmName

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
    val defaultMemberPermissions: Permissions?,
    @SerialName("dm_permission")
    val dmPermission: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("default_permission")
    @Deprecated("'defaultPermission' is deprecated in favor of 'defaultMemberPermissions' and 'dmPermission'.")
    val defaultPermission: OptionalBoolean? = OptionalBoolean.Missing,
    val nsfw: OptionalBoolean = OptionalBoolean.Missing,
    val version: Snowflake
)

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
    val choices: Optional<List<Choice<@Serializable(NotSerializable::class) Any?>>> = Optional.Missing(),
    val autocomplete: OptionalBoolean = OptionalBoolean.Missing,
    val options: Optional<List<ApplicationCommandOption>> = Optional.Missing(),
    @SerialName("channel_types")
    val channelTypes: Optional<List<ChannelType>> = Optional.Missing(),
    @SerialName("min_value")
    val minValue: Optional<JsonPrimitive> = Optional.Missing(),
    @SerialName("max_value")
    val maxValue: Optional<JsonPrimitive> = Optional.Missing(),
    @SerialName("min_length")
    val minLength: OptionalInt = OptionalInt.Missing,
    @SerialName("max_length")
    val maxLength: OptionalInt = OptionalInt.Missing
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


private val LocalizationSerializer =
    Optional.serializer(MapSerializer(Locale.serializer(), String.serializer()).nullable)

@Serializable(Choice.Serializer::class)
public sealed class Choice<out T> {
    public abstract val name: String
    public abstract val nameLocalizations: Optional<Map<Locale, String>?>
    public abstract val value: T

    @Deprecated("Renamed to 'IntegerChoice'.", level = ERROR)
    public data class IntChoice
    @Deprecated(
        "Renamed to 'IntegerChoice'.",
        ReplaceWith("IntegerChoice(name, nameLocalizations, value)", "dev.kord.common.entity.Choice.IntegerChoice"),
        level = ERROR,
    ) public constructor(
        override val name: String,
        override val nameLocalizations: Optional<Map<Locale, String>?>,
        override val value: Long
    ) : Choice<Long>()

    public data class IntegerChoice(
        override val name: String,
        override val nameLocalizations: Optional<Map<Locale, String>?>,
        override val value: Long,
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
                else -> value.longOrNull?.let { IntegerChoice(name, nameLocalizations, it) }
                    ?: value.doubleOrNull?.let { NumberChoice(name, nameLocalizations, it) }
                    ?: throw SerializationException("Illegal choice value: $value")
            }
        }

        override fun serialize(encoder: Encoder, value: Choice<*>) = encoder.encodeStructure(descriptor) {

            encodeStringElement(descriptor, 0, value.name)

            when (value) {
                is @Suppress("DEPRECATION_ERROR") IntChoice -> encodeLongElement(descriptor, 1, value.value)
                is IntegerChoice -> encodeLongElement(descriptor, 1, value.value)
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
    @SerialName("app_permissions")
    val appPermissions: Optional<Permissions> = Optional.Missing(),
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
                null, is ApplicationCommandOptionType.Unknown -> error("unknown ApplicationCommandOptionType $type")
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

@Deprecated(
    "Use an is-check or cast instead.",
    ReplaceWith("(this as CommandArgument.IntegerArgument).value", "dev.kord.common.entity.CommandArgument"),
    level = ERROR,
)
public fun CommandArgument<*>.int(): Long {
    return value as? Long ?: error("$value wasn't an int.")
}


@Deprecated(
    "This function calls value.toString() which might be unexpected. Use an explicit value.toString() instead.",
    ReplaceWith("this.value.toString()"),
    level = ERROR,
)
public fun CommandArgument<*>.string(): String {
    return value.toString()
}


@Deprecated(
    "Use an is-check or cast instead.",
    ReplaceWith("(this as CommandArgument.BooleanArgument).value", "dev.kord.common.entity.CommandArgument"),
    level = ERROR,
)
public fun CommandArgument<*>.boolean(): Boolean {
    return value as? Boolean ?: error("$value wasn't a Boolean.")
}


@Deprecated(
    "This function calls value.toString() which might be unexpected. Use an explicit value.toString() instead.",
    ReplaceWith("Snowflake(this.value.toString())", "dev.kord.common.entity.Snowflake"),
    level = ERROR,
)
public fun CommandArgument<*>.snowflake(): Snowflake {
    val id = value.toString().toULongOrNull() ?: error("$value wasn't a Snowflake")
    return Snowflake(id)
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
    val type: ApplicationCommandPermissionType,
    val permission: Boolean
)

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
