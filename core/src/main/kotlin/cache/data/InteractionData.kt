package dev.kord.core.cache.data

import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.*
import dev.kord.common.entity.NotSerializable
import dev.kord.common.entity.optional.*
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
    val permissions: Optional<Permissions>,
    val version: Int
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
                    version
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

sealed class ApplicationInteractionData {
    companion object {

        fun from(
            data: InteractionCallbackData,
            guildId: Snowflake?
        ): ApplicationInteractionData {
            return when (data) {
                is DiscordApplicationCommandInteractionData -> ApplicationCommandInteractionData.from(data, guildId)
                is DiscordApplicationComponentCallbackData -> ApplicationComponentInteractionData.from(data)
                else -> error("Unknown type")
            }
        }
    }
}

@KordPreview
@Serializable
data class ApplicationCommandInteractionData(
    val id: Snowflake,
    val name: String,
    val options: Optional<List<OptionData>> = Optional.Missing(),
    val resolvedObjectsData: Optional<ResolvedObjectsData> = Optional.Missing()
) : ApplicationInteractionData() {
    companion object {
        fun from(
            data: DiscordApplicationCommandInteractionData,
            guildId: Snowflake?
        ): ApplicationCommandInteractionData {
            return with(data) {
                ApplicationCommandInteractionData(
                    id,
                    name,
                    options.mapList { OptionData.from(it) },
                    data.resolved.map { ResolvedObjectsData.from(it, guildId) }
                )

            }
        }
    }
}

@KordPreview
@Serializable
data class ApplicationComponentInteractionData(
    val customId: Optional<String> = Optional.Missing(),
    val componentType: ComponentType
) : ApplicationInteractionData() {
    companion object {
        fun from(
            data: DiscordApplicationComponentCallbackData,
        ): ApplicationComponentInteractionData {
            return with(data) {
                ApplicationComponentInteractionData(
                    customId, componentType
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


