package dev.kord.core.cache.data

import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Choice
import dev.kord.common.entity.CommandArgument
import dev.kord.common.entity.CommandGroup
import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.DiscordInteraction
import dev.kord.common.entity.InteractionCallbackData
import dev.kord.common.entity.InteractionType
import dev.kord.common.entity.Option
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.ResolvedObjects
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.SubCommand
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.flatMap
import dev.kord.common.entity.optional.map
import dev.kord.common.entity.optional.mapValues
import dev.kord.common.entity.optional.switchOnMissing
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@KordPreview
data class InteractionData(
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
    val message: Optional<MessageData> = Optional.Missing()
) {
    companion object {
        fun from(interaction: DiscordInteraction): InteractionData {
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
                    }
                )
            }
        }
    }
}

@KordPreview
@Serializable
data class ResolvedObjectsData(
    val members: Optional<Map<Snowflake, MemberData>> = Optional.Missing(),
    val users: Optional<Map<Snowflake, UserData>> = Optional.Missing(),
    val roles: Optional<Map<Snowflake, RoleData>> = Optional.Missing(),
    val channels: Optional<Map<Snowflake, ChannelData>> = Optional.Missing()
) {
    companion object {
        fun from(data: ResolvedObjects, guildId: Snowflake?): ResolvedObjectsData {
            return ResolvedObjectsData(
                members = data.members.mapValues { MemberData.from(it.key, guildId!!, it.value) },
                channels = data.channels.mapValues { ChannelData.from(it.value) },
                roles = data.roles.mapValues { RoleData.from(guildId!!, it.value) },
                users = data.users.mapValues { it.value.toData() }
            )
        }
    }
}

@Serializable
data class ApplicationInteractionData(
    val id: OptionalSnowflake = OptionalSnowflake.Missing,
    val name: Optional<String> = Optional.Missing(),
    val options: Optional<List<OptionData>> = Optional.Missing(),
    val resolvedObjectsData: Optional<ResolvedObjectsData> = Optional.Missing(),
    val customId: Optional<String> = Optional.Missing(),
    val componentType: Optional<ComponentType> = Optional.Missing(),
    val values: Optional<List<String>> = Optional.Missing()
) {
    companion object {

        fun from(
            data: InteractionCallbackData,
            guildId: Snowflake?
        ): ApplicationInteractionData {
            return with(data) {
                ApplicationInteractionData(
                    id,
                    name,
                    options.map { it.map { OptionData.from(it) } },
                    resolved.map { ResolvedObjectsData.from(it, guildId) },
                    customId,
                    componentType,
                    values = values,
                )
            }
        }
    }
}

@KordPreview
@Serializable
data class OptionData(
    val name: String,
    @OptIn(KordExperimental::class)
    val value: Optional<CommandArgument<@Serializable(NotSerializable::class) Any?>> = Optional.Missing(),
    val values: Optional<List<CommandArgument<@Serializable(NotSerializable::class) Any?>>> = Optional.Missing(),
    val subCommands: Optional<List<SubCommand>> = Optional.Missing()
) {
    companion object {
        fun from(data: Option): OptionData = with(data) {
            when (data) {
                is SubCommand -> OptionData(name, values = data.options)
                is CommandArgument<*> -> OptionData(name, value = Optional(data))
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
object NotSerializable : KSerializer<Any?> {
    override fun deserialize(decoder: Decoder) = error("This operation is not supported.")
    override val descriptor: SerialDescriptor = String.serializer().descriptor
    override fun serialize(encoder: Encoder, value: Any?) = error("This operation is not supported.")
}
