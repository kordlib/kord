package dev.kord.core.entity

import cache.data.MessageInteractionData
import dev.kord.common.entity.InteractionType
import dev.kord.common.entity.MessageFlags
import dev.kord.common.entity.MessageType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.mapNullable
import dev.kord.common.entity.optional.orEmpty
import dev.kord.common.entity.optional.unwrap
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.MessageBehavior
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.behavior.channel.ChannelBehavior
import dev.kord.core.behavior.interaction.response.InteractionResponseBehavior
import dev.kord.core.cache.data.MessageData
import dev.kord.core.entity.application.ApplicationCommand
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.GuildChannel
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.entity.channel.TopGuildMessageChannel
import dev.kord.core.entity.component.ActionRowComponent
import dev.kord.core.entity.component.Component
import dev.kord.core.entity.interaction.ActionInteraction
import dev.kord.core.entity.interaction.followup.FollowupMessage
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Instant
import java.util.Objects

/**
 * An instance of a [Discord Message][https://discord.com/developers/docs/resources/channel#message-object].
 */
public class Message(
    public val data: MessageData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : MessageBehavior {

    /**
     * An instance of [MessageInteraction](https://discord.com/developers/docs/interactions/receiving-and-responding#message-interaction-object)
     *
     * This is sent on the [Message] object when the message is a response to an [ActionInteraction].
     */
    public class Interaction(
        public val data: MessageInteractionData,
        override val kord: Kord,
        override val supplier: EntitySupplier = kord.defaultSupplier
    ) : KordEntity, Strategizable {

        /** [Id][ActionInteraction.id] of the [ActionInteraction] this message is responding to. */
        override val id: Snowflake get() = data.id

        /** The [name][ApplicationCommand.name] of the [ApplicationCommand] that triggered this message. */
        public val name: String get() = data.name

        /** The [UserBehavior] of the [user][ActionInteraction.user] who invoked the [ActionInteraction]. */
        public val user: UserBehavior get() = UserBehavior(data.user, kord)

        /** The [InteractionType] of the interaction [Interaction]. */
        public val type: InteractionType get() = data.type

        /**
         * Requests the [User] of this interaction message.
         *
         * @throws RequestException if something went wrong while retrieving the user.
         * @throws EntityNotFoundException if the user was null.
         */
        public suspend fun getUser(): User = supplier.getUser(user.id)

        /**
         * Requests to get the user of this interaction message,
         * returns null if the [User] isn't present.
         *
         * @throws [RequestException] if anything went wrong during the request.
         */
        public suspend fun getUserOrNull(): User? = supplier.getUserOrNull(user.id)

        override fun withStrategy(strategy: EntitySupplyStrategy<*>): Interaction =
            Interaction(data, kord, strategy.supply(kord))
    }


    /**
     * The id of this message.
     */
    override val id: Snowflake
        get() = data.id

    /**
     * The id of the [MessageChannel] this message was send in.
     */
    override val channelId: Snowflake
        get() = data.channelId

    override suspend fun asMessageOrNull(): Message = this

    /**
     * The files attached to this message.
     */
    public val attachments: Set<Attachment> get() = data.attachments.asSequence().map { Attachment(it, kord) }.toSet()

    /**
     * The author of this message, if it was created by a [User].
     *
     * Returns null if the author is not a Discord account, like a [Webhook] or systems message. This also applies to
     * [interaction responses][InteractionResponseBehavior] and [followup messages][FollowupMessage] since
     * [they are webhooks under the hood](https://discord.com/developers/docs/interactions/receiving-and-responding#responding-to-an-interaction).
     */
    public val author: User?
        get() = if (data.webhookId.value == data.author.id) null
        else User(data.author, kord)

    /**
     * The content of this message.
     */
    public val content: String get() = data.content

    /**
     * The instant when this message was last edited, if ever.
     *
     * Returns null if the message was never edited.
     */
    public val editedTimestamp: Instant? get() = data.editedTimestamp

    /**
     * The embedded content of this message.
     *
     * This includes automatically embedded [videos][Embed.video] and [urls][Embed.Provider].
     */
    public val embeds: List<Embed> get() = data.embeds.map { Embed(it, kord) }

    /**
     * The ids of [Channels][Channel] specifically mentioned in this message.
     *
     * This collection can only contain values on crossposted messages, channels
     * mentioned inside the same guild will not be present.
     */
    public val mentionedChannelIds: Set<Snowflake> get() = data.mentionedChannels.orEmpty().map { it }.toSet()

    /**
     * The [Channels][ChannelBehavior] specifically mentioned in this message.
     *
     * This collection can only contain values on crossposted messages, channels
     * mentioned inside the same guild will not be present.
     */
    public val mentionedChannelBehaviors: Set<ChannelBehavior>
        get() = data.mentionedChannels.orEmpty().map { ChannelBehavior(it, kord) }.toSet()

    /**
     * The stickers sent with this message.
     */
    public val stickers: List<StickerItem> get() = data.stickers.orEmpty().map { StickerItem(it, kord) }

    /**
     * If the message is a response to an [ActionInteraction], this is the id of the interaction's application
     */
    public val applicationId: Snowflake? get() = data.application.unwrap { it.id }

    /**
     * The message being replied to.
     *
     * Absence of this field does **not** mean this message was not a reply. The referenced message
     * may not be available (through deletion or other means).
     * Compare [type] to [MessageType.Reply] for a consistent way of identifying replies.
     */
    public val referencedMessage: Message? get() = data.referencedMessage.value?.let { Message(it, kord) }

    /**
     * reference data sent with crossposted messages and replies.
     *
     * This field is only returned for messages with [MessageType.Reply].
     * If the message is a reply but the [referencedMessage] field is not present,
     * the backend did not attempt to fetch the [Message] that was being replied to,
     * so its state is unknown.
     * If the field exists but is null, the referenced message was deleted.
     */
    public val messageReference: MessageReference? get() = data.messageReference.value?.let { MessageReference(it, kord) }

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
    public val mentionedChannels: Flow<Channel>
        get() = mentionedChannelIds.asFlow().map { supplier.getChannel(it) }

    /**
     * True if this message mentions `@everyone`.
     */
    public val mentionsEveryone: Boolean get() = data.mentionEveryone

    /**
     * The [ids][Role.id] of roles mentioned in this message.
     */
    public val mentionedRoleIds: Set<Snowflake> get() = data.mentionRoles.map { it }.toSet()

    /**
     * The [roles][Role] mentioned in this message.
     *
     * This request uses state [data] to resolve the entities belonging to the flow,
     * as such it can't guarantee an up to date representation if the [data] is outdated.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val mentionedRoles: Flow<Role>
        get() = flow {
            if (mentionedRoleIds.isEmpty()) return@flow

            val guild = getGuild()
            supplier.getGuildRoles(guild.id).filter { it.id in mentionedRoleIds }
        }

    /**
     * The [ids][User.id] of users mentioned in this message.
     */
    public val mentionedUserIds: Set<Snowflake> get() = data.mentions.map { it }.toSet()

    /**
     * The [Behaviors][UserBehavior] of users mentioned in this message.
     */
    public val mentionedUserBehaviors: Set<UserBehavior> get() = data.mentions.map { UserBehavior(it, kord) }.toSet()

    /**
     * The [Message.Interaction] sent on this message object when it is a response to an [ActionInteraction].
     */
    public val interaction: Interaction? get() = data.interaction.mapNullable { Interaction(it, kord) }.value

    /**
     * The [users][User] mentioned in this message.
     *
     * This request uses state [data] to resolve the entities belonging to the flow,
     * as such it can't guarantee an up to date representation if the [data] is outdated.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val mentionedUsers: Flow<User>
        get() = data.mentions.asFlow().map { supplier.getUser(it) }

    /**
     * Whether the message was pinned in its [channel].
     */
    public val isPinned: Boolean get() = data.pinned

    /**
     * The reactions to this message.
     */
    public val reactions: Set<Reaction> get() = data.reactions.orEmpty().asSequence().map { Reaction(it, kord) }.toSet()

    /**
     * The instant when this message was created.
     */
    public val timestamp: Instant get() = data.timestamp

    /**
     * Whether this message was send using `\tts`.
     */
    public val tts: Boolean get() = data.tts

    /**
     * The type of this message.
     */
    public val type: MessageType get() = data.type

    /** The flags of this message. */
    public val flags: MessageFlags? get() = data.flags.value

    /**
     * The [id][Webhook.id] of the [Webhook] that was used to send this message.
     *
     * Returns null if this message was not send using a webhook.
     */
    public val webhookId: Snowflake? get() = data.webhookId.value

    @Deprecated("Replaced with 'actionRows'.", ReplaceWith("this.actionRows"))
    public val components: List<Component>
        get() = data.components.orEmpty().map { Component(it) }

    /** The [ActionRowComponent]s of this message. */
    public val actionRows: List<ActionRowComponent>
        get() = data.components.orEmpty().map { ActionRowComponent(it) }

    /**
     * Returns itself.
     */
    override suspend fun asMessage(): Message = this

    /**
     * Requests to get the [author] as a member.
     *
     * Returns null if the message was not send in a [TopGuildMessageChannel], or if the [author] is not a [User].
     */
    public suspend fun getAuthorAsMember(): Member? {
        val author = author ?: return null
        val guildId = getGuildOrNull()?.id ?: return null
        return author.asMember(guildId)
    }

    /**
     * Requests to get the guild of this message.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [Guild] wasn't present.
     * @throws [ClassCastException] if this message wasn't made in a guild.
     */
    public suspend fun getGuild(): Guild = supplier.getChannelOf<GuildChannel>(channelId).getGuild()

    /**
     * Requests to get the guild of this message,
     * returns null if the [Guild] isn't present or this message wasn't made in a guild.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getGuildOrNull(): Guild? = supplier.getChannelOfOrNull<GuildChannel>(channelId)?.getGuildOrNull()

    /**
     * Returns a new [Message] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Message = Message(data, kord, strategy.supply(kord))

    override fun hashCode(): Int = Objects.hash(id)

    override fun equals(other: Any?): Boolean = when (other) {
        is MessageBehavior -> other.id == id && other.channelId == channelId
        else -> false
    }

    override fun toString(): String {
        return "Message(data=$data, kord=$kord, supplier=$supplier)"
    }

}
