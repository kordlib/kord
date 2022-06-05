package dev.kord.core.supplier

import dev.kord.cache.api.DataCache
import dev.kord.cache.api.put
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.*
import dev.kord.core.entity.application.ApplicationCommandPermissions
import dev.kord.core.entity.application.GlobalApplicationCommand
import dev.kord.core.entity.application.GuildApplicationCommand
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.TopGuildChannel
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.entity.channel.thread.ThreadMember
import dev.kord.core.entity.interaction.followup.FollowupMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.Instant

/**
 * [EntitySupplier] that delegates to another [EntitySupplier] to resolve entities.
 *
 * Resolved entities will always be stored in [cache] if it wasn't null or empty for flows.
 */
public class StoreEntitySupplier(
    private val supplier: EntitySupplier,
    private val cache: DataCache,
) : EntitySupplier {

    @Deprecated(
        "Parameter 'kord' is unused, use other constructor instead.",
        ReplaceWith("StoreEntitySupplier(supplier, cache)"),
        DeprecationLevel.ERROR,
    )
    @Suppress("UNUSED_PARAMETER")
    public constructor(supplier: EntitySupplier, cache: DataCache, kord: Kord) : this(supplier, cache)

    override val guilds: Flow<Guild>
        get() = storeOnEach(supplier.guilds) { it.data }

    override val regions: Flow<Region>
        get() = storeOnEach(supplier.regions) { it.data }


    override suspend fun getGuildOrNull(id: Snowflake): Guild? {
        return storeAndReturn(supplier.getGuildOrNull(id)) { it.data }
    }

    override suspend fun getGuildPreviewOrNull(guildId: Snowflake): GuildPreview? {
        return storeAndReturn(supplier.getGuildPreviewOrNull(guildId)) { it.data }
    }

    override suspend fun getGuildWidgetOrNull(guildId: Snowflake): GuildWidget? {
        return storeAndReturn(supplier.getGuildWidgetOrNull(guildId)) { it.data }
    }

    override suspend fun getChannelOrNull(id: Snowflake): Channel? {
        return storeAndReturn(supplier.getChannelOrNull(id)) { it.data }
    }

    override fun getGuildChannels(guildId: Snowflake): Flow<TopGuildChannel> {
        return storeOnEach(supplier.getGuildChannels(guildId)) { it.data }

    }

    override fun getChannelPins(channelId: Snowflake): Flow<Message> {
        return storeOnEach(supplier.getChannelPins(channelId)) { it.data }

    }

    override suspend fun getMemberOrNull(guildId: Snowflake, userId: Snowflake): Member? {
        return storeAndReturn(supplier.getMemberOrNull(guildId, userId)) { it.data }
    }

    override suspend fun getMessageOrNull(channelId: Snowflake, messageId: Snowflake): Message? {
        return storeAndReturn(supplier.getMessageOrNull(channelId, messageId)) { it.data }
    }

    override fun getMessagesAfter(messageId: Snowflake, channelId: Snowflake, limit: Int?): Flow<Message> {
        return storeOnEach(supplier.getMessagesAfter(messageId, channelId, limit)) { it.data }
    }

    override fun getMessagesBefore(messageId: Snowflake, channelId: Snowflake, limit: Int?): Flow<Message> {
        return storeOnEach(supplier.getMessagesBefore(messageId, channelId, limit)) { it.data }
    }

    override fun getMessagesAround(messageId: Snowflake, channelId: Snowflake, limit: Int): Flow<Message> {
        return storeOnEach(supplier.getMessagesAround(messageId, channelId, limit)) { it.data }
    }

    override suspend fun getSelfOrNull(): User? {
        return storeAndReturn(supplier.getSelfOrNull()) { it.data }
    }

    override suspend fun getUserOrNull(id: Snowflake): User? {
        return storeAndReturn(supplier.getUserOrNull(id)) { it.data }
    }

    override suspend fun getRoleOrNull(guildId: Snowflake, roleId: Snowflake): Role? {
        return storeAndReturn(supplier.getRoleOrNull(guildId, roleId)) { it.data }
    }

    override fun getGuildRoles(guildId: Snowflake): Flow<Role> {
        return storeOnEach(supplier.getGuildRoles(guildId)) { it.data }
    }

    override suspend fun getGuildBanOrNull(guildId: Snowflake, userId: Snowflake): Ban? {
        return storeAndReturn(supplier.getGuildBanOrNull(guildId, userId)) { it.data }
    }

    override fun getGuildBans(guildId: Snowflake, limit: Int?): Flow<Ban> {
        return storeOnEach(supplier.getGuildBans(guildId, limit)) { it.data }
    }

    override fun getGuildMembers(guildId: Snowflake, limit: Int?): Flow<Member> {
        return storeOnEach(supplier.getGuildMembers(guildId, limit)) { it.data }
    }

    override fun getGuildVoiceRegions(guildId: Snowflake): Flow<Region> {
        return storeOnEach(supplier.getGuildVoiceRegions(guildId)) { it.data }
    }

    override suspend fun getEmojiOrNull(guildId: Snowflake, emojiId: Snowflake): GuildEmoji? {
        return storeAndReturn(supplier.getEmojiOrNull(guildId, emojiId)) { it.data }
    }

    override fun getEmojis(guildId: Snowflake): Flow<GuildEmoji> {
        return storeOnEach(supplier.getEmojis(guildId)) { it.data }

    }

    override fun getCurrentUserGuilds(limit: Int?): Flow<Guild> {
        return storeOnEach(supplier.getCurrentUserGuilds(limit)) { it.data }

    }

    override fun getChannelWebhooks(channelId: Snowflake): Flow<Webhook> {
        return storeOnEach(supplier.getChannelWebhooks(channelId)) { it.data }
    }

    override fun getGuildWebhooks(guildId: Snowflake): Flow<Webhook> {
        return storeOnEach(supplier.getGuildWebhooks(guildId)) { it.data }
    }

    override suspend fun getWebhookOrNull(id: Snowflake): Webhook? {
        return storeAndReturn(supplier.getWebhookOrNull(id)) { it.data }
    }

    override suspend fun getWebhookWithTokenOrNull(id: Snowflake, token: String): Webhook? {
        return storeAndReturn(supplier.getWebhookWithTokenOrNull(id, token)) { it.data }
    }

    override suspend fun getWebhookMessageOrNull(
        webhookId: Snowflake,
        token: String,
        messageId: Snowflake,
        threadId: Snowflake?,
    ): Message? {
        return storeAndReturn(supplier.getWebhookMessageOrNull(webhookId, token, messageId, threadId)) { it.data }
    }

    override suspend fun getTemplateOrNull(code: String): Template? {
        return storeAndReturn(supplier.getTemplateOrNull(code)) { it.data }
    }

    override fun getTemplates(guildId: Snowflake): Flow<Template> {
        return storeOnEach(supplier.getTemplates(guildId)) { it.data }
    }

    override suspend fun getStageInstanceOrNull(channelId: Snowflake): StageInstance? {
        return storeAndReturn(supplier.getStageInstanceOrNull(channelId)) { it.data }
    }

    override fun getThreadMembers(channelId: Snowflake): Flow<ThreadMember> {
        return storeOnEach(supplier.getThreadMembers(channelId)) { it.data }
    }

    override fun getActiveThreads(guildId: Snowflake): Flow<ThreadChannel> {
        return storeOnEach(supplier.getActiveThreads(guildId)) { it.data }
    }

    override fun getPublicArchivedThreads(channelId: Snowflake, before: Instant?, limit: Int?): Flow<ThreadChannel> {
        return storeOnEach(supplier.getPublicArchivedThreads(channelId, before, limit)) { it.data }
    }

    override fun getPrivateArchivedThreads(channelId: Snowflake, before: Instant?, limit: Int?): Flow<ThreadChannel> {
        return storeOnEach(supplier.getPrivateArchivedThreads(channelId, before, limit)) { it.data }
    }

    override fun getJoinedPrivateArchivedThreads(
        channelId: Snowflake,
        before: Snowflake?,
        limit: Int?,
    ): Flow<ThreadChannel> {
        return storeOnEach(supplier.getJoinedPrivateArchivedThreads(channelId, before, limit)) { it.data }
    }

    override fun getGuildApplicationCommands(
        applicationId: Snowflake,
        guildId: Snowflake,
        withLocalizations: Boolean?
    ): Flow<GuildApplicationCommand> {
        return storeOnEach(supplier.getGuildApplicationCommands(applicationId, guildId, withLocalizations)) { it.data }
    }

    override suspend fun getGuildApplicationCommandOrNull(
        applicationId: Snowflake,
        guildId: Snowflake,
        commandId: Snowflake
    ): GuildApplicationCommand? {
        return storeAndReturn(supplier.getGuildApplicationCommandOrNull(applicationId, guildId, commandId)) { it.data }
    }

    override suspend fun getGlobalApplicationCommandOrNull(
        applicationId: Snowflake,
        commandId: Snowflake
    ): GlobalApplicationCommand? {
        return storeAndReturn(supplier.getGlobalApplicationCommandOrNull(applicationId, commandId)) { it.data }
    }

    override fun getGlobalApplicationCommands(applicationId: Snowflake, withLocalizations: Boolean?): Flow<GlobalApplicationCommand> {
        return storeOnEach(supplier.getGlobalApplicationCommands(applicationId, withLocalizations)) { it.data }
    }

    override suspend fun getApplicationCommandPermissionsOrNull(
        applicationId: Snowflake,
        guildId: Snowflake,
        commandId: Snowflake
    ): ApplicationCommandPermissions? {
        return storeAndReturn(
            supplier.getApplicationCommandPermissionsOrNull(
                applicationId,
                guildId,
                commandId
            )
        ) { it.data }
    }

    override suspend fun getFollowupMessageOrNull(
        applicationId: Snowflake,
        interactionToken: String,
        messageId: Snowflake,
    ): FollowupMessage? {
        return storeAndReturn(supplier.getFollowupMessageOrNull(applicationId, interactionToken, messageId)) {
            it.message.data
        }
    }

    override fun getGuildScheduledEvents(guildId: Snowflake): Flow<GuildScheduledEvent> =
        storeOnEach(supplier.getGuildScheduledEvents(guildId)) { it.data }

    override suspend fun getGuildScheduledEventOrNull(guildId: Snowflake, eventId: Snowflake): GuildScheduledEvent? =
        storeAndReturn(supplier.getGuildScheduledEventOrNull(guildId, eventId)) { it.data }

    override suspend fun getStickerOrNull(id: Snowflake): Sticker? {
        return storeAndReturn(supplier.getStickerOrNull(id)) { it.data }
    }

    override suspend fun getGuildStickerOrNull(guildId: Snowflake, id: Snowflake): GuildSticker? {
        return storeAndReturn(supplier.getGuildStickerOrNull(guildId, id)) { it.data }
    }

    override fun getNitroStickerPacks(): Flow<StickerPack> {
        return storeOnEach(supplier.getNitroStickerPacks()) { it.data }
    }

    override fun getGuildStickers(guildId: Snowflake): Flow<GuildSticker> {
        return storeOnEach(supplier.getGuildStickers(guildId)) { it.data }

    }

    override fun getGuildScheduledEventUsersBefore(
        guildId: Snowflake,
        eventId: Snowflake,
        before: Snowflake,
        limit: Int?,
    ): Flow<User> = storeOnEach(supplier.getGuildScheduledEventUsersBefore(guildId, eventId, before, limit)) { it.data }

    public override fun getGuildScheduledEventUsersAfter(
        guildId: Snowflake,
        eventId: Snowflake,
        after: Snowflake,
        limit: Int?,
    ): Flow<User> = storeOnEach(supplier.getGuildScheduledEventUsersAfter(guildId, eventId, after, limit)) { it.data }

    override fun getGuildScheduledEventMembersBefore(
        guildId: Snowflake,
        eventId: Snowflake,
        before: Snowflake,
        limit: Int?,
    ): Flow<Member> =
        storeOnEach(supplier.getGuildScheduledEventMembersBefore(guildId, eventId, before, limit)) { it.data }

    override fun getGuildScheduledEventMembersAfter(
        guildId: Snowflake,
        eventId: Snowflake,
        after: Snowflake,
        limit: Int?,
    ): Flow<Member> =
        storeOnEach(supplier.getGuildScheduledEventMembersAfter(guildId, eventId, after, limit)) { it.data }


    override fun getGuildApplicationCommandPermissions(
        applicationId: Snowflake,
        guildId: Snowflake
    ): Flow<ApplicationCommandPermissions> {
        return storeOnEach(supplier.getGuildApplicationCommandPermissions(applicationId, guildId)) { it.data }
    }

    private inline fun <T, reified R : Any> storeOnEach(source: Flow<T>, crossinline transform: (T) -> R): Flow<T> {
        return source.onEach { fetchedEntity ->
            storeAndReturn(fetchedEntity) { transform(it) }
        }
    }

    private suspend inline fun <T, reified R : Any> storeAndReturn(value: T?, transform: (T) -> R): T? {
        if (value == null) return null
        cache.put(transform(value))
        return value
    }


    override fun toString(): String = "StoreEntitySupplier(supplier=$supplier, cache=$cache)"
}
