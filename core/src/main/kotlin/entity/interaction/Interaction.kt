package dev.kord.core.entity.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.InteractionType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.orEmpty
import dev.kord.core.Kord
import dev.kord.core.behavior.InteractionBehavior
import dev.kord.core.behavior.MemberBehavior
import dev.kord.core.behavior.PartialInteractionBehavior
import dev.kord.core.cache.data.ApplicationCommandInteractionData
import dev.kord.core.cache.data.ApplicationCommandInteractionDataOptionData
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.entity.Entity
@KordPreview
class PartialInteraction(val data: InteractionData, override val kord: Kord) : PartialInteractionBehavior {

    override val id: Snowflake get() = data.id

    val channelId: Snowflake get() = data.channelId

    override val token: String get() = data.token

    val guildId: Snowflake get() = data.channelId

    val type: InteractionType get() = data.type

    val member: MemberBehavior get() = MemberBehavior(guildId, data.member.userId, kord)

    val version: Int get() = data.version

}
@KordPreview
class Interaction(val data: InteractionData, override val applicationId: Snowflake, override val kord: Kord) :
    InteractionBehavior {

    override val id: Snowflake get() = data.id

    val channelId: Snowflake get() = data.channelId

    override val token: String get() = data.token

    val guildId: Snowflake get() = data.channelId

    val type: InteractionType get() = data.type

    val member: MemberBehavior get() = MemberBehavior(guildId, data.member.userId, kord)

    val command: Command
        get() = Command(data.data)

    val version: Int get() = data.version

}

class Command(val data: ApplicationCommandInteractionData) : Entity {
    override val id: Snowflake
        get() = data.id
    val name = data.name

    val parameters = data.options.orEmpty().map { Parameter(it) }

}

class Parameter(val data: ApplicationCommandInteractionDataOptionData) {
    val name: String
        get() = data.name
    val value: String?
        get() = data.value.value

    val parameters = data.options.value.orEmpty().map { Parameter(it) }
}