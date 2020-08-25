package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.common.annotation.KordUnstableApi
import com.gitlab.kordlib.gateway.DiscordRemovedEmoji
import com.gitlab.kordlib.gateway.DiscordRemovedReactionEmoji
import kotlinx.serialization.Serializable

@Serializable
@KordUnstableApi
data class RemovedReactionData(val id: Long?, val name: String) {
    companion object {
        fun from(entity: DiscordRemovedReactionEmoji): RemovedReactionData = with(entity) {
            RemovedReactionData(id?.toLong(), name)
        }
    }
}

@Serializable
@KordUnstableApi
data class ReactionRemoveEmojiData(
        /**
         * The id of the channel.
         */
        val channelId: Long,

        /**
         * The id of the guild.
         */
        val guildId: Long,

        /**
         * The id of the message.
         */
        val messageId: Long,

        /**
         * The emoji that was removed.
         */
        val emoji: RemovedReactionData
) {

    companion object {
        fun from(entity: DiscordRemovedEmoji): ReactionRemoveEmojiData = with(entity) {
            ReactionRemoveEmojiData(channelId.toLong(), guildId.toLong(), messageId.toLong(), RemovedReactionData.from(emoji))
        }
    }

}