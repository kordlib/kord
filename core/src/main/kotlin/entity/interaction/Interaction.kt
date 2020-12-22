package dev.kord.core.entity.interaction

import dev.kord.common.entity.InteractionType
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.InteractionBehavior
import dev.kord.core.behavior.MemberBehavior
import dev.kord.core.behavior.PartialInteractionBehavior
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.entity.CommandOptions
import dev.kord.core.entity.KordEntity

 class PartialInteraction(val data: InteractionData, override val kord: Kord) : PartialInteractionBehavior {

    override val id: Snowflake get() = data.id

    val channelId: Snowflake get() = data.channelId

    override val token: String get() = data.token

    val guildId: Snowflake get() = data.channelId

    val type: InteractionType get() = data.type

    val member: MemberBehavior get() = MemberBehavior(guildId, data.member.userId, kord)

    val version: Int get() = data.version

}

class Interaction(val data: InteractionData, override val applicationId: Snowflake, override val kord: Kord): InteractionBehavior {

    override val id: Snowflake get() = data.id

    val channelId: Snowflake get() = data.channelId

    override val token: String get() = data.token

    val guildId: Snowflake get() = data.channelId

    val type: InteractionType get() = data.type

    val member: MemberBehavior get() = MemberBehavior(guildId, data.member.userId, kord)

    val name: String = data.data.name

    val version: Int get() = data.version

}