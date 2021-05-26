package dev.kord.common.entity

import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.optional
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
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
    @SerialName("default_permission")
    val defaultPermission: OptionalBoolean = OptionalBoolean.Missing
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
    object Mentionable : ApplicationCommandOptionType(9)
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

@Serializable(with = DiscordInteraction.Serializer::class)
@KordPreview
data class DiscordInteraction(
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
    val message: Optional<DiscordMessage> = Optional.Missing()
) {
    companion object Serializer : KSerializer<DiscordInteraction> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Interaction") {
            element<Snowflake>("id")
            element<Snowflake>("application_id")
            element<InteractionType>("type")
            element<InteractionCallbackData>("data")
            element<OptionalSnowflake>("guild_id")
            element<Snowflake>("channel_id")
            element<Optional<DiscordInteractionGuildMember>>("member")
            element<Optional<DiscordUser>>("user")
            element<String>("token")
            element<Int>("version")
            element<Optional<DiscordMessage>>("message")
        }

        override fun deserialize(decoder: Decoder): DiscordInteraction {
            var id: Snowflake? = null
            var applicationId: Snowflake? = null
            var type: InteractionType? = null
            var data: InteractionCallbackData? = null
            var guildId: OptionalSnowflake? = null
            var channelId: Snowflake? = null
            var member: Optional<DiscordInteractionGuildMember>? = null
            var user: Optional<DiscordUser>? = null
            var token: String? = null
            var version: Int? = null
            var message: Optional<DiscordMessage>? = null

            return decoder.decodeStructure(descriptor) {
                while (true) {
                    when (decodeElementIndex(descriptor)) {
                        0 -> id = decodeSerializableElement(descriptor, 0, Snowflake.serializer())
                        1 -> applicationId = decodeSerializableElement(descriptor, 1, Snowflake.serializer())
                        2 -> type = decodeSerializableElement(descriptor, 2, InteractionType.serializer())
                        3 -> {
                            data = when (val safeType = type ?: error("Missing ComponentType")) {
                                is InteractionType.ApplicationCommand -> decodeSerializableElement(
                                    descriptor,
                                    3,
                                    DiscordApplicationCommandInteractionData.serializer()
                                )
                                is InteractionType.Ping -> decodeSerializableElement(
                                    descriptor,
                                    3,
                                    DiscordApplicationCommandInteractionData.serializer()
                                )
                                is InteractionType.Component -> decodeSerializableElement(
                                    descriptor,
                                    3,
                                    DiscordApplicationComponentCallbackData.serializer()
                                )
                                else -> error("Unknown ComponentType: $safeType")
                            }
                        }
                        4 -> guildId = decodeSerializableElement(descriptor, 4, OptionalSnowflake.serializer())
                        5 -> channelId = decodeSerializableElement(descriptor, 5, Snowflake.serializer())
                        6 -> member = decodeSerializableElement(
                            descriptor,
                            6,
                            Optional.OptionalSerializer(DiscordInteractionGuildMember.serializer())
                        )
                        7 -> user =
                            decodeSerializableElement(
                                descriptor,
                                7,
                                Optional.OptionalSerializer(DiscordUser.serializer())
                            )
                        8 -> token = decodeStringElement(descriptor, 8)
                        9 -> version = decodeIntElement(descriptor, 9)
                        10 -> message = decodeSerializableElement(
                            descriptor,
                            7,
                            Optional.OptionalSerializer(DiscordMessage.serializer())
                        )
                        CompositeDecoder.DECODE_DONE -> break
                    }
                }

                DiscordInteraction(
                    id!!,
                    applicationId!!,
                    type!!,
                    data!!,
                    guildId!!,
                    channelId!!,
                    member ?: Optional.Missing(),
                    user ?: Optional.Missing(),
                    token!!,
                    version!!,
                    message ?: Optional.Missing(),
                )
            }
        }

        override fun serialize(encoder: Encoder, value: DiscordInteraction) {
            encoder.encodeStructure(descriptor) {
                encodeSerializableElement(descriptor, 0, Snowflake.serializer(), value.id)
                encodeSerializableElement(descriptor, 1, Snowflake.serializer(), value.applicationId)
                encodeSerializableElement(descriptor, 2, InteractionType.serializer(), value.type)
                when (value.data) {
                    is DiscordApplicationCommandInteractionData -> encodeSerializableElement(
                        descriptor, 3, DiscordApplicationCommandInteractionData.serializer(),
                        value.data
                    )
                    is DiscordApplicationComponentCallbackData -> encodeSerializableElement(
                        descriptor, 3, DiscordApplicationComponentCallbackData.serializer(),
                        value.data
                    )
                    else -> error("Cannot encode unknown interaction type")
                }
                encodeSerializableElement(descriptor, 4, OptionalSnowflake.serializer(), value.guildId)
                encodeSerializableElement(descriptor, 5, Snowflake.serializer(), value.channelId)
                encodeSerializableElement(
                    descriptor,
                    6,
                    Optional.OptionalSerializer(DiscordInteractionGuildMember.serializer()),
                    value.member
                )
                encodeSerializableElement(
                    descriptor,
                    7,
                    Optional.OptionalSerializer(DiscordUser.serializer()),
                    value.user
                )
                encodeStringElement(descriptor, 8, value.token)
                encodeIntElement(descriptor, 9, value.version)
                encodeSerializableElement(
                    descriptor,
                    10,
                    Optional.OptionalSerializer(DiscordMessage.serializer()),
                    value.message
                )
            }
        }
    }
}

@Serializable(InteractionType.Serializer::class)
@KordPreview
sealed class InteractionType(val type: Int) {
    object Ping : InteractionType(1)
    object ApplicationCommand : InteractionType(2)

    /**
     * don't trust the docs:
     *
     * this type exists and is needed for components even though it's not documented
     */
    object Component : InteractionType(3)
    class Unknown(type: Int) : InteractionType(type)

    override fun toString(): String = when (this) {
        Ping -> "InteractionType.Ping($type)"
        ApplicationCommand -> "InteractionType.ApplicationCommand($type)"
        Component -> "InteractionType.ComponentInvoke($type)"
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
                3 -> Component
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
) : InteractionCallbackData

@Serializable(with = Option.Serializer::class)
@KordPreview
sealed class Option {
    abstract val name: String
    abstract val type: ApplicationCommandOptionType

    internal object Serializer : KSerializer<Option> {

        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Kord.Option") {
            element("name", String.serializer().descriptor, isOptional = false)
            element("value", JsonElement.serializer().descriptor, isOptional = true)
            element("options", JsonArray.serializer().descriptor, isOptional = true)
            element("type", ApplicationCommandOptionType.serializer().descriptor, isOptional = false)
        }

        override fun deserialize(decoder: Decoder): Option {
            decoder as? JsonDecoder ?: error("Option can only be deserialize with a JsonDecoder")
            val json = decoder.json

            var name = ""
            var jsonValue: JsonElement? = null
            var jsonOptions: JsonArray? = null
            var type: ApplicationCommandOptionType? = null
            decoder.decodeStructure(descriptor) {
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> name = decodeStringElement(descriptor, index)
                        1 -> jsonValue = decodeSerializableElement(descriptor, index, JsonElement.serializer())
                        2 -> jsonOptions = decodeSerializableElement(descriptor, index, JsonArray.serializer())
                        3 -> type =
                            decodeSerializableElement(descriptor, index, ApplicationCommandOptionType.serializer())

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
                ApplicationCommandOptionType.Mentionable,
                ApplicationCommandOptionType.Role,
                ApplicationCommandOptionType.String,
                ApplicationCommandOptionType.User -> CommandArgument.Serializer.deserialize(
                    json, jsonValue!!, name, type!!
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
@KordPreview
data class SubCommand(
    override val name: String,
    val options: Optional<List<CommandArgument<@Contextual Any?>>> = Optional.Missing()
) : Option() {
    override val type: ApplicationCommandOptionType
        get() = ApplicationCommandOptionType.SubCommand
}

@KordPreview
@Serializable(with = CommandArgument.Serializer::class)
sealed class CommandArgument<out T> : Option() {

    abstract val value: T

    class StringArgument(
        override val name: String,
        override val value: String
    ) : CommandArgument<String>() {
        override val type: ApplicationCommandOptionType
            get() = ApplicationCommandOptionType.String

        override fun toString(): String = "StringArgument(name=$name, value=$value)"
    }

    class IntegerArgument(
        override val name: String,
        override val value: Int
    ) : CommandArgument<Int>() {
        override val type: ApplicationCommandOptionType
            get() = ApplicationCommandOptionType.Integer

        override fun toString(): String = "IntegerArgument(name=$name, value=$value)"
    }

    class BooleanArgument(
        override val name: String,
        override val value: Boolean
    ) : CommandArgument<Boolean>() {
        override val type: ApplicationCommandOptionType
            get() = ApplicationCommandOptionType.Boolean

        override fun toString(): String = "BooleanArgument(name=$name, value=$value)"
    }

    class UserArgument(
        override val name: String,
        override val value: Snowflake
    ) : CommandArgument<Snowflake>() {
        override val type: ApplicationCommandOptionType
            get() = ApplicationCommandOptionType.User

        override fun toString(): String = "UserArgument(name=$name, value=$value)"
    }

    class ChannelArgument(
        override val name: String,
        override val value: Snowflake
    ) : CommandArgument<Snowflake>() {
        override val type: ApplicationCommandOptionType
            get() = ApplicationCommandOptionType.Channel

        override fun toString(): String = "ChannelArgument(name=$name, value=$value)"
    }

    class RoleArgument(
        override val name: String,
        override val value: Snowflake
    ) : CommandArgument<Snowflake>() {
        override val type: ApplicationCommandOptionType
            get() = ApplicationCommandOptionType.Role

        override fun toString(): String = "RoleArgument(name=$name, value=$value)"
    }

    class MentionableArgument(
        override val name: String,
        override val value: Snowflake
    ) : CommandArgument<Snowflake>() {
        override val type: ApplicationCommandOptionType
            get() = ApplicationCommandOptionType.Mentionable

        override fun toString(): String = "MentionableArgument(name=$name, value=$value)"
    }

    internal object Serializer : KSerializer<CommandArgument<*>> {

        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("kord.CommandArgument") {
            element("name", String.serializer().descriptor)
            element("value", JsonElement.serializer().descriptor)
            element("type", ApplicationCommandOptionType.serializer().descriptor)
        }

        override fun serialize(encoder: Encoder, value: CommandArgument<*>) {
            encoder.encodeStructure(descriptor) {
                encodeStringElement(descriptor, 0, value.name)
                encodeSerializableElement(descriptor, 1, ApplicationCommandOptionType.serializer(), value.type)
                when (value) {
                    is BooleanArgument -> encodeBooleanElement(descriptor, 3, value.value)
                    is ChannelArgument -> encodeSerializableElement(
                        descriptor,
                        3,
                        Snowflake.serializer(),
                        value.value
                    )
                    is RoleArgument -> encodeSerializableElement(
                        descriptor,
                        3,
                        Snowflake.serializer(),
                        value.value
                    )
                    is MentionableArgument -> encodeSerializableElement(
                        descriptor,
                        3,
                        Snowflake.serializer(),
                        value.value
                    )
                    is UserArgument -> encodeSerializableElement(
                        descriptor,
                        3,
                        Snowflake.serializer(),
                        value.value
                    )
                    is IntegerArgument -> encodeIntElement(descriptor, 3, value.value)
                    is StringArgument -> encodeStringElement(descriptor, 3, value.value)
                }
            }
        }

        fun deserialize(
            json: Json,
            element: JsonElement,
            name: String,
            type: ApplicationCommandOptionType
        ): CommandArgument<*> = when (type) {
            ApplicationCommandOptionType.Boolean -> BooleanArgument(
                name, json.decodeFromJsonElement(Boolean.serializer(), element)
            )
            ApplicationCommandOptionType.String -> StringArgument(
                name, json.decodeFromJsonElement(String.serializer(), element)
            )
            ApplicationCommandOptionType.Integer -> IntegerArgument(
                name, json.decodeFromJsonElement(Int.serializer(), element)
            )
            ApplicationCommandOptionType.Channel -> ChannelArgument(
                name, json.decodeFromJsonElement(Snowflake.serializer(), element)
            )
            ApplicationCommandOptionType.Mentionable -> MentionableArgument(
                name, json.decodeFromJsonElement(Snowflake.serializer(), element)
            )
            ApplicationCommandOptionType.Role -> RoleArgument(
                name, json.decodeFromJsonElement(Snowflake.serializer(), element)
            )
            ApplicationCommandOptionType.User -> UserArgument(
                name, json.decodeFromJsonElement(Snowflake.serializer(), element)
            )
            ApplicationCommandOptionType.SubCommand,
            ApplicationCommandOptionType.SubCommandGroup,
            is ApplicationCommandOptionType.Unknown -> error("unknown CommandArgument type ${type.type}")
        }

        override fun deserialize(decoder: Decoder): CommandArgument<*> {
            decoder.decodeStructure(descriptor) {
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
                return deserialize(json, element, name, type)
            }
        }
    }
}

@KordPreview
data class CommandGroup(
    override val name: String,
    val options: Optional<List<SubCommand>> = Optional.Missing(),
) : Option() {
    override val type: ApplicationCommandOptionType
        get() = ApplicationCommandOptionType.SubCommandGroup
}

@KordPreview
fun CommandArgument<*>.int(): Int {
    return value as? Int ?: error("$value wasn't an Int.")
}

@KordPreview
fun CommandArgument<*>.string(): String {
    return value.toString()
}

@KordPreview
fun CommandArgument<*>.boolean(): Boolean {
    return value as? Boolean ?: error("$value wasn't a Boolean.")
}

@KordPreview
fun CommandArgument<*>.snowflake(): Snowflake {
    val id = string().toLongOrNull() ?: error("$value wasn't a Snowflake")
    return Snowflake(id)
}


@Serializable(InteractionResponseType.Serializer::class)
@KordPreview
sealed class InteractionResponseType(val type: Int) {
    object Pong : InteractionResponseType(1)
    object ChannelMessageWithSource : InteractionResponseType(4)
    object DeferredChannelMessageWithSource : InteractionResponseType(5)
    object DeferredUpdateMessage : InteractionResponseType(6)
    object UpdateMessage : InteractionResponseType(7)
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
                6 -> DeferredUpdateMessage
                7 -> UpdateMessage
                else -> Unknown(type)
            }
        }

        override fun serialize(encoder: Encoder, value: InteractionResponseType) {
            encoder.encodeInt(value.type)
        }

    }
}


@KordPreview
@Serializable
data class DiscordGuildApplicationCommandPermissions(
    val id: Snowflake,
    @SerialName("application_id")
    val applicationId: Snowflake,
    @SerialName("guild_id")
    val guildId: Snowflake,
    val permissions: List<DiscordGuildApplicationCommandPermission>
)

@KordPreview
@Serializable
data class PartialDiscordGuildApplicationCommandPermissions(
    val id: Snowflake,
    val permissions: List<DiscordGuildApplicationCommandPermission>
)

@KordPreview
@Serializable
data class DiscordGuildApplicationCommandPermission(
    val id: Snowflake,
    val type: Type,
    val permission: Boolean
) {
    @Serializable(with = Type.Serializer::class)
    sealed class Type(val value: Int) {
        object Role : Type(1)
        object User : Type(2)
        class Unknown(value: Int) : Type(value)

        object Serializer : KSerializer<Type> {
            override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("type", PrimitiveKind.INT)

            override fun deserialize(decoder: Decoder): Type =
                when (val value = decoder.decodeInt()) {
                    1 -> Role
                    2 -> User
                    else -> Unknown(value)
                }

            override fun serialize(encoder: Encoder, value: Type) = encoder.encodeInt(value.value)
        }
    }
}

interface InteractionCallbackData
