package cache.data;

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.DiscordMessageInteraction
import dev.kord.common.entity.InteractionType
import dev.kord.common.entity.Snowflake
import dev.kord.core.cache.data.UserData
import dev.kord.core.cache.data.toData
import kotlinx.serialization.Serializable

@KordPreview
@Serializable
data class MessageInteractionData(
    val id:Snowflake,
    val type:InteractionType,
    val name:String,
    val user:UserData
) {
    companion object {
        fun from(entity: DiscordMessageInteraction): MessageInteractionData = with(entity) {
            MessageInteractionData(id, type, name, user.toData())
        }
    }
}
