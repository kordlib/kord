package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.common.entity.MessageType
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.exception.RequestException
import com.gitlab.kordlib.core.*
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.behavior.MessageBehavior
import com.gitlab.kordlib.core.behavior.RoleBehavior
import com.gitlab.kordlib.core.behavior.UserBehavior
import com.gitlab.kordlib.core.behavior.channel.ChannelBehavior
import com.gitlab.kordlib.core.cache.data.MessageData
import com.gitlab.kordlib.core.entity.channel.*
import com.gitlab.kordlib.core.exception.EntityNotFoundException
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.core.supplier.getChannelOf
import com.gitlab.kordlib.core.supplier.getChannelOfOrNull
import kotlinx.coroutines.flow.*
import java.time.Instant
import java.time.format.DateTimeFormatter

private const val guildDeprecationMessage = "Guild ids aren't consistently present, use getGuild() instead."

/**
 * An instance of a [Discord Message][https://discordapp.com/developers/docs/resources/channel#message-object].
 */
class Message(
        val data: MessageData,
        override val kord: Kord,
        override val supplier: EntitySupplier = kord.defaultSupplier
) : MessageBehavior {

    /**
     * The id of this message.
     */
    override val id: Snowflake
        get() = Snowflake(data.id)

    /**
     * The id of the [MessageChannel] this message was send in.
     */
    override val channelId: Snowflake
        get() = Snowflake(data.channelId)

    /**
     * The id of the [Guild] this message was send in, if it was send in a [GuildMessageChannel].
     *
     * Returns null if this message was send in a [DmChannel].
     */
    @Deprecated(guildDeprecationMessage, ReplaceWith("getGuild()?.id"), DeprecationLevel.WARNING)
    val guildId: Snowflake?
        get() = data.guildId?.toSnowflakeOrNull()

    /**
     * The files attached to this message.
     */
    val attachments: Set<Attachment> get() = data.attachments.asSequence().map { Attachment(it, kord) }.toSet()

    /**
     * The author of this message, if it was created by a [User].
     *
     * Returns null if the author is not a Discord account, like a [Webhook] or systems message.
     */
    val author: User? get() = data.author?.let { User(it, kord) }

    /**
     * The content of this message.
     */
    val content: String get() = data.content

    /**
     * The instant when this message was last edited, if ever.
     *
     * Returns null if the message was never edited.
     */
    val editedTimestamp: Instant?
        get() = data.editedTimestamp?.let {
            DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(it, Instant::from)
        }

    /**
     * The embedded content of this message.
     *
     * This includes automatically embedded [videos][Embed.video] and [urls][Embed.Provider].
     */
    val embeds: List<Embed> get() = data.embeds.map { Embed(it, kord) }

    /**
     * The behavior of the [Guild] this message was send in, if it was send in a [GuildMessageChannel].
     *
     * Returns null if this message was send in a [DmChannel].
     */
    @Deprecated(guildDeprecationMessage, ReplaceWith("getGuild()"), DeprecationLevel.WARNING)
    val guild: GuildBehavior?
        get() = guildId?.let { GuildBehavior(it, kord) }

    /**
     * The ids of [Channels][Channel] specifically mentioned in this message.
     *
     * This collection can only contain values on crossposted messages, channels
     * mentioned inside the same guild will not be present.
     */
    val mentionedChannelIds: Set<Snowflake> get() = data.mentionedChannels.orEmpty().map { Snowflake(it) }.toSet()

    /**
     * The [Channels][ChannelBehavior] specifically mentioned in this message.
     *
     * This collection can only contain values on crossposted messages, channels
     * mentioned inside the same guild will not be present.
     */
    val mentionedChannelBehaviors: Set<ChannelBehavior> get() = data.mentionedChannels.orEmpty().map { ChannelBehavior(Snowflake(it), kord) }.toSet()

    /**
     * The [Channels][Channel] specifically mentioned in this message.
     *
     * This property will only emit values on crossposted messages, channels
     * mentioned inside the same guild will not be present.
     *
     * This request uses state [data] to resolve the entities belonging to the flow,
     * as such it can't guarantee an up to date representation if the [data] is outdated.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    val mentionedChannels: Flow<Channel>
        get() = mentionedChannelIds.asFlow().map { supplier.getChannel(it) }

    /**
     * True if this message mentions `@everyone`.
     */
    val mentionsEveryone: Boolean get() = data.mentionEveryone

    /**
     * The [ids][Role.id] of roles mentioned in this message.
     */
    val mentionedRoleIds: Set<Snowflake> get() = data.mentionRoles.map { Snowflake(it) }.toSet()

    /**
     * The [Behaviors][RoleBehavior] of roles mentioned in this message.
     */
    @Deprecated(
            "Guild ids aren't consistently present",
            ReplaceWith("roles.toSet()", "kotlinx.coroutines.flow.*"),
            DeprecationLevel.ERROR
    )
    val mentionedRoleBehaviors: Set<RoleBehavior>
        get() = error("Guild ids aren't consistently present, use `roles.toSet()` instead")

    /**
     * The [roles][Role] mentioned in this message.
     *
     * This request uses state [data] to resolve the entities belonging to the flow,
     * as such it can't guarantee an up to date representation if the [data] is outdated.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    val mentionedRoles: Flow<Role>
        get() = flow {
            if (mentionedRoleIds.isEmpty()) return@flow

            val guild = getGuild()
            supplier.getGuildRoles(guild.id).filter { it.id in mentionedRoleIds }
        }

    /**
     * The [ids][User.id] of users mentioned in this message.
     */
    val mentionedUserIds: Set<Snowflake> get() = data.mentions.map { Snowflake(it) }.toSet()

    /**
     * The [Behaviors][UserBehavior] of users mentioned in this message.
     */
    val mentionedUserBehaviors: Set<UserBehavior> get() = data.mentions.map { UserBehavior(Snowflake(it), kord) }.toSet()

    /**
     * The [users][User] mentioned in this message.
     *
     * This request uses state [data] to resolve the entities belonging to the flow,
     * as such it can't guarantee an up to date representation if the [data] is outdated.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    val mentionedUsers: Flow<User>
        get() = data.mentions.asFlow().map { supplier.getUser(Snowflake(it)) }

    /**
     * Whether the message was pinned in its [channel].
     */
    val isPinned get() = data.pinned

    /**
     * The reactions to this message.
     */
    val reactions: Set<Reaction> get() = data.reactions.orEmpty().asSequence().map { Reaction(it, kord) }.toSet()

    /**
     * The instant when this message was created.
     */
    val timestamp: Instant get() = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(data.timestamp, Instant::from)

    /**
     * Whether this message was send using `\tts`.
     */
    val tts: Boolean get() = data.tts

    /**
     * The type of this message.
     */
    val type: MessageType get() = data.type

    /**
     * The [id][Webhook.id] of the [Webhook] that was used to send this message.
     *
     * Returns null if this message was not send using a webhook.
     */
    val webhookId: Snowflake? get() = data.webhookId?.let(::Snowflake)

    /**
     * Returns itself.
     */
    override suspend fun asMessage(): Message = this

    /**
     * Requests to get the channel this message was send in.
     */
    suspend fun getChannel(): MessageChannel = supplier.getChannelOf(channelId)

    suspend fun getChannelOrNull(): MessageChannel? = supplier.getChannelOfOrNull(channelId)

    /**
     * Requests to get the [author] as a member.
     *
     * Returns null if the message was not send in a [GuildMessageChannel], or if the [author] is not a [User].
     */
    suspend fun getAuthorAsMember(): Member? {
        val author = author ?: return null
        val guildId = getGuild().id
        return author.asMember(guildId)
    }

    /**
     * Requests to get the guild of this message.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [Guild] wasn't present.
     * @throws [ClassCastException] if this message wasn't made in a guild.
     */
    suspend fun getGuild(): Guild = supplier.getChannelOf<GuildChannel>(channelId).getGuild()

    /**
     * Requests to get the guild of this message,
     * returns null if the [Guild] isn't present or this message wasn't made in a guild.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getGuildOrNull(): Guild? = supplier.getChannelOfOrNull<GuildChannel>(channelId)?.getGuildOrNull()

    /**
     * Returns a new [Message] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Message = Message(data, kord, strategy.supply(kord))

}
