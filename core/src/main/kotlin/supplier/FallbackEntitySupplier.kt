package dev.kord.core.supplier

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.*
import dev.kord.core.entity.application.ApplicationCommandPermissions
import dev.kord.core.entity.application.GlobalApplicationCommand
import dev.kord.core.entity.application.GuildApplicationCommand
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.TopGuildChannel
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.entity.channel.thread.ThreadMember
import dev.kord.core.entity.interaction.followup.FollowupMessage
import dev.kord.core.switchIfEmpty
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

/**
 * Creates supplier providing a strategy which will first operate on this supplier. When an entity
 * is not present from the first supplier it will be fetched from [other] instead. Operations that return flows
 * will only fall back to [other] when the returned flow contained no elements.
 */
public infix fun EntitySupplier.withFallback(other: EntitySupplier): EntitySupplier =
    FallbackEntitySupplier(this, other)

private class FallbackEntitySupplier(val first: EntitySupplier, val second: EntitySupplier) : EntitySupplier {

    override val guilds: Flow<Guild>
        get() = first.guilds.switchIfEmpty(second.guilds)

    override val regions: Flow<Region>
        get() = first.regions.switchIfEmpty(second.regions)

    override suspend fun getGuildOrNull(id: Snowflake): Guild? =
        first.getGuildOrNull(id) ?: second.getGuildOrNull(id)

    override suspend fun getChannelOrNull(id: Snowflake): Channel? =
        first.getChannelOrNull(id) ?: second.getChannelOrNull(id)

    override fun getGuildChannels(guildId: Snowflake): Flow<TopGuildChannel> =
        first.getGuildChannels(guildId).switchIfEmpty(second.getGuildChannels(guildId))

    override fun getChannelPins(channelId: Snowflake): Flow<Message> =
        first.getChannelPins(channelId).switchIfEmpty(second.getChannelPins(channelId))

    override suspend fun getMemberOrNull(guildId: Snowflake, userId: Snowflake): Member? =
        first.getMemberOrNull(guildId, userId) ?: second.getMemberOrNull(guildId, userId)

    override suspend fun getMessageOrNull(channelId: Snowflake, messageId: Snowflake): Message? =
        first.getMessageOrNull(channelId, messageId) ?: second.getMessageOrNull(channelId, messageId)

    override fun getMessagesAfter(messageId: Snowflake, channelId: Snowflake, limit: Int?): Flow<Message> =
        first.getMessagesAfter(messageId, channelId, limit)
            .switchIfEmpty(second.getMessagesAfter(messageId, channelId, limit))

    override fun getMessagesBefore(messageId: Snowflake, channelId: Snowflake, limit: Int?): Flow<Message> =
        first.getMessagesBefore(messageId, channelId, limit)
            .switchIfEmpty(second.getMessagesBefore(messageId, channelId, limit))

    override fun getMessagesAround(messageId: Snowflake, channelId: Snowflake, limit: Int): Flow<Message> =
        first.getMessagesAround(messageId, channelId, limit)
            .switchIfEmpty(second.getMessagesAround(messageId, channelId, limit))

    override suspend fun getSelfOrNull(): User? =
        first.getSelfOrNull() ?: second.getSelfOrNull()

    override suspend fun getUserOrNull(id: Snowflake): User? =
        first.getUserOrNull(id) ?: second.getUserOrNull(id)

    override suspend fun getRoleOrNull(guildId: Snowflake, roleId: Snowflake): Role? =
        first.getRoleOrNull(guildId, roleId) ?: second.getRoleOrNull(guildId, roleId)


    override fun getGuildRoles(guildId: Snowflake): Flow<Role> =
        first.getGuildRoles(guildId).switchIfEmpty(second.getGuildRoles(guildId))

    override suspend fun getGuildBanOrNull(guildId: Snowflake, userId: Snowflake): Ban? =
        first.getGuildBanOrNull(guildId, userId) ?: second.getGuildBanOrNull(guildId, userId)

    override fun getGuildBans(guildId: Snowflake, limit: Int?): Flow<Ban> =
        first.getGuildBans(guildId, limit).switchIfEmpty(second.getGuildBans(guildId, limit))

    override fun getGuildMembers(guildId: Snowflake, limit: Int?): Flow<Member> =
        first.getGuildMembers(guildId, limit).switchIfEmpty(second.getGuildMembers(guildId, limit))

    override fun getGuildVoiceRegions(guildId: Snowflake): Flow<Region> =
        first.getGuildVoiceRegions(guildId).switchIfEmpty(second.getGuildVoiceRegions(guildId))

    override suspend fun getEmojiOrNull(guildId: Snowflake, emojiId: Snowflake): GuildEmoji? =
        first.getEmojiOrNull(guildId, emojiId) ?: second.getEmojiOrNull(guildId, emojiId)

    override fun getEmojis(guildId: Snowflake): Flow<GuildEmoji> =
        first.getEmojis(guildId).switchIfEmpty(second.getEmojis(guildId))

    override fun getCurrentUserGuilds(limit: Int?): Flow<Guild> =
        first.getCurrentUserGuilds(limit).switchIfEmpty(second.getCurrentUserGuilds(limit))

    override fun getChannelWebhooks(channelId: Snowflake): Flow<Webhook> =
        first.getChannelWebhooks(channelId).switchIfEmpty(second.getChannelWebhooks(channelId))

    override fun getGuildWebhooks(guildId: Snowflake): Flow<Webhook> =
        first.getGuildWebhooks(guildId).switchIfEmpty(second.getGuildWebhooks(guildId))

    override suspend fun getWebhookOrNull(id: Snowflake): Webhook? =
        first.getWebhookOrNull(id) ?: second.getWebhookOrNull(id)

    override suspend fun getWebhookWithTokenOrNull(id: Snowflake, token: String): Webhook? =
        first.getWebhookWithTokenOrNull(id, token) ?: second.getWebhookWithTokenOrNull(id, token)

    override suspend fun getWebhookMessageOrNull(
        webhookId: Snowflake,
        token: String,
        messageId: Snowflake,
        threadId: Snowflake?,
    ): Message? = first.getWebhookMessageOrNull(webhookId, token, messageId, threadId)
        ?: second.getWebhookMessageOrNull(webhookId, token, messageId, threadId)

    override suspend fun getGuildPreviewOrNull(guildId: Snowflake): GuildPreview? =
        first.getGuildPreviewOrNull(guildId) ?: second.getGuildPreviewOrNull(guildId)

    override suspend fun getGuildWidgetOrNull(guildId: Snowflake): GuildWidget? =
        first.getGuildWidgetOrNull(guildId) ?: second.getGuildWidgetOrNull(guildId)

    override suspend fun getTemplateOrNull(code: String): Template? =
        first.getTemplateOrNull(code) ?: second.getTemplateOrNull(code)


    override fun getTemplates(guildId: Snowflake): Flow<Template> =
        first.getTemplates(guildId).switchIfEmpty(second.getTemplates(guildId))

    override suspend fun getStageInstanceOrNull(channelId: Snowflake): StageInstance? =
        first.getStageInstanceOrNull(channelId) ?: second.getStageInstanceOrNull(channelId)

    override fun getThreadMembers(channelId: Snowflake): Flow<ThreadMember> {
        return first.getThreadMembers(channelId).switchIfEmpty(second.getThreadMembers(channelId))
    }

    override fun getActiveThreads(guildId: Snowflake): Flow<ThreadChannel> {
        return first.getActiveThreads(guildId).switchIfEmpty(second.getActiveThreads(guildId))
    }

    override fun getPublicArchivedThreads(channelId: Snowflake, before: Instant?, limit: Int?): Flow<ThreadChannel> {
        return first.getPublicArchivedThreads(channelId, before, limit)
            .switchIfEmpty(second.getPublicArchivedThreads(channelId, before, limit))
    }

    override fun getPrivateArchivedThreads(channelId: Snowflake, before: Instant?, limit: Int?): Flow<ThreadChannel> {
        return first.getPrivateArchivedThreads(channelId, before, limit)
            .switchIfEmpty(second.getPrivateArchivedThreads(channelId, before, limit))
    }

    override fun getJoinedPrivateArchivedThreads(
        channelId: Snowflake,
        before: Snowflake?,
        limit: Int?,
    ): Flow<ThreadChannel> {
        return first.getJoinedPrivateArchivedThreads(channelId, before, limit)
            .switchIfEmpty(second.getJoinedPrivateArchivedThreads(channelId, before, limit))
    }

    override fun getGuildApplicationCommands(
        applicationId: Snowflake,
        guildId: Snowflake,
        withLocalizations: Boolean?
    ): Flow<GuildApplicationCommand> = first.getGuildApplicationCommands(applicationId, guildId, withLocalizations)
        .switchIfEmpty(second.getGuildApplicationCommands(applicationId, guildId, withLocalizations))


    override suspend fun getGuildApplicationCommandOrNull(
        applicationId: Snowflake,
        guildId: Snowflake,
        commandId: Snowflake
    ): GuildApplicationCommand? =
        first.getGuildApplicationCommandOrNull(applicationId, guildId, commandId)
            ?: second.getGuildApplicationCommandOrNull(applicationId, guildId, commandId)


    override suspend fun getGlobalApplicationCommandOrNull(
        applicationId: Snowflake,
        commandId: Snowflake
    ): GlobalApplicationCommand? =
        first.getGlobalApplicationCommandOrNull(applicationId, commandId) ?: second.getGlobalApplicationCommandOrNull(
            applicationId,
            commandId
        )


    override fun getGlobalApplicationCommands(applicationId: Snowflake, withLocalizations: Boolean?): Flow<GlobalApplicationCommand> =
        first.getGlobalApplicationCommands(applicationId, withLocalizations)
            .switchIfEmpty(second.getGlobalApplicationCommands(applicationId, withLocalizations))


    override suspend fun getApplicationCommandPermissionsOrNull(
        applicationId: Snowflake,
        guildId: Snowflake,
        commandId: Snowflake
    ): ApplicationCommandPermissions? =
        first.getApplicationCommandPermissionsOrNull(applicationId, guildId, commandId)
            ?: second.getApplicationCommandPermissionsOrNull(applicationId, guildId, commandId)


    override fun getGuildApplicationCommandPermissions(
        applicationId: Snowflake,
        guildId: Snowflake
    ): Flow<ApplicationCommandPermissions> =
        first.getGuildApplicationCommandPermissions(applicationId, guildId)
            .switchIfEmpty(second.getGuildApplicationCommandPermissions(applicationId, guildId))

    override suspend fun getFollowupMessageOrNull(
        applicationId: Snowflake,
        interactionToken: String,
        messageId: Snowflake,
    ): FollowupMessage? = first.getFollowupMessageOrNull(applicationId, interactionToken, messageId)
        ?: second.getFollowupMessageOrNull(applicationId, interactionToken, messageId)

    override fun getGuildScheduledEvents(guildId: Snowflake): Flow<GuildScheduledEvent> =
        first.getGuildScheduledEvents(guildId).switchIfEmpty(second.getGuildScheduledEvents(guildId))

    override suspend fun getGuildScheduledEventOrNull(guildId: Snowflake, eventId: Snowflake): GuildScheduledEvent? =
        first.getGuildScheduledEventOrNull(guildId, eventId) ?: second.getGuildScheduledEventOrNull(guildId, eventId)

    override fun getGuildScheduledEventUsersBefore(
        guildId: Snowflake,
        eventId: Snowflake,
        before: Snowflake,
        limit: Int?,
    ): Flow<User> =
        first.getGuildScheduledEventUsersBefore(guildId, eventId, before, limit)
            .switchIfEmpty(second.getGuildScheduledEventUsersBefore(guildId, eventId, before, limit))

    override fun getGuildScheduledEventUsersAfter(
        guildId: Snowflake,
        eventId: Snowflake,
        after: Snowflake,
        limit: Int?,
    ): Flow<User> =
        first.getGuildScheduledEventUsersAfter(guildId, eventId, after, limit)
            .switchIfEmpty(second.getGuildScheduledEventUsersAfter(guildId, eventId, after, limit))

    override fun getGuildScheduledEventMembersBefore(
        guildId: Snowflake,
        eventId: Snowflake,
        before: Snowflake,
        limit: Int?,
    ): Flow<Member> =
        first.getGuildScheduledEventMembersBefore(guildId, eventId, before, limit)
            .switchIfEmpty(second.getGuildScheduledEventMembersBefore(guildId, eventId, before, limit))

    override fun getGuildScheduledEventMembersAfter(
        guildId: Snowflake,
        eventId: Snowflake,
        after: Snowflake,
        limit: Int?,
    ): Flow<Member> =
        first.getGuildScheduledEventMembersAfter(guildId, eventId, after, limit)
            .switchIfEmpty(second.getGuildScheduledEventMembersAfter(guildId, eventId, after, limit))

    override suspend fun getStickerOrNull(id: Snowflake): Sticker? =
        first.getStickerOrNull(id) ?: second.getStickerOrNull(id)


    override suspend fun getGuildStickerOrNull(guildId: Snowflake, id: Snowflake): GuildSticker? =
        first.getGuildStickerOrNull(guildId, id) ?: second.getGuildStickerOrNull(guildId, id)


    override fun getNitroStickerPacks(): Flow<StickerPack> =
        first.getNitroStickerPacks().switchIfEmpty(second.getNitroStickerPacks())


    override fun getGuildStickers(guildId: Snowflake): Flow<GuildSticker> =
        first.getGuildStickers(guildId).switchIfEmpty(second.getGuildStickers(guildId))


    override fun toString(): String = "FallbackEntitySupplier(first=$first, second=$second)"
}
