package dev.kord.core.entity.interaction

import cache.data.MessageInteractionData
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.InteractionType
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.User

@KordPreview
class MessageInteraction(val data: MessageInteractionData, override val kord: Kord): KordEntity {
    override val id: Snowflake get() = data.id
    val name: String get() = data.name
    val user: User get() = User(data.user, kord)
    val type: InteractionType get() = data.type
}