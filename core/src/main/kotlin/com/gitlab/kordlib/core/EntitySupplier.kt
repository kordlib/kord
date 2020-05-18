package com.gitlab.kordlib.core

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.entity.*
import com.gitlab.kordlib.core.entity.channel.Channel
import com.gitlab.kordlib.rest.json.response.VoiceRegion
import kotlinx.coroutines.flow.Flow

interface EntitySupplier {
    val guilds: Flow<Guild>

    val regions: Flow<Region>

    suspend fun getChannel(id: Snowflake): Channel?

    suspend fun getMessagesAfter(messageId: Snowflake, limit: Int = Int.MAX_VALUE): Flow<Message>

    suspend fun getMessagesBefore(messageId: Snowflake, limit: Int = Int.MAX_VALUE): Flow<Message>

    suspend fun getMessagesAround(messageId: Snowflake, limit: Int = Int.MAX_VALUE): Flow<Message>

    suspend fun getChannelPins(channelId: Snowflake): Flow<Message>

    suspend fun getGuild(id: Snowflake): Guild?

    suspend fun getMember(guildId: Snowflake, userId: Snowflake): Member?

    suspend fun getMessage(channelId: Snowflake, messageId: Snowflake): Message?

    suspend fun getSelf(): User?

    suspend fun getUser(id: Snowflake): User?

    suspend fun getRole(guildId: Snowflake, roleId: Snowflake): Role?

    suspend fun getGuildBan(guildId: Snowflake, userId: Snowflake): Ban?

    suspend fun getGuildRoles(guildId: Snowflake): Flow<Role>

    suspend fun getGuildBans(guildId: Snowflake): Flow<Ban>

    suspend fun getGuildMembers(guildId: Snowflake, limit: Int = 1): Flow<Member>

    suspend fun getGuildVoiceRegions(guildId: Snowflake): Flow<VoiceRegion>

    suspend fun getReactors(channelId: Snowflake, messageId: Snowflake, emoji: ReactionEmoji): Flow<User>

    suspend fun getEmoji(guildId: Snowflake, emojiId: Snowflake): ReactionEmoji?

    suspend fun getEmojis(guildId: Snowflake): Flow<ReactionEmoji>

    suspend fun getCurrentUser(): User?

    suspend fun getCurrentUserGuilds(): Flow<Guild>

    suspend fun getChannelWebhooks(channelId: Snowflake): Flow<Webhook>

    suspend fun getGuildWebhooks(guildId: Snowflake): Flow<Webhook>

    suspend fun getWebhook(webhookId: Snowflake): Webhook?

    suspend fun getWebhookWithToken(webhookId: Snowflake, token: String): Webhook?

}

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
suspend inline fun <reified T : Channel> EntitySupplier.getChannel(id: Snowflake) = getChannel(id) as? T
