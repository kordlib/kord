package dev.kord.core.supplier

import dev.kord.cache.api.DataCache
import dev.kord.cache.api.query
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.any
import dev.kord.core.cache.api.TypedCache
import dev.kord.core.cache.data.*
import dev.kord.core.cache.idEq
import dev.kord.core.cache.idGt
import dev.kord.core.cache.idLt
import dev.kord.core.entity.*
import dev.kord.core.entity.application.ApplicationCommandPermissions
import dev.kord.core.entity.application.GlobalApplicationCommand
import dev.kord.core.entity.application.GuildApplicationCommand
import dev.kord.core.entity.automoderation.AutoModerationRule
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.TopGuildChannel
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.entity.channel.thread.ThreadMember
import dev.kord.core.entity.interaction.followup.FollowupMessage
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.gateway.Gateway
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Instant

/**
 * [EntitySupplier] that uses a [DataCache] to resolve entities.
 *
 * Getting existing entities should not throw any [RequestException] unless
 * specified by the configured cache.
 *
 * The supplier might not be able to return entities independent of their actual
 * existence, their presence depends on incoming events from the [Gateway] and
 * how the cache is set up to store entities.
 *
 * Returned flows without entities will not throw an [EntityNotFoundException]
 * if none are presented like other `getX` functions. Instead, the flow will be empty.
 */
public class CacheEntitySupplier(private val kord: Kord) : EntitySupplier {

    /**
     * The Cache this [CacheEntitySupplier] operates on.
     *
     * Shorthand for [kord.cache][Kord.cache].
     */
    private inline val cache: TypedCache get() = TODO()

    /**
     *  Returns a [Flow] of [Channel]s fetched from cache.
     *
     *  The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val channels: Flow<Channel>
        get() = cache.getType<ChannelData>()
            .asSet()
            .asFlow()
            .map { Channel.from(it, kord) }

    /**
     *  fetches all cached [Guild]s
     *
     *  The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    override val guilds: Flow<Guild>
        get() = cache.getType<GuildData>()
            .asSet()
            .asFlow()
            .map { Guild(it, kord) }

    /**
     *  fetches all cached [Region]s
     *
     *  The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    override val regions: Flow<Region>
        get() = cache.getType<RegionData>()
            .asSet()
            .asFlow()
            .map { Region(it, kord) }

    /**
     *  fetches all cached [Role]s
     */
    public val roles: Flow<Role>
        get() = cache.getType<RoleData>()
            .asSet()
            .asFlow()
            .map { Role(it, kord) }

    /**
     *  fetches all cached [User]s
     */
    public val users: Flow<User>
        get() = cache.getType<UserData>()
            .asSet()
            .asFlow()
            .map { User(it, kord) }

    /**
     *  fetches all cached [Member]s
     */
    public val members: Flow<Member>
        get() = cache.getType<MemberData>()
            .asSet()
            .asFlow().mapNotNull { member ->
            val userData = cache.getType<UserData>().get { it.id == member.userId } ?: return@mapNotNull null
            Member(member, userData, kord)
        }

    public suspend fun getRole(id: Snowflake): Role? {
        val data = cache.getType<RoleData>().get { it.id == id } ?: return null
        return Role(data, kord)
    }

    override suspend fun getGuildPreviewOrNull(guildId: Snowflake): GuildPreview? {
        val data = cache.getType<GuildPreviewData>().get { it.id == guildId } ?: return null
        return GuildPreview(data, kord)
    }

    override suspend fun getGuildWidgetOrNull(guildId: Snowflake): GuildWidget? = null

    override suspend fun getChannelOrNull(id: Snowflake): Channel? {
        val data = cache.getType<ChannelData>().get { it.id == id } ?: return null
        return Channel.from(data, kord)
    }

    override fun getGuildChannels(guildId: Snowflake): Flow<TopGuildChannel> =
        cache.getType<ChannelData>()
            .asSet()
            .asFlow()
            .map { Channel.from(it, kord) }
            .filterIsInstance()

    override fun getChannelPins(channelId: Snowflake): Flow<Message> =
        cache.getType<MessageData>()
            .asSet()
            .asFlow()
            .filter { it.channelId == channelId && it.pinned }
            .map { Message(it, kord) }

    override suspend fun getGuildOrNull(id: Snowflake): Guild? {
        val data = cache.getType<GuildData>().get { it.id == id } ?: return null
        return Guild(data, kord)
    }

    override suspend fun getMemberOrNull(guildId: Snowflake, userId: Snowflake): Member? {
        val userData = cache.getType<UserData>().get { it.id == userId } ?: return null
        val memberData = cache.getType<MemberData>().get {it.userId == userId && it.guildId == guildId } ?: return null
        return Member(memberData, userData, kord)
    }

    override suspend fun getMessageOrNull(channelId: Snowflake, messageId: Snowflake): Message? {
        val data = cache.getType<MessageData>().get { it.id == messageId && it.channelId == channelId} ?: return null
        return Message(data, kord)
    }

    override fun getMessagesAfter(messageId: Snowflake, channelId: Snowflake, limit: Int?): Flow<Message> {
        checkLimit(limit)
        return cache.getType<MessageData>()
            .asSet()
            .asFlow()
            .filter { it.channelId == channelId && it.id > messageId }
            .map { Message(it, kord) }
            .limit(limit)
    }

    override fun getMessagesBefore(messageId: Snowflake, channelId: Snowflake, limit: Int?): Flow<Message> {
        checkLimit(limit)
        return cache.getType<MessageData>()
            .asSet()
            .asFlow()
            .filter { it.channelId == channelId && it.id < messageId }
            .map { Message(it, kord) }.limit(limit)
    }


    override fun getMessagesAround(messageId: Snowflake, channelId: Snowflake, limit: Int): Flow<Message> {
        require(limit in 1..100) { "Expected limit to be in 1..100, but was $limit" }
        return flow {
            emitAll(getMessagesBefore(messageId, channelId, limit / 2))
            getMessageOrNull(channelId, messageId)?.let { emit(it) }
            emitAll(getMessagesAfter(messageId, channelId, limit / 2))
        }
    }

    override suspend fun getSelfOrNull(): User? = getUserOrNull(kord.selfId)

    override suspend fun getRoleOrNull(guildId: Snowflake, roleId: Snowflake): Role? {
        val data = cache.getType<RoleData>().get { it.id == roleId && it.guildId == guildId } ?: return null
        return Role(data, kord)
    }

    override fun getGuildRoles(guildId: Snowflake): Flow<Role> =
        cache.getType<RoleData>()
            .asSet()
            .asFlow()
            .filter { it.guildId == guildId }
            .map { Role(it, kord) }

    override suspend fun getGuildBanOrNull(guildId: Snowflake, userId: Snowflake): Ban? {
        val data = cache.getType<BanData>().get { it.userId == userId && it.guildId == guildId } ?: return null
        return Ban(data, kord)
    }

    override fun getGuildBans(guildId: Snowflake, limit: Int?): Flow<Ban> {
        checkLimit(limit)
        return cache.getType<BanData>()
            .asSet()
            .asFlow()
            .filter { it.guildId == guildId }
            .map { Ban(it, kord) }
            .limit(limit)
    }

    override fun getGuildMembers(guildId: Snowflake, limit: Int?): Flow<Member> {
        checkLimit(limit)
        return cache.getType<MemberData>()
            .asSet()
            .asFlow()
            .mapNotNull { memberData ->
                val userData = cache.getType<UserData>().get { it.id == memberData.userId }
                userData?.let { Member(memberData, userData = it, kord) }
            }
            .limit(limit)
    }

    override fun getGuildVoiceRegions(guildId: Snowflake): Flow<Region> = cache.getType<RegionData>()
        .asSet()
        .asFlow()
        .filter { it.guildId.value == guildId }
        .map { Region(it, kord) }

    override suspend fun getEmojiOrNull(guildId: Snowflake, emojiId: Snowflake): GuildEmoji? {
        val data = cache.getType<EmojiData>().get { it.guildId == guildId } ?: return null
        return GuildEmoji(data, kord)
    }

    override fun getEmojis(guildId: Snowflake): Flow<GuildEmoji> = cache.getType<EmojiData>()
        .asSet()
        .asFlow()
        .filter { it.guildId == guildId }
        .map { GuildEmoji(it, kord) }

    override fun getCurrentUserGuilds(limit: Int?): Flow<Guild> {
        checkLimit(limit)
        return guilds.filter {
            members.any { it.id == kord.selfId }
        }
            .limit(limit)
    }

    override fun getChannelWebhooks(channelId: Snowflake): Flow<Webhook> = cache.getType<WebhookData>()
        .asSet()
        .filter { it.channelId == channelId }
        .asFlow()
        .map { Webhook(it, kord) }

    override fun getGuildWebhooks(guildId: Snowflake): Flow<Webhook> = cache.getType<WebhookData>()
        .asSet()
        .asFlow()
        .filter{ it.guildId.value == guildId }
        .map { Webhook(it, kord) }

    override suspend fun getWebhookOrNull(id: Snowflake): Webhook? {
        val data = cache.getType<WebhookData>().get { it.id == id } ?: return null
        return Webhook(data, kord)
    }

    override suspend fun getWebhookWithTokenOrNull(id: Snowflake, token: String): Webhook? {
        val data = cache.getType<WebhookData>().get { it.id  == id && it.token.value == token } ?: return null
        return Webhook(data, kord)
    }

    override suspend fun getWebhookMessageOrNull(
        webhookId: Snowflake,
        token: String,
        messageId: Snowflake,
        threadId: Snowflake?,
    ): Message? {
        val data = cache.getType<MessageData>().get {
            val condition = it.webhookId.value == webhookId && it.id == messageId
            return@get if (threadId != null)  condition && it.channelId == threadId else condition
        }?: return null
        return Message(data, kord)
    }

    override suspend fun getUserOrNull(id: Snowflake): User? {
        val data = cache.getType<UserData>().get { it.id == id } ?: return null
        return User(data, kord)
    }

    override suspend fun getTemplateOrNull(code: String): Template? {
        val data = cache.getType<TemplateData>().get { it.code == code } ?: return null
        return Template(data, kord)
    }

    override fun getTemplates(guildId: Snowflake): Flow<Template> {
        return cache.getType<TemplateData>()
            .asSet()
            .asFlow()
            .filter { it.sourceGuildId == guildId }
            .map { Template(it, kord) }
    }

    override suspend fun getStageInstanceOrNull(channelId: Snowflake): StageInstance? = null

    override fun getThreadMembers(channelId: Snowflake): Flow<ThreadMember> {
        return cache.getType<ThreadMemberData>()
            .asSet()
            .asFlow()
            .filter { it.id == channelId }
            .map { ThreadMember(it, kord) }
    }

    override fun getActiveThreads(guildId: Snowflake): Flow<ThreadChannel> =
        cache.getType<ChannelData>()
            .asSet()
            .sortedByDescending { it.id }
            .asFlow()
            .filter { it.threadMetadata.value?.archived != true }
            .mapNotNull { Channel.from(it, kord) as? ThreadChannel }



    override fun getPublicArchivedThreads(channelId: Snowflake, before: Instant?, limit: Int?): Flow<ThreadChannel> {
        checkLimit(limit)
        return cache.getType<ChannelData>()
            .asSet()
            .sortedByDescending { it.threadMetadata.value?.archiveTimestamp }
            .asFlow()
            .filter {
                val metadataData = it.threadMetadata.value ?: return@filter false
                        it.parentId?.value == channelId
                        && metadataData.archived
                        && (before == null || metadataData.archiveTimestamp < before)
                        && (it.type == ChannelType.PublicGuildThread || it.type == ChannelType.PublicGuildThread)


            }
            .mapNotNull { Channel.from(it, kord) as? ThreadChannel }
            .limit(limit)
    }

    override fun getPrivateArchivedThreads(channelId: Snowflake, before: Instant?, limit: Int?): Flow<ThreadChannel> {
        checkLimit(limit)
        return cache.getType<ChannelData>()
            .asSet()
            .sortedByDescending { it.threadMetadata.value?.archiveTimestamp }
            .asFlow()
            .filter {
                val metadataData = it.threadMetadata.value ?: return@filter false
                        it.parentId?.value == channelId
                        && it.type == ChannelType.PrivateThread
                        && metadataData.archived
                        && (before == null || metadataData.archiveTimestamp < before)
            }
            .mapNotNull { Channel.from(it, kord) as? ThreadChannel }
            .limit(limit)
    }



    override fun getJoinedPrivateArchivedThreads(
        channelId: Snowflake,
        before: Snowflake?,
        limit: Int?,
    ): Flow<ThreadChannel> {
        checkLimit(limit)
        checkLimit(limit)
        return cache.getType<ChannelData>()
            .asSet()
            .sortedByDescending { it.threadMetadata.value?.archiveTimestamp }
            .asFlow()
            .filter {
                val metadataData = it.threadMetadata.value ?: return@filter false
                it.parentId?.value == channelId
                        && it.type == ChannelType.PrivateThread
                        && it.member !is Optional.Missing
                        && metadataData.archived
                        && (before == null || it.id < before)
            }
            .mapNotNull { Channel.from(it, kord) as? ThreadChannel }
            .limit(limit)
    }

    override fun getGuildApplicationCommands(
        applicationId: Snowflake,
        guildId: Snowflake,
        withLocalizations: Boolean?
    ): Flow<GuildApplicationCommand> =
        cache.getType<ApplicationCommandData>()
        .asSet()
        .asFlow()
        .filter { it.guildId.value == guildId  && it.applicationId == applicationId }
        .map {
            when (withLocalizations) {
                true, null -> it
                false -> it.copy(
                    nameLocalizations = Optional.Missing(),
                    descriptionLocalizations = Optional.Missing(),
                )
            }
        }
        .map { GuildApplicationCommand(it, kord.rest.interaction) }


    override suspend fun getGuildApplicationCommandOrNull(
        applicationId: Snowflake,
        guildId: Snowflake,
        commandId: Snowflake
    ): GuildApplicationCommand? =
        getGuildApplicationCommands(guildId, applicationId)
            .filter { it.id == commandId }
            .singleOrNull()

    override suspend fun getGlobalApplicationCommandOrNull(
        applicationId: Snowflake,
        commandId: Snowflake
    ): GlobalApplicationCommand? =
        getGlobalApplicationCommands(applicationId)
            .filter { it.id == commandId }
            .singleOrNull()

    override fun getGlobalApplicationCommands(applicationId: Snowflake, withLocalizations: Boolean?): Flow<GlobalApplicationCommand> =
        cache.getType<ApplicationCommandData>()
            .asSet()
            .asFlow()
            .filter { it.applicationId == applicationId && it.guildId.value == null }
            .map {
                when (withLocalizations) {
                    true, null -> it
                    false -> it.copy(
                        nameLocalizations = Optional.Missing(),
                        descriptionLocalizations = Optional.Missing(),
                    )
                }
            }
            .map { GlobalApplicationCommand(it, kord.rest.interaction) }

    override fun getGuildApplicationCommandPermissions(
        applicationId: Snowflake,
        guildId: Snowflake
    ): Flow<ApplicationCommandPermissions> = cache.getType<GuildApplicationCommandPermissionsData>()
        .asSet()
        .asFlow()
        .filter {it.applicationId == applicationId && it.guildId == guildId }
        .map { ApplicationCommandPermissions(it) }


    override suspend fun getApplicationCommandPermissionsOrNull(
        applicationId: Snowflake,
        guildId: Snowflake,
        commandId: Snowflake
    ): ApplicationCommandPermissions? =
        getGuildApplicationCommandPermissions(applicationId, guildId)
            .filter { it.id == commandId }
            .singleOrNull()

    override suspend fun getFollowupMessageOrNull(
        applicationId: Snowflake,
        interactionToken: String,
        messageId: Snowflake,
    ): FollowupMessage? {
        val data = cache.getType<MessageData>()
            .get { it.id == messageId && it.applicationId.value == applicationId } ?: return null
        return FollowupMessage(Message(data, kord), applicationId, interactionToken, kord)
    }

    override suspend fun getGuildScheduledEventOrNull(guildId: Snowflake, eventId: Snowflake): GuildScheduledEvent? {
        val data = cache.getType<GuildScheduledEventData>()
            .get { it.guildId == guildId && it.id == eventId } ?: return null
        return GuildScheduledEvent(data, kord)
    }

    override fun getGuildScheduledEventMembersBefore(
        guildId: Snowflake,
        eventId: Snowflake,
        before: Snowflake,
        limit: Int?,
    ): Flow<Member> {
        checkLimit(limit)
        return cache
            .getType<MemberData>()
            .asSet()
            .asFlow()
            .filter { it.guildId == guildId && it.userId < before }
            .mapNotNull {
                val userData = cache.getType<UserData>()
                    .get { user -> user.id == it.userId } ?: return@mapNotNull null
                Member(it, userData, kord)
            }
            .limit(limit)
    }

    override fun getGuildScheduledEventUsersBefore(
        guildId: Snowflake,
        eventId: Snowflake,
        before: Snowflake,
        limit: Int?,
    ): Flow<User> = getGuildScheduledEventMembersBefore(guildId, eventId, before, limit).map { it.asUser() }

    override fun getGuildScheduledEventMembersAfter(
        guildId: Snowflake,
        eventId: Snowflake,
        after: Snowflake,
        limit: Int?,
    ): Flow<Member> {
        checkLimit(limit)
        return cache
            .getType<MemberData>()
            .asSet()
            .asFlow()
            .filter { it.guildId == guildId && it.userId > after }
            .mapNotNull {
                val userData = cache.getType<UserData> { idEq(UserData::id, it.userId) }.singleOrNull()
                    ?: return@mapNotNull null
                Member(it, userData, kord)
            }
            .limit(limit)
    }

    override fun getGuildScheduledEventUsersAfter(
        guildId: Snowflake,
        eventId: Snowflake,
        after: Snowflake,
        limit: Int?,
    ): Flow<User> = getGuildScheduledEventMembersAfter(guildId, eventId, after, limit).map { it.asUser() }

    override suspend fun getStickerOrNull(id: Snowflake): Sticker? {
        val data = cache.getType<StickerData>().get { it.id == id } ?: return null
        return Sticker(data, kord)
    }

    override suspend fun getGuildStickerOrNull(guildId: Snowflake, id: Snowflake): GuildSticker? {
        val data = cache.getType<StickerData>().get { it.id == id && it.guildId.value == guildId } ?: return null
        return GuildSticker(data, kord)
    }

    override fun getNitroStickerPacks(): Flow<StickerPack> {
        return cache.getType<StickerPackData>()
            .asSet()
            .asFlow()
            .map { StickerPack(it, kord) }
    }

    override fun getGuildStickers(guildId: Snowflake): Flow<GuildSticker> {
        return cache.getType<StickerData>()
            .asSet()
            .asFlow()
            .map { GuildSticker(it, kord) }
    }

    override fun getGuildScheduledEvents(guildId: Snowflake): Flow<GuildScheduledEvent> =
        cache.getType<GuildScheduledEventData>()
            .asSet()
            .asFlow()
            .filter { it.guildId == guildId }
            .map { GuildScheduledEvent(it, kord) }

    override fun getAutoModerationRules(guildId: Snowflake): Flow<AutoModerationRule> =
        cache.query { idEq(AutoModerationRuleData::guildId, guildId) }
            .asFlow()
            .map { AutoModerationRule(it, kord) }

    override suspend fun getAutoModerationRuleOrNull(guildId: Snowflake, ruleId: Snowflake): AutoModerationRule? =
        cache
            .query {
                idEq(AutoModerationRuleData::id, ruleId)
                idEq(AutoModerationRuleData::guildId, guildId)
            }
            .singleOrNull()
            ?.let { AutoModerationRule(it, kord) }


    override fun toString(): String = "CacheEntitySupplier(cache=$cache)"
}


private fun checkLimit(limit: Int?) {
    require(limit == null || limit > 0) { "At least 1 item should be requested, but got $limit." }
}

private fun <T> Flow<T>.limit(limit: Int?): Flow<T> = if (limit == null) this else take(limit)
