package com.gitlab.kordlib.core

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.entity.*
import com.gitlab.kordlib.core.entity.channel.Channel
import com.gitlab.kordlib.core.entity.channel.GuildChannel
import com.gitlab.kordlib.rest.request.RequestException
import kotlinx.coroutines.flow.Flow

interface EntitySupplier {
    val guilds: Flow<Guild>

    val regions: Flow<Region>

    /**
     * Returns the [Guild] associated with the given [id] if present.
     *
     * @throws [RequestException] if retrieving an entity failed.
     */
    suspend fun getGuildOrNull(id: Snowflake): Guild?

    /**
     * Returns the [Guild] associated with the given [id] if present.
     *
     * @throws [RequestException] if retrieving an entity failed.
     */
    suspend fun getChannelOrNull(id: Snowflake): Channel?

    /**
     * Returns the [Guild] associated with the given [id] if present.
     *
     * @throws [RequestException] if retrieving an entity failed.
     * @throws [KotlinNullPointerException] if the entity retrieved was null.
     */
    suspend fun getGuild(id: Snowflake): Guild = getGuildOrNull(id)!!

    /**
     * Returns the [Channel] associated with the given [id] if present.
     *
     * @throws [RequestException] if retrieving an entity failed.
     * @throws [KotlinNullPointerException] if the entity retrieved was null.
     */
    suspend fun getChannel(id: Snowflake): Channel = getChannelOrNull(id)!!

    /**
     * Returns a flow of [Channel]s in the [Guild] associated with [guildId].
     *
     * @throws [RequestException] if retrieving an entity failed.
     */
    fun getGuildChannels(guildId: Snowflake): Flow<GuildChannel>

    /**
     * Returns a [Flow] of [Message] pinned in the [Channel] associated with the given [channelId] if present.
     *
     * @throws [RequestException] if retrieving an entity failed.
     */
    fun getChannelPins(channelId: Snowflake): Flow<Message>

    /**
     * Returns the [Member] associated with the given [guildId] and [userId] if present.
     *
     * @throws [RequestException] if retrieving an entity failed.
     */
    suspend fun getMemberOrNull(guildId: Snowflake, userId: Snowflake): Member?

    /**
     * Returns the [Message] associated with the given [channelId] and [messageId] if present.
     *
     * @throws [RequestException] if retrieving an entity failed.
     */
    suspend fun getMessageOrNull(channelId: Snowflake, messageId: Snowflake): Message?

    /**
     * Returns a [Member] associated with the given [id] if present.
     *
     * @throws [RequestException] if retrieving an entity failed.
     * @throws [KotlinNullPointerException] if the entity retrieved was null.
     */
    suspend fun getMember(guildId: Snowflake, userId: Snowflake): Member = getMemberOrNull(guildId, userId)!!

    /**
     * Returns the [Message] associated with the given [channelId] and [messageId] if present.
     *
     * @throws [RequestException] if retrieving an entity failed.
     * @throws [KotlinNullPointerException] if the entity retrieved was null.
     */
    suspend fun getMessage(channelId: Snowflake, messageId: Snowflake): Message = getMessageOrNull(channelId, messageId)!!


    /**
     * Returns a [Flow] of [Message]s from the [Channel] associated with [channelId]
     * after the [Message] with [messageId].
     *
     * @param limit a strictly positive number representing the bound for the retrieved elements.
     * @throws [RequestException] if retrieving an entity failed.

     */
    fun getMessagesAfter(messageId: Snowflake, channelId: Snowflake, limit: Int = Int.MAX_VALUE): Flow<Message>

    /**
     * Returns a [Flow] of [Message]s from the [Channel] associated with [channelId]
     * before the [Message] with [messageId].
     *
     * @param limit a strictly positive number representing the bound for the retrieved elements.
     * @throws [RequestException] if retrieving an entity failed.

     */
    fun getMessagesBefore(messageId: Snowflake, channelId: Snowflake, limit: Int = Int.MAX_VALUE): Flow<Message>

    /**
     * Returns a [Flow] of [Message]s from the [Channel] associated with [channelId]
     * around the [Message] with [messageId].
     *
     * @param limit a strictly positive number representing the bound for the retrieved elements.
     * @throws [RequestException] if retrieving an entity failed.
     */
    fun getMessagesAround(messageId: Snowflake, channelId: Snowflake, limit: Int = Int.MAX_VALUE): Flow<Message>

    /**
     * Returns the [User] associated with this Kord instance if present.
     *
     * @throws [RequestException] if retrieving an entity failed.

     */
    suspend fun getSelfOrNull(): User?

    /**
     * Returns the [User] associated with the given [id] if present.
     *
     * @throws [RequestException] if retrieving an entity failed.

     */
    suspend fun getUserOrNull(id: Snowflake): User?

    /**
     * Returns the [User] of the current Kord instance if present.
     *
     * @throws [RequestException] if retrieving an entity failed.
     * @throws [KotlinNullPointerException] if the entity retrieved was null.
     */
    suspend fun getSelf(): User = getSelfOrNull()!!

    /**
     * Returns the [User] associated with the given [id] if present.
     *
     * @throws [RequestException] if retrieving an entity failed.
     * @throws [KotlinNullPointerException] if the entity retrieved was null.
     */
    suspend fun getUser(id: Snowflake): User = getUserOrNull(id)!!

    /**
     * Returns the [Role] associated with the given [guildId] and [roleId] if present.
     *
     * @throws [RequestException] if retrieving an entity failed.

     */
    suspend fun getRoleOrNull(guildId: Snowflake, roleId: Snowflake): Role?

    /**
     * Returns the [Role] associated with the given [guildId] and [roleId] if present.
     *
     * @throws [RequestException] if retrieving an entity failed.
     * @throws [KotlinNullPointerException] if the entity retrieved was null.
     */
    suspend fun getRole(guildId: Snowflake, roleId: Snowflake): Role = getRoleOrNull(guildId, roleId)!!

    /**
     * Returns a [Flow] of the [Role]s in the [Guild] associated with the given [guildId] if present.
     *
     * @throws [RequestException] if retrieving an entity failed.

     */
    fun getGuildRoles(guildId: Snowflake): Flow<Role>

    /**
     * Returns the [Ban] associated with the given [userId] in the [Guild] with [guildId] if present.
     *
     * @throws [RequestException] if retrieving an entity failed.

     */
    suspend fun getGuildBanOrNull(guildId: Snowflake, userId: Snowflake): Ban?

    /**
     * Returns the [Ban] for the [User] associated with the given [userId] in the [Guild] with [guildId] if present.
     *
     * @throws [RequestException] if retrieving an entity failed.
     * @throws [KotlinNullPointerException] if the entity retrieved was null.
     */
    suspend fun getGuildBan(guildId: Snowflake, userId: Snowflake): Ban = getGuildBanOrNull(guildId, userId)!!

    /**
     * Returns a [Flow] of the [Ban]s for the [Guild] associated with the given [guildId] if present.
     * @throws [RequestException] if retrieving an entity failed.
     */
    fun getGuildBans(guildId: Snowflake): Flow<Ban>

    /**
     * Returns a [Flow] of the [Member]s for the [Guild] associated with the given [guildId] if present.
     * @param limit a strictly positive number representing the bound for the retrieved elements.
     * @throws [RequestException] if retrieving an entity failed.
     */
    fun getGuildMembers(guildId: Snowflake, limit: Int = Int.MAX_VALUE): Flow<Member>

    /**
     * Returns a [Flow] of the [Region]s for the [Guild] associated with the given [guildId] if present.
     *@throws [RequestException] if retrieving an entity failed.
     */
    fun getGuildVoiceRegions(guildId: Snowflake): Flow<Region>

    /**
     * Returns the [GuildEmoji] associated with the given [guildId] and [emojiId] if present.
     *
     * @throws [RequestException] if retrieving an entity failed.
     */
    suspend fun getEmojiOrNull(guildId: Snowflake, emojiId: Snowflake): GuildEmoji?

    /**
     * Returns the [GuildEmoji] associated with the given [guildId] and [emojiId] if present.
     *
     * @throws [RequestException] if retrieving an entity failed.
     *
     * @throws [KotlinNullPointerException] if the entity retrieved was null.
     */
    suspend fun getEmoji(guildId: Snowflake, emojiId: Snowflake): GuildEmoji = getEmojiOrNull(guildId, emojiId)!!

    /**
     * Returns a [Flow] of the [GuildEmoji]s for the [Guild] associated with the given [guildId] if present.
     *
     * @throws [RequestException] if retrieving an entity failed.
     */
    fun getEmojis(guildId: Snowflake): Flow<GuildEmoji>

    /**
     * Returns a [Flow] of the [Guild]s for the bot (This Kord instance).
     *
     * @param limit a strictly positive number representing the bound for the retrieved elements.
     *
     * @throws [RequestException] if retrieving an entity failed.
     */
    fun getCurrentUserGuilds(limit: Int = Int.MAX_VALUE): Flow<Guild>

    /**
     * Returns a [Flow] of the [Webhook]s for the [Channel] associated with the given [channelId] if present.
     *
     * @throws [RequestException] if retrieving an entity failed.
     */
    fun getChannelWebhooks(channelId: Snowflake): Flow<Webhook>

    /**
     * Returns a [Flow] of the [Webhook]s for the [Guild] associated with the given [guildId] if present.
     *
     * @throws [RequestException] if retrieving an entity failed.
     */
    fun getGuildWebhooks(guildId: Snowflake): Flow<Webhook>

    /**
     * Returns the [Webhook] associated with the given [webhookId] if present.
     *
     * @throws [RequestException] if retrieving an entity failed.
     */
    suspend fun getWebhookOrNull(webhookId: Snowflake): Webhook?

    /**
     * Returns the [Webhook] associated with the given [webhookId] and [token] if present.
     *
     * @throws [RequestException] if retrieving an entity failed.
     */
    suspend fun getWebhookWithTokenOrNull(webhookId: Snowflake, token: String): Webhook?

    /**
     * Returns the [Webhook] associated with the given [webhookId] if present.
     *
     * @throws [RequestException] if retrieving an entity failed.
     * @throws [KotlinNullPointerException] if the entity retrieved was null.
     */
    suspend fun getWebhook(webhookId: Snowflake): Webhook = getWebhookOrNull(webhookId)!!

    /**
     * Returns the [Webhook] associated with the given [webhookId] and [token] if present.
     *
     * @throws [RequestException] if retrieving an entity failed.
     * @throws [KotlinNullPointerException] if the entity retrieved was null.
     */
    suspend fun getWebhookWithToken(webhookId: Snowflake, token: String): Webhook = getWebhookWithTokenOrNull(webhookId, token)!!
}

suspend inline fun <reified T : Channel> EntitySupplier.getChannelOfOrNull(id: Snowflake) = getChannelOrNull(id) as? T

suspend inline fun <reified T : Channel> EntitySupplier.getChannelOf(id: Snowflake) = getChannelOrNull(id) as T