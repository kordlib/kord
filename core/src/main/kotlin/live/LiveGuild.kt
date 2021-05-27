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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@KordPreview
fun Guild.live(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    parent: CoroutineScope = kord
): LiveGuild = LiveGuild(this, dispatcher, parent)

@KordPreview
inline fun Guild.live(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    block: LiveGuild.() -> Unit,
    parent: CoroutineScope = kord
) = this.live(dispatcher, parent).apply(block)

@KordPreview
fun LiveGuild.onEmojisUpdate(scope: CoroutineScope = this, block: suspend (EmojisUpdateEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
fun LiveGuild.onIntegrationsUpdate(scope: CoroutineScope = this, block: suspend (IntegrationsUpdateEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
fun LiveGuild.onBanAdd(scope: CoroutineScope = this, block: suspend (BanAddEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
fun LiveGuild.onBanRemove(scope: CoroutineScope = this, block: suspend (BanRemoveEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
fun LiveGuild.onPresenceUpdate(scope: CoroutineScope = this, block: suspend (PresenceUpdateEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
fun LiveGuild.onVoiceServerUpdate(scope: CoroutineScope = this, block: suspend (VoiceServerUpdateEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
fun LiveGuild.onVoiceStateUpdate(scope: CoroutineScope = this, block: suspend (VoiceStateUpdateEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
fun LiveGuild.onWebhookUpdate(scope: CoroutineScope = this, block: suspend (WebhookUpdateEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
fun LiveGuild.onRoleCreate(scope: CoroutineScope = this, block: suspend (RoleCreateEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
fun LiveGuild.onRoleUpdate(scope: CoroutineScope = this, block: suspend (RoleUpdateEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
fun LiveGuild.onRoleDelete(scope: CoroutineScope = this, block: suspend (RoleDeleteEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
fun LiveGuild.onMemberJoin(scope: CoroutineScope = this, block: suspend (MemberJoinEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
fun LiveGuild.onMemberUpdate(scope: CoroutineScope = this, block: suspend (MemberUpdateEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
fun LiveGuild.onMemberLeave(scope: CoroutineScope = this, block: suspend (MemberLeaveEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
fun LiveGuild.onReactionAdd(scope: CoroutineScope = this, block: suspend (ReactionAddEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
inline fun LiveGuild.onReactionAdd(
    reaction: ReactionEmoji,
    scope: CoroutineScope = this, crossinline block: suspend (ReactionAddEvent) -> Unit
) = on<ReactionAddEvent>(scope) {
    if (it.emoji == reaction) {
        block(it)
    }
}

@KordPreview
fun LiveGuild.onReactionRemove(scope: CoroutineScope = this, block: suspend (ReactionRemoveEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
inline fun LiveGuild.onReactionRemove(
    reaction: ReactionEmoji,
    scope: CoroutineScope = this, crossinline block: suspend (ReactionRemoveEvent) -> Unit
) = on<ReactionRemoveEvent>(scope) {
    if (it.emoji == reaction) {
        block(it)
    }
}

@KordPreview
fun LiveGuild.onReactionRemoveAll(scope: CoroutineScope = this, block: suspend (ReactionRemoveAllEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
fun LiveGuild.onMessageCreate(scope: CoroutineScope = this, block: suspend (MessageCreateEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
fun LiveGuild.onMessageUpdate(scope: CoroutineScope = this, block: suspend (MessageUpdateEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
fun LiveGuild.onMessageDelete(scope: CoroutineScope = this, block: suspend (MessageDeleteEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
fun LiveGuild.onChannelCreate(scope: CoroutineScope = this, block: suspend (ChannelCreateEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
fun LiveGuild.onChannelUpdate(scope: CoroutineScope = this, block: suspend (ChannelUpdateEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
fun LiveGuild.onChannelDelete(scope: CoroutineScope = this, block: suspend (ChannelDeleteEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
fun LiveGuild.onGuildCreate(scope: CoroutineScope = this, block: suspend (GuildCreateEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
fun LiveGuild.onGuildUpdate(scope: CoroutineScope = this, block: suspend (GuildUpdateEvent) -> Unit) =
    on(scope = scope, consumer = block)

@Deprecated(
    "The block is not called when the entity is deleted because the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.ERROR
)
@KordPreview
fun LiveGuild.onGuildDelete(scope: CoroutineScope = this, block: suspend (GuildDeleteEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
class LiveGuild(
    guild: Guild,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    parent: CoroutineScope = guild.kord
) : AbstractLiveKordEntity(guild.kord, dispatcher, parent), KordEntity {

    override val id: Snowflake
        get() = guild.id

    var guild: Guild = guild
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
