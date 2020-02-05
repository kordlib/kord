package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.common.entity.MessageType
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.behavior.MessageBehavior
import com.gitlab.kordlib.core.behavior.RoleBehavior
import com.gitlab.kordlib.core.behavior.UserBehavior
import com.gitlab.kordlib.core.behavior.channel.ChannelBehavior
import com.gitlab.kordlib.core.cache.data.MessageData
import com.gitlab.kordlib.core.entity.channel.Channel
import com.gitlab.kordlib.core.entity.channel.DmChannel
import com.gitlab.kordlib.core.entity.channel.GuildMessageChannel
import com.gitlab.kordlib.core.entity.channel.MessageChannel
import com.gitlab.kordlib.core.toSnowflakeOrNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Instant
import java.time.format.DateTimeFormatter

/**
 * An instance of a [Discord Message][https://discordapp.com/developers/docs/resources/channel#message-object].
 */
class Message(val data: MessageData, override val kord: Kord) : MessageBehavior {

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
    val guildId: Snowflake? get() = data.guildId?.toSnowflakeOrNull()

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
    val guild: GuildBehavior? get() = guildId?.let { GuildBehavior(it, kord) }

    /**
     * The ids of [Channels][Channel] specifically mentioned in this message.
     *
     * This collection can only contain values on crossposted messages.
     */
    val mentionedChannelIds: Set<Snowflake> get() = data.mentionedChannels.orEmpty().map { Snowflake(it) }.toSet()

    /**
     * The [Channels][ChannelBehavior] specifically mentioned in this message.
     *
     * This collection can only contain values on crossposted messages.
     */
    val mentionedChannelBehaviors: Set<ChannelBehavior> get() = data.mentionedChannels.orEmpty().map { ChannelBehavior(Snowflake(it), kord) }.toSet()

    /**
     * The [Channels][Channel] specifically mentioned in this message.
     *
     * This property will only emit values on crossposted messages.
     */
    @Suppress("RemoveExplicitTypeArguments")
    val mentionedChannels: Flow<Channel>
        get() = flow<Channel> /*The plugin can infer the type, but the compiler can't, so leave this here for now*/ {
            for (id in mentionedChannelIds) {
                val channel = kord.getChannel(id)
                if (channel != null) emit(channel)
            }
        }

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
    val mentionedRoleBehaviors: Set<RoleBehavior>
        get() = data.mentionRoles.map { RoleBehavior(guildId = guildId!!, id = Snowflake(it), kord = kord) }.toSet()

    /**
     * The [roles][Role] mentioned in this message.
     */
    val mentionedRoles: Flow<Role>
        get() = flow {
            for (mentionRole in data.mentionRoles) {
                val role = kord.getRole(guildId!!, Snowflake(mentionRole)) ?: continue
                emit(role)
            }
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
     */
    val mentionedUsers: Flow<User>
        get() = flow {
            for (mentionUser in data.mentions) {
                val user = kord.getUser(Snowflake(mentionUser)) ?: continue
                emit(user)
            }
        }

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
    suspend fun getChannel(): MessageChannel = kord.getChannel(channelId) as MessageChannel

    /**
     * Requests to get the [author] as a member.
     *
     * Returns null if the message was not send in a [GuildMessageChannel], or if the [author] is not a [User].
     */
    suspend fun getAuthorAsMember(): Member? = data.guildId?.let { author?.asMember(Snowflake(it)) }

    /**
     * Requests to get the [Guild] this message was send in.
     *
     * Returns null if the message was not send in a [GuildMessageChannel].
     */
    suspend fun getGuild(): Guild? = guildId?.let { kord.getGuild(it) }
}
