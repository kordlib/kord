package dev.kord.core.live

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.map
import dev.kord.core.entity.Guild
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.event.Event
import dev.kord.core.event.channel.ChannelCreateEvent
import dev.kord.core.event.channel.ChannelDeleteEvent
import dev.kord.core.event.channel.ChannelUpdateEvent
import dev.kord.core.event.guild.*
import dev.kord.core.event.message.*
import dev.kord.core.event.role.RoleCreateEvent
import dev.kord.core.event.role.RoleDeleteEvent
import dev.kord.core.event.role.RoleUpdateEvent
import dev.kord.core.event.user.PresenceUpdateEvent
import dev.kord.core.event.user.VoiceStateUpdateEvent
import dev.kord.core.live.exception.LiveCancellationException
import kotlinx.coroutines.*

/**
 * Returns a [LiveGuild] for a given [Guild].
 *
 * @param coroutineScope The [CoroutineScope] to create the [LiveGuild] with
 * @return the created [LiveGuild]
 */
@KordPreview
public fun Guild.live(
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job)
): LiveGuild = LiveGuild(this, coroutineScope)

/**
 * Returns a [LiveGuild] for a given [Guild] with configuration.
 *
 * @param coroutineScope The [CoroutineScope] to create the [LiveGuild] with
 * @param block The [LiveGuild] configuration
 * @return the created [LiveGuild]
 */
@KordPreview
public inline fun Guild.live(
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job),
    block: LiveGuild.() -> Unit
): LiveGuild = this.live(coroutineScope).apply(block)

/**
 * Invokes the consumer for this entity with [block] for the given [CoroutineScope]
 *
 * @param scope The [CoroutineScope] to invoke the consumer with
 * @param block The configuration for the consumer
 */
@KordPreview
public fun LiveGuild.onEmojisUpdate(scope: CoroutineScope = this, block: suspend (EmojisUpdateEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

/**
 * Invokes the consumer for this entity with [block] for the given [CoroutineScope]
 *
 * @param scope The [CoroutineScope] to invoke the consumer with
 * @param block The configuration for the consumer
 */
@KordPreview
public fun LiveGuild.onIntegrationsUpdate(
    scope: CoroutineScope = this,
    block: suspend (IntegrationsUpdateEvent) -> Unit
): Job =
    on(scope = scope, consumer = block)

/**
 * Invokes the consumer for this entity with [block] for the given [CoroutineScope]
 *
 * @param scope The [CoroutineScope] to invoke the consumer with
 * @param block The configuration for the consumer
 */
@KordPreview
public fun LiveGuild.onBanAdd(scope: CoroutineScope = this, block: suspend (BanAddEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

/**
 * Invokes the consumer for this entity with [block] for the given [CoroutineScope]
 *
 * @param scope The [CoroutineScope] to invoke the consumer with
 * @param block The configuration for the consumer
 */
@KordPreview
public fun LiveGuild.onBanRemove(scope: CoroutineScope = this, block: suspend (BanRemoveEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

/**
 * Invokes the consumer for this entity with [block] for the given [CoroutineScope]
 *
 * @param scope The [CoroutineScope] to invoke the consumer with
 * @param block The configuration for the consumer
 */
@KordPreview
public fun LiveGuild.onPresenceUpdate(scope: CoroutineScope = this, block: suspend (PresenceUpdateEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

/**
 * Invokes the consumer for this entity with [block] for the given [CoroutineScope]
 *
 * @param scope The [CoroutineScope] to invoke the consumer with
 * @param block The configuration for the consumer
 */
@KordPreview
public fun LiveGuild.onVoiceServerUpdate(
    scope: CoroutineScope = this,
    block: suspend (VoiceServerUpdateEvent) -> Unit
): Job =
    on(scope = scope, consumer = block)

/**
 * Invokes the consumer for this entity with [block] for the given [CoroutineScope]
 *
 * @param scope The [CoroutineScope] to invoke the consumer with
 * @param block The configuration for the consumer
 */
@KordPreview
public fun LiveGuild.onVoiceStateUpdate(
    scope: CoroutineScope = this,
    block: suspend (VoiceStateUpdateEvent) -> Unit
): Job =
    on(scope = scope, consumer = block)

/**
 * Invokes the consumer for this entity with [block] for the given [CoroutineScope]
 *
 * @param scope The [CoroutineScope] to invoke the consumer with
 * @param block The configuration for the consumer
 */
@KordPreview
public fun LiveGuild.onWebhookUpdate(scope: CoroutineScope = this, block: suspend (WebhookUpdateEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

/**
 * Invokes the consumer for this entity with [block] for the given [CoroutineScope]
 *
 * @param scope The [CoroutineScope] to invoke the consumer with
 * @param block The configuration for the consumer
 */
@KordPreview
public fun LiveGuild.onRoleCreate(scope: CoroutineScope = this, block: suspend (RoleCreateEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

/**
 * Invokes the consumer for this entity with [block] for the given [CoroutineScope]
 *
 * @param scope The [CoroutineScope] to invoke the consumer with
 * @param block The configuration for the consumer
 */
@KordPreview
public fun LiveGuild.onRoleUpdate(scope: CoroutineScope = this, block: suspend (RoleUpdateEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

/**
 * Invokes the consumer for this entity with [block] for the given [CoroutineScope]
 *
 * @param scope The [CoroutineScope] to invoke the consumer with
 * @param block The configuration for the consumer
 */
@KordPreview
public fun LiveGuild.onRoleDelete(scope: CoroutineScope = this, block: suspend (RoleDeleteEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

/**
 * Invokes the consumer for this entity with [block] for the given [CoroutineScope]
 *
 * @param scope The [CoroutineScope] to invoke the consumer with
 * @param block The configuration for the consumer
 */
@KordPreview
public fun LiveGuild.onMemberJoin(scope: CoroutineScope = this, block: suspend (MemberJoinEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

/**
 * Invokes the consumer for this entity with [block] for the given [CoroutineScope]
 *
 * @param scope The [CoroutineScope] to invoke the consumer with
 * @param block The configuration for the consumer
 */
@KordPreview
public fun LiveGuild.onMemberUpdate(scope: CoroutineScope = this, block: suspend (MemberUpdateEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

/**
 * Invokes the consumer for this entity with [block] for the given [CoroutineScope]
 *
 * @param scope The [CoroutineScope] to invoke the consumer with
 * @param block The configuration for the consumer
 */
@KordPreview
public fun LiveGuild.onMemberLeave(scope: CoroutineScope = this, block: suspend (MemberLeaveEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

/**
 * Invokes the consumer for this entity with [block] for the given [CoroutineScope]
 *
 * @param scope The [CoroutineScope] to invoke the consumer with
 * @param block The configuration for the consumer
 */
@KordPreview
public fun LiveGuild.onReactionAdd(scope: CoroutineScope = this, block: suspend (ReactionAddEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

/**
 * Invokes the consumer for this entity with [block] for the given [CoroutineScope]
 *
 * @param reaction The [ReactionEmoji] that was added
 * @param scope The [CoroutineScope] to invoke the consumer with
 * @param block The configuration for the consumer
 */
@KordPreview
public inline fun LiveGuild.onReactionAdd(
    reaction: ReactionEmoji,
    scope: CoroutineScope = this, crossinline block: suspend (ReactionAddEvent) -> Unit
): Job = on<ReactionAddEvent>(scope) {
    if (it.emoji == reaction) {
        block(it)
    }
}

/**
 * Invokes the consumer for this entity with [block] for the given [CoroutineScope]
 *
 * @param scope The [CoroutineScope] to invoke the consumer with
 * @param block The configuration for the consumer
 */
@KordPreview
public fun LiveGuild.onReactionRemove(scope: CoroutineScope = this, block: suspend (ReactionRemoveEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

/**
 * Invokes the consumer for this entity with [block] for the given [CoroutineScope]
 *
 * @param reaction The [ReactionEmoji] that was removed
 * @param scope The [CoroutineScope] to invoke the consumer with
 * @param block The configuration for the consumer
 */
@KordPreview
public inline fun LiveGuild.onReactionRemove(
    reaction: ReactionEmoji,
    scope: CoroutineScope = this, crossinline block: suspend (ReactionRemoveEvent) -> Unit
): Job = on<ReactionRemoveEvent>(scope) {
    if (it.emoji == reaction) {
        block(it)
    }
}

/**
 * Invokes the consumer for this entity with [block] for the given [CoroutineScope]
 *
 * @param scope The [CoroutineScope] to invoke the consumer with
 * @param block The configuration for the consumer
 */
@KordPreview
public fun LiveGuild.onReactionRemoveAll(
    scope: CoroutineScope = this,
    block: suspend (ReactionRemoveAllEvent) -> Unit
): Job =
    on(scope = scope, consumer = block)

/**
 * Invokes the consumer for this entity with [block] for the given [CoroutineScope]
 *
 * @param scope The [CoroutineScope] to invoke the consumer with
 * @param block The configuration for the consumer
 */
@KordPreview
public fun LiveGuild.onMessageCreate(scope: CoroutineScope = this, block: suspend (MessageCreateEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

/**
 * Invokes the consumer for this entity with [block] for the given [CoroutineScope]
 *
 * @param scope The [CoroutineScope] to invoke the consumer with
 * @param block The configuration for the consumer
 */
@KordPreview
public fun LiveGuild.onMessageUpdate(scope: CoroutineScope = this, block: suspend (MessageUpdateEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

/**
 * Invokes the consumer for this entity with [block] for the given [CoroutineScope]
 *
 * @param scope The [CoroutineScope] to invoke the consumer with
 * @param block The configuration for the consumer
 */
@KordPreview
public fun LiveGuild.onMessageDelete(scope: CoroutineScope = this, block: suspend (MessageDeleteEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

/**
 * Invokes the consumer for this entity with [block] for the given [CoroutineScope]
 *
 * @param scope The [CoroutineScope] to invoke the consumer with
 * @param block The configuration for the consumer
 */
@KordPreview
public fun LiveGuild.onChannelCreate(scope: CoroutineScope = this, block: suspend (ChannelCreateEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

/**
 * Invokes the consumer for this entity with [block] for the given [CoroutineScope]
 *
 * @param scope The [CoroutineScope] to invoke the consumer with
 * @param block The configuration for the consumer
 */
@KordPreview
public fun LiveGuild.onChannelUpdate(scope: CoroutineScope = this, block: suspend (ChannelUpdateEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

/**
 * Invokes the consumer for this entity with [block] for the given [CoroutineScope]
 *
 * @param scope The [CoroutineScope] to invoke the consumer with
 * @param block The configuration for the consumer
 */
@KordPreview
public fun LiveGuild.onChannelDelete(scope: CoroutineScope = this, block: suspend (ChannelDeleteEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

/**
 * Invokes the consumer for this entity with [block] for the given [CoroutineScope]
 *
 * @param scope The [CoroutineScope] to invoke the consumer with
 * @param block The configuration for the consumer
 */
@KordPreview
public fun LiveGuild.onGuildCreate(scope: CoroutineScope = this, block: suspend (GuildCreateEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

/**
 * Invokes the consumer for this entity with [block] for the given [CoroutineScope]
 *
 * @param scope The [CoroutineScope] to invoke the consumer with
 * @param block The configuration for the consumer
 */
@KordPreview
public fun LiveGuild.onGuildUpdate(scope: CoroutineScope = this, block: suspend (GuildUpdateEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

/**
 * A [AbstractLiveKordEntity] for a [Guild]
 *
 * @property guild The [Guild] to get the live entity for
 * @property coroutineContext The [CoroutineScope] to create the live object with
 */
@KordPreview
public class LiveGuild(
    guild: Guild,
    coroutineScope: CoroutineScope = guild.kord + SupervisorJob(guild.kord.coroutineContext.job)
) : AbstractLiveKordEntity(guild.kord, coroutineScope), KordEntity {

    override val id: Snowflake
        get() = guild.id

    /**
     * The [Guild] to create the live entity for
     */
    public var guild: Guild = guild
        private set

    override fun filter(event: Event): Boolean = when (event) {
        is EmojisUpdateEvent -> event.guildId == guild.id

        is IntegrationsUpdateEvent -> event.guildId == guild.id

        is BanAddEvent -> event.guildId == guild.id
        is BanRemoveEvent -> event.guildId == guild.id

        is PresenceUpdateEvent -> event.guildId == guild.id

        is VoiceServerUpdateEvent -> event.guildId == guild.id
        is VoiceStateUpdateEvent -> event.state.guildId == guild.id

        is WebhookUpdateEvent -> event.guildId == guild.id

        is RoleCreateEvent -> event.guildId == guild.id
        is RoleUpdateEvent -> event.guildId == guild.id
        is RoleDeleteEvent -> event.guildId == guild.id

        is MemberJoinEvent -> event.guildId == guild.id
        is MemberUpdateEvent -> event.guildId == guild.id
        is MemberLeaveEvent -> event.guildId == guild.id

        is ReactionAddEvent -> event.guildId == guild.id
        is ReactionRemoveEvent -> event.guildId == guild.id
        is ReactionRemoveAllEvent -> event.guildId == guild.id

        is MessageCreateEvent -> event.guildId == guild.id
        is MessageUpdateEvent -> event.new.guildId.value == guild.id
        is MessageDeleteEvent -> event.guildId == guild.id

        is ChannelCreateEvent -> event.channel.data.guildId.value == guild.id
        is ChannelUpdateEvent -> event.channel.data.guildId.value == guild.id
        is ChannelDeleteEvent -> event.channel.data.guildId.value == guild.id

        is GuildCreateEvent -> event.guild.id == guild.id
        is GuildUpdateEvent -> event.guild.id == guild.id
        is GuildDeleteEvent -> event.guildId == guild.id

        else -> false
    }

    override fun update(event: Event): Unit = when (event) {
        is EmojisUpdateEvent -> guild = Guild(guild.data.copy(emojis = event.emojis.map { it.id }), kord)

        is RoleCreateEvent -> guild = Guild(
            guild.data.copy(
                roles = guild.data.roles + event.guildId
            ), kord
        )

        is RoleDeleteEvent -> guild = Guild(
            guild.data.copy(
                roles = guild.data.roles - event.guildId
            ), kord
        )

        is MemberJoinEvent -> guild = Guild(
            guild.data.copy(
                memberCount = guild.data.memberCount.map { it + 1 },
            ), kord
        )

        is MemberLeaveEvent -> guild = Guild(guild.data.copy(
            memberCount = guild.data.memberCount.map { it - 1 }
        ), kord)

        is ChannelCreateEvent -> guild = Guild(guild.data.copy(
            channels = guild.data.channels.map { it + event.channel.id }
        ), kord)

        is ChannelDeleteEvent -> guild = Guild(guild.data.copy(
            channels = guild.data.channels.map { it - event.channel.id }
        ), kord)

        is GuildUpdateEvent -> guild = event.guild
        is GuildDeleteEvent -> shutDown(LiveCancellationException(event, "The guild is deleted"))
        else -> Unit
    }

}
