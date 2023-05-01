package dev.kord.core.supplier

import dev.kord.cache.api.DataCache
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.any
import dev.kord.core.cache.data.*
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
    private inline val cache: TypedCache get() = kord.cache

    /**
     *  Returns a [Flow] of [Channel]s fetched from cache.
     *
     *  The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val channels: Flow<Channel>
        get() = cache.channels()
            .filter()
            .map { Channel.from(it, kord) }

    /**
     *  fetches all cached [Guild]s
     *
     *  The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    override val guilds: Flow<Guild>
        get() = cache.guilds()
            .filter()
            .map { Guild(it, kord) }

    /**
     *  fetches all cached [Region]s
     *
     *  The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    override val regions: Flow<Region>
        get() = cache.regions()
            .filter()
            .map { Region(it, kord) }

    /**
     *  fetches all cached [Role]s
     */
    public val roles: Flow<Role>
        get() = cache.roles()
            .filter()
            .map { Role(it, kord) }

    /**
     *  fetches all cached [User]s
     */
    public val users: Flow<User>
        get() = cache.users()
            .filter()
            .map { User(it, kord) }

    /**
     *  fetches all cached [Member]s
     */
    public val members: Flow<Member>
        get() = cache.members()
            .filter()
            .map {
                val userData = getUser(it.userId)
                Member(it, userData.data, kord)
            }

    public suspend fun getRole(id: Snowflake): Role? =
        cache.roles()
            .filter { it.id == id }
            .map { Role(it, kord) }
            .singleOrNull()

    override suspend fun getGuildPreviewOrNull(guildId: Snowflake): GuildPreview? =
        cache.guildPreviews()
            .filter { it.id == guildId }
            .map { GuildPreview(it, kord) }
            .singleOrNull()


    override suspend fun getGuildWidgetOrNull(guildId: Snowflake): GuildWidget? = null

    override suspend fun getChannelOrNull(id: Snowflake): Channel? =
        cache.channels()
            .filter { it.id == id }
            .map { Channel.from(it, kord) }
            .singleOrNull()


    override fun getGuildChannels(guildId: Snowflake): Flow<TopGuildChannel> =
        cache.channels()
            .filter { it.guildId.value == guildId }
            .map { Channel.from(it, kord) }
            .filterIsInstance()

    override fun getChannelPins(channelId: Snowflake): Flow<Message> =
        cache.messages()
            .filter { it.id == channelId && it.pinned }
            .map { Message(it, kord) }

    override suspend fun getGuildOrNull(id: Snowflake): Guild? =
        cache.guilds()
            .filter { it.id == id }
            .map { Guild(it, kord) }
            .singleOrNull()

    override suspend fun getMemberOrNull(guildId: Snowflake, userId: Snowflake): Member? {
        val user = getUserOrNull(userId) ?: return null
        return cache.members()
            .filter { it.guildId == guildId && it.userId == userId }
            .map { Member(it, user.data, kord) }
            .singleOrNull()

    }

    override suspend fun getMessageOrNull(channelId: Snowflake, messageId: Snowflake): Message? =
        cache.messages()
            .filter { it.id == messageId && it.channelId == channelId }
            .map { Message(it, kord) }
            .singleOrNull()

    override fun getMessagesAfter(messageId: Snowflake, channelId: Snowflake, limit: Int?): Flow<Message> {
        checkLimit(limit)
        return cache.messages()
            .filter { it.id < messageId && it.channelId == channelId }
            .limit(limit)
            .map { Message(it, kord) }
    }

    override fun getMessagesBefore(messageId: Snowflake, channelId: Snowflake, limit: Int?): Flow<Message> {
        checkLimit(limit)
        return cache.messages()
            .filter { it.id > messageId && it.channelId == channelId }
            .limit(limit)
            .map { Message(it, kord) }
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

    override suspend fun getRoleOrNull(guildId: Snowflake, roleId: Snowflake): Role? =
        cache.roles()
            .filter { it.id == roleId && it.guildId == guildId }
            .map { Role(it, kord) }
            .singleOrNull()


    override fun getGuildRoles(guildId: Snowflake): Flow<Role> =
        cache.roles()
            .filter { it.guildId == guildId }
            .map { Role(it, kord) }

    override suspend fun getGuildBanOrNull(guildId: Snowflake, userId: Snowflake): Ban? =
        cache.bans()
            .filter { it.userId == userId }
            .map { Ban(it, kord) }
            .singleOrNull()

    override fun getGuildBans(guildId: Snowflake, limit: Int?): Flow<Ban> {
        checkLimit(limit)
        return cache.bans()
            .filter { it.guildId == guildId }
            .limit(limit)
            .map { Ban(it, kord) }
    }

    override fun getGuildMembers(guildId: Snowflake, limit: Int?): Flow<Member> {
        checkLimit(limit)
        return cache.members()
            .filter { it.guildId == guildId }
            .mapNotNull { memberData ->
                val userData = getUserOrNull(memberData.userId)
                userData?.let { Member(memberData, userData = it.data, kord) }
            }
            .limit(limit)
    }

    override fun getGuildVoiceRegions(guildId: Snowflake): Flow<Region> =
        cache.regions()
            .filter { it.guildId.value == guildId }
            .map { Region(it, kord) }

    override suspend fun getEmojiOrNull(guildId: Snowflake, emojiId: Snowflake): GuildEmoji? =
        cache.emojis()
            .filter { it.id == emojiId && it.guildId == guildId }
            .map { GuildEmoji(it, kord) }
            .singleOrNull()

    override fun getEmojis(guildId: Snowflake): Flow<GuildEmoji> =
        cache.emojis()
            .filter { it.guildId == guildId }
            .map { GuildEmoji(it, kord) }

    override fun getCurrentUserGuilds(limit: Int?): Flow<Guild> {
        checkLimit(limit)
        return guilds.filter {
            members.any { it.id == kord.selfId }
        }.limit(limit)
    }

    override fun getChannelWebhooks(channelId: Snowflake): Flow<Webhook> =
        cache.webhooks()
            .filter { it.channelId == channelId }
            .map { Webhook(it, kord) }

    override fun getGuildWebhooks(guildId: Snowflake): Flow<Webhook> =
        cache.webhooks()
            .filter { it.guildId.value == guildId }
            .map { Webhook(it, kord) }

    override suspend fun getWebhookOrNull(id: Snowflake): Webhook? =
        cache.webhooks()
            .filter { it.id == id }
            .map { Webhook(it, kord) }
            .singleOrNull()

    override suspend fun getWebhookWithTokenOrNull(id: Snowflake, token: String): Webhook? =
        cache.webhooks()
            .filter { it.id == id && it.token.value == token }
            .map { Webhook(it, kord) }
            .singleOrNull()

    override suspend fun getWebhookMessageOrNull(
        webhookId: Snowflake,
        token: String,
        messageId: Snowflake,
        threadId: Snowflake?,
    ): Message? =
        cache.messages()
            .filter {
                val result = it.id == webhookId && it.id == messageId
                if (threadId == null) result else result && it.channelId == threadId
            }
            .map { Message(it, kord) }
            .singleOrNull()


    override suspend fun getUserOrNull(id: Snowflake): User? =
        cache.users()
            .filter { it.id == id }
            .map { User(it, kord) }
            .singleOrNull()

    override suspend fun getTemplateOrNull(code: String): Template? =
        cache.templates()
            .filter { it.code == code }
            .map { Template(it, kord) }
            .singleOrNull()

    override fun getTemplates(guildId: Snowflake): Flow<Template> =
        cache.templates()
            .filter { it.sourceGuildId == it.sourceGuildId }
            .map { Template(it, kord) }


    override suspend fun getStageInstanceOrNull(channelId: Snowflake): StageInstance? = null

    override fun getThreadMembers(channelId: Snowflake): Flow<ThreadMember> =
        cache.threadMembers()
            .filter { it.id == channelId }
            .map { ThreadMember(it, kord) }

    override fun getActiveThreads(guildId: Snowflake): Flow<ThreadChannel> = flow {
        cache.channels()
            .filter { it.guildId.value == guildId && it.threadMetadata.value?.archived != true }
            .mapNotNull { Channel.from(it, kord) as? ThreadChannel }
            .toList()
            .sortedByDescending { it.id }
            .forEach { emit(it) }
    }

    override fun getPublicArchivedThreads(channelId: Snowflake, before: Instant?, limit: Int?): Flow<ThreadChannel> {
        checkLimit(limit)
        return flow {
            val result = cache.channels()
                .filter {
                    val time = it.threadMetadata.value?.archiveTimestamp
                    it.threadMetadata.value?.archived == true
                            && time != null
                            && (before == null || time < before)
                            && (it.type == ChannelType.PublicGuildThread || it.type == ChannelType.PublicNewsThread)
                            && it.parentId?.value == channelId
                }
                .mapNotNull { Channel.from(it, kord) as? ThreadChannel }
                .toList()
                .sortedByDescending { it.archiveTimestamp }
                .asFlow()

            emitAll(result)
        }
    }

    override fun getPrivateArchivedThreads(channelId: Snowflake, before: Instant?, limit: Int?): Flow<ThreadChannel> {
        checkLimit(limit)
        return flow {
            val result = cache.channels()
                .filter {
                    val time = it.threadMetadata.value?.archiveTimestamp
                    it.threadMetadata.value?.archived == true
                            && time != null
                            && (before == null || time < before)
                            && it.type == ChannelType.PrivateThread
                            && it.parentId?.value == channelId
                }
                .mapNotNull { Channel.from(it, kord) as? ThreadChannel }
                .toList()
                .sortedByDescending { it.archiveTimestamp }
                .asFlow()

            emitAll(result)
        }
    }

    override fun getJoinedPrivateArchivedThreads(
        channelId: Snowflake,
        before: Snowflake?,
        limit: Int?,
    ): Flow<ThreadChannel> {
        checkLimit(limit)
        return flow {
            val result = cache.channels()
                .filter {
                    it.threadMetadata.value?.archived == true
                            && (before == null || it.id < before)
                            && it.type == ChannelType.PrivateThread
                            && it.member !is Optional.Missing
                            && it.parentId?.value == channelId
                }
                .mapNotNull { Channel.from(it, kord) as? ThreadChannel }
                .toList()
                .sortedByDescending { it.id }
                .asFlow()
                .limit(limit)


            emitAll(result)
        }
    }

    override fun getGuildApplicationCommands(
        applicationId: Snowflake,
        guildId: Snowflake,
        withLocalizations: Boolean?
    ): Flow<GuildApplicationCommand> =
        cache.applicationCommands()
            .filter { it.guildId.value == guildId && it.applicationId == applicationId }
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
        cache.applicationCommands()
            .filter { it.guildId.value == guildId && it.applicationId == applicationId && it.id == commandId }
            .map { GuildApplicationCommand(it, kord.rest.interaction) }
            .singleOrNull()


    override suspend fun getGlobalApplicationCommandOrNull(
        applicationId: Snowflake,
        commandId: Snowflake
    ): GlobalApplicationCommand? =
        cache.applicationCommands()
            .filter { it.guildId.value == null && it.applicationId == applicationId && it.id == commandId }
            .map { GlobalApplicationCommand(it, kord.rest.interaction) }
            .singleOrNull()


    override fun getGlobalApplicationCommands(
        applicationId: Snowflake,
        withLocalizations: Boolean?
    ): Flow<GlobalApplicationCommand> =
        cache.applicationCommands()
            .filter { it.guildId.value == null && it.applicationId == applicationId }
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
    ): Flow<ApplicationCommandPermissions> =
        cache.applicationCommandPermissions()
            .filter { it.applicationId == applicationId && it.guildId == guildId }
            .map { ApplicationCommandPermissions(it) }



    override suspend fun getApplicationCommandPermissionsOrNull(
        applicationId: Snowflake,
        guildId: Snowflake,
        commandId: Snowflake
    ): ApplicationCommandPermissions? =
        cache.applicationCommandPermissions()
            .filter { it.applicationId == applicationId && it.guildId == guildId  && it.id == commandId }
            .map { ApplicationCommandPermissions(it) }
            .singleOrNull()

    override suspend fun getFollowupMessageOrNull(
        applicationId: Snowflake,
        interactionToken: String,
        messageId: Snowflake,
    ): FollowupMessage? =
        cache.messages()
            .filter {it.id == messageId && it.applicationId.value == applicationId }
            .map { FollowupMessage(Message(it, kord), applicationId, interactionToken, kord) }
            .singleOrNull()


    override suspend fun getGuildScheduledEventOrNull(guildId: Snowflake, eventId: Snowflake): GuildScheduledEvent? =
         cache.guildScheduledEvents()
            .filter {it.id == eventId && it.guildId == guildId }
            .map { GuildScheduledEvent(it, kord) }
            .singleOrNull()


    override fun getGuildScheduledEventMembersBefore(
        guildId: Snowflake,
        eventId: Snowflake,
        before: Snowflake,
        limit: Int?,
    ): Flow<Member> {
        checkLimit(limit)
        return cache.members()
            .filter { it.userId < before && it.guildId == guildId }
            .mapNotNull {
                val userData = getUserOrNull(it.userId) ?: return@mapNotNull null
                Member(it, userData.data, kord)
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
        return cache.members()
            .filter { it.guildId == guildId && it.userId > after }
            .mapNotNull {
                val userData = getUserOrNull(it.userId) ?: return@mapNotNull null
                Member(it, userData.data, kord)
            }
            .limit(limit)
    }

    override fun getGuildScheduledEventUsersAfter(
        guildId: Snowflake,
        eventId: Snowflake,
        after: Snowflake,
        limit: Int?,
    ): Flow<User> = getGuildScheduledEventMembersAfter(guildId, eventId, after, limit).map { it.asUser() }

    override suspend fun getStickerOrNull(id: Snowflake): Sticker? =
        cache.stickers()
            .filter { it.id == id }
            .map { Sticker(it, kord) }
            .singleOrNull()


    override suspend fun getGuildStickerOrNull(guildId: Snowflake, id: Snowflake): GuildSticker? =
        cache.stickers()
            .filter { it.id == id && it.guildId.value == guildId }
            .map { GuildSticker(it, kord) }
            .singleOrNull()

    override fun getNitroStickerPacks(): Flow<StickerPack> =
        cache.stickerPacks()
            .filter()
            .map { StickerPack(it, kord) }

    override fun getGuildStickers(guildId: Snowflake): Flow<GuildSticker> =
        cache.stickers()
            .filter { it.id == guildId }
            .map { GuildSticker(it, kord) }

    override fun getGuildScheduledEvents(guildId: Snowflake): Flow<GuildScheduledEvent> =
        cache.guildScheduledEvents()
            .filter { it.guildId == guildId }
            .map { GuildScheduledEvent(it, kord) }

    override fun getAutoModerationRules(guildId: Snowflake): Flow<AutoModerationRule> =
        cache.autoModerationRules()
            .filter { it.guildId == guildId }
            .map { AutoModerationRule(it, kord) }

    override suspend fun getAutoModerationRuleOrNull(guildId: Snowflake, ruleId: Snowflake): AutoModerationRule? =
        cache.autoModerationRules()
            .filter { it.id == ruleId && it.guildId == guildId }
            .map { AutoModerationRule(it, kord) }
            .singleOrNull()



    override fun toString(): String = "CacheEntitySupplier(cache=$cache)"
}


private fun checkLimit(limit: Int?) {
    require(limit == null || limit > 0) { "At least 1 item should be requested, but got $limit." }
}

private fun <T> Flow<T>.limit(limit: Int?): Flow<T> = if (limit == null) this else take(limit)
