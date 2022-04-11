package dev.kord.core.cache.data

import dev.kord.common.Locale
import dev.kord.common.annotation.KordExperimental
import dev.kord.common.entity.*
import dev.kord.common.entity.optional.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
public data class InteractionData(
    val id: Snowflake,
    val applicationId: Snowflake,
    val type: InteractionType,
    val data: ApplicationInteractionData,
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    val channelId: Snowflake,
    val member: Optional<MemberData> = Optional.Missing(),
    val user: Optional<UserData> = Optional.Missing(),
    val token: String,
    val permissions: Optional<Permissions> = Optional.Missing(),
    val version: Int,
    val message: Optional<MessageData> = Optional.Missing(),
    val locale: Optional<Locale> = Optional.Missing(),
    val guildLocale: Optional<Locale> = Optional.Missing()
) {
    public companion object {
        public fun from(interaction: DiscordInteraction): InteractionData {
            return with(interaction) {
                InteractionData(
                    id,
                    applicationId,
                    type,
                    ApplicationInteractionData.from(data, guildId.value),
                    guildId,
                    channelId,
                    member.map { it.toData(it.user.value!!.id, guildId.value!!) },
                    //borrow user from member if present
                    user.switchOnMissing(member.flatMap { it.user }).map { it.toData() },
                    token,
                    member.map { it.permissions },
                    version,
                    message.map {
                        MessageData.from(it)
                    },
                    locale,
                    guildLocale
                )
            }
        }
    }
}

@Serializable
public data class ResolvedObjectsData(
    val members: Optional<Map<Snowflake, MemberData>> = Optional.Missing(),
    val users: Optional<Map<Snowflake, UserData>> = Optional.Missing(),
    val roles: Optional<Map<Snowflake, RoleData>> = Optional.Missing(),
    val channels: Optional<Map<Snowflake, ChannelData>> = Optional.Missing(),
    val messages: Optional<Map<Snowflake, MessageData>> = Optional.Missing(),
    val attachments: Optional<Map<Snowflake, AttachmentData>> = Optional.Missing()
) {
    public companion object {
        public fun from(data: ResolvedObjects, guildId: Snowflake?): ResolvedObjectsData {
            return ResolvedObjectsData(
                members = data.members.mapValues { MemberData.from(it.key, guildId!!, it.value) },
                channels = data.channels.mapValues { ChannelData.from(it.value) },
                roles = data.roles.mapValues { RoleData.from(guildId!!, it.value) },
                users = data.users.mapValues { it.value.toData() },
                messages = data.messages.mapValues { it.value.toData() },
                attachments = data.attachments.mapValues { AttachmentData.from(it.value) }
            )
        }
    }
}

@Serializable
public data class ApplicationInteractionData(
    val id: OptionalSnowflake = OptionalSnowflake.Missing,
    val type: Optional<ApplicationCommandType> = Optional.Missing(),
    val targetId: OptionalSnowflake = OptionalSnowflake.Missing,
    val name: Optional<String> = Optional.Missing(),
    val options: Optional<List<OptionData>> = Optional.Missing(),
    val resolvedObjectsData: Optional<ResolvedObjectsData> = Optional.Missing(),
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    val customId: Optional<String> = Optional.Missing(),
    val componentType: Optional<ComponentType> = Optional.Missing(),
    val values: Optional<List<String>> = Optional.Missing(),
    val components: Optional<List<ComponentData>> = Optional.Missing()
) {
    public companion object {

        public fun from(
            data: InteractionCallbackData,
            interactionGuildId: Snowflake?, // this is the id of the guild the interaction was triggered in
        ): ApplicationInteractionData {
            return with(data) {
                ApplicationInteractionData(
                    id,
                    type,
                    targetId,
                    name,
                    options.map { it.map { option -> OptionData.from(option) } },
                    resolved.map { ResolvedObjectsData.from(it, interactionGuildId) },
                    guildId, // this is the id of the guild the command is registered to
                    customId,
                    componentType,
                    values = values,
                    components = components.mapList { ComponentData.from(it) }
                )
            }
        }
    }
}


@Serializable
public data class OptionData(
    val name: String,
    @OptIn(KordExperimental::class)
    val value: Optional<CommandArgument<@Serializable(NotSerializable::class) Any?>> = Optional.Missing(),
    @OptIn(KordExperimental::class)
    val values: Optional<List<CommandArgument<@Serializable(NotSerializable::class) Any?>>> = Optional.Missing(),
    val subCommands: Optional<List<SubCommand>> = Optional.Missing(),
    val focused: OptionalBoolean = OptionalBoolean.Missing
) {
    public companion object {
        public fun from(data: Option): OptionData = with(data) {
            when (data) {
                is SubCommand -> OptionData(name, values = data.options)
                is CommandArgument<*> -> OptionData(name, value = Optional(data), focused = data.focused)
                is CommandGroup -> OptionData(name, subCommands = data.options)
            }
        }
    }
}


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
