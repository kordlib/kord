package dev.kord.core.cache.data

import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.*
import dev.kord.common.entity.NotSerializable
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.mapList
import dev.kord.common.entity.optional.optional
import dev.kord.core.entity.interaction.OptionValue
import dev.kord.gateway.InteractionCreate
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@KordPreview
data class InteractionData(
    val id: Snowflake,
    val type: InteractionType,
    val data: ApplicationCommandInteractionData,
    val guildId: Snowflake,
    val channelId: Snowflake,
    val member: MemberData,
    val token: String,
    val permissions: Permissions,
    val version: Int
) {
    companion object {
        fun from(event: InteractionCreate): InteractionData {
            return with(event.interaction) {
                val resolvables = ResolvedObjectsData(
                    members = resolved.members.mapValues { MemberData.from(it.key, guildId, it.value) },
                    channels = resolved.channels.mapValues { ChannelData.from(it.value) },
                    roles = resolved.roles.mapValues { RoleData.from(it.value) },
                    users = resolved.users.mapValues { it.value.toData() }
                ).optional()
                InteractionData(
                    id,
                    type,
                    ApplicationCommandInteractionData.from(data, resolvables),
                    guildId,
                    channelId,
                    member.toData(member.user.value!!.id, guildId),
                    token,
                    member.permissions,
                    version
                )
            }
        }
    }
}

@KordPreview
@Serializable
data class ResolvedObjectsData(
    val members: Map<Snowflake, MemberData>,
    val users: Map<Snowflake, UserData>,
    val roles: Map<Snowflake, RoleData>,
    val channels: Map<Snowflake, ChannelData>
)

@KordPreview
@Serializable
data class ApplicationCommandInteractionData(
    val id: Snowflake,
    val name: String,
    val options: Optional<List<OptionData>> = Optional.Missing(),
    val resolvedObjectsData: Optional<ResolvedObjectsData> = Optional.Missing()
) {
    companion object {
        fun from(
            data: DiscordApplicationCommandInteractionData,
            resolvables: Optional<ResolvedObjectsData> = Optional.Missing()
        ): ApplicationCommandInteractionData {
            return with(data) {
                ApplicationCommandInteractionData(
                    id,
                    name,
                    options.mapList { OptionData.from(it) },
                    resolvables
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
        val value: Optional<DiscordOptionValue<@Serializable(NotSerializable::class) Any?>> = Optional.Missing(),
        val values: Optional<List<CommandArgument>> = Optional.Missing(),
        val subCommands: Optional<List<SubCommand>> = Optional.Missing()
) {
    companion object {
        fun from(data: Option): OptionData = with(data) {
            when(data) {
                is SubCommand -> OptionData(name, values = data.options)
                is CommandArgument -> OptionData(name, value = Optional(data.value))
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


