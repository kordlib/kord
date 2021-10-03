package cache.data;

import dev.kord.common.entity.DiscordMessageInteraction
import dev.kord.common.entity.InteractionType
import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable

@Serializable
public data class MessageInteractionData(
    val id: Snowflake,
    val type: InteractionType,
    val name: String,
    val user: Snowflake
) {
    public companion object {
        public fun from(entity: DiscordMessageInteraction): MessageInteractionData = with(entity) {
            MessageInteractionData(id, type, name, user.id)
        }
    }
}
