package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordApplicationCommandInteractionData
import dev.kord.common.entity.DiscordApplicationCommandInteractionDataOption
import dev.kord.common.entity.InteractionType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.mapList
import dev.kord.gateway.InteractionCreate
import kotlinx.serialization.Serializable

data class InteractionData(
    val id: Snowflake,
    val type: InteractionType,
    val data: ApplicationCommandInteractionData,
    val guildId: Snowflake,
    val channelId: Snowflake,
    val member: MemberData,
    val token: String,
    val version: Int
) {
    companion object {
        fun from(event: InteractionCreate): InteractionData {
            return with(event.interaction) {
                InteractionData(
                    id,
                    type,
                    ApplicationCommandInteractionData.from(data),
                    guildId,
                    channelId,
                    member.toData(member.user.value!!.id,guildId),
                    token,
                    version
                )
            }
        }
    }
}

@Serializable
data class ApplicationCommandInteractionData(
    val id: Snowflake,
    val name: String,
    val options: Optional<List<ApplicationCommandInteractionDataOptionData>> = Optional.Missing()
) {
    companion object {
        fun from(data: DiscordApplicationCommandInteractionData): ApplicationCommandInteractionData {
            return with(data) {
                ApplicationCommandInteractionData(
                    id,
                    name,
                    options.mapList { ApplicationCommandInteractionDataOptionData.from(it) })
            }
        }
    }
}

@Serializable
data class ApplicationCommandInteractionDataOptionData(
    val name: String,
    val value: Optional<String> = Optional.Missing(),
    val options: Optional<List<ApplicationCommandInteractionDataOptionData>> = Optional.Missing()
) {
    companion object {
        fun from(data: DiscordApplicationCommandInteractionDataOption): ApplicationCommandInteractionDataOptionData {
            return with(data) {
                ApplicationCommandInteractionDataOptionData(name, value, options.mapList { from(it) })
            }
        }
    }
}