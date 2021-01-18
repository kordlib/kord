package dev.kord.core.cache.data

import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.*
import dev.kord.common.entity.NotSerializable
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.mapList
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
                InteractionData(
                    id,
                    type,
                    ApplicationCommandInteractionData.from(data),
                    guildId,
                    channelId,
                    member.toData(member.user.value!!.id,guildId),
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
data class ApplicationCommandInteractionData(
    val id: Snowflake,
    val name: String,
    val options: Optional<List<OptionData>> = Optional.Missing()
) {
    companion object {
        fun from(data: DiscordApplicationCommandInteractionData): ApplicationCommandInteractionData {
            return with(data) {
                ApplicationCommandInteractionData(
                    id,
                    name,
                    options.mapList { OptionData.from(it) })
            }
        }
    }
}

@KordPreview
@Serializable
data class OptionData(
        val name: String,
        @OptIn(KordExperimental::class)
        val value: Optional<OptionValue<@Serializable(NotSerializable::class) Any?>> = Optional.Missing(),
        val values: Optional<List<CommandArgument>> = Optional.Missing(),
        val subCommand: Optional<List<SubCommand>> = Optional.Missing()
) {
    companion object {
        fun from(data: Option): OptionData = with(data) {
            when(data) {
                is SubCommand -> OptionData(name, values = data.options)
                is CommandArgument -> OptionData(name, value = Optional(data.value))
                is CommandGroup -> OptionData(name, subCommand = data.options)
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


