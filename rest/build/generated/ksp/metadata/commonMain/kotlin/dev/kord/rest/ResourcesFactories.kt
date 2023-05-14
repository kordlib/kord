@file:Suppress(names = arrayOf("NOTHING_TO_INLINE"))

package dev.kord.rest

import dev.kord.common.entity.Snowflake
import dev.kord.rest.route.Resources

public inline fun Resources.Users.Companion.Me(): Resources.Users.Me =
    Resources.Users.Me(Resources.Users())

public inline fun Resources.Users.Me.Companion.Guilds(): Resources.Users.Me.Guilds =
    Resources.Users.Me.Guilds(Resources.Users.Me())

public inline fun Resources.Users.Me.Guilds.Companion.ById(guildId: Snowflake):
    Resources.Users.Me.Guilds.ById = Resources.Users.Me.Guilds.ById(guildId,
    Resources.Users.Me.Guilds())

public inline fun Resources.Users.Me.Companion.Channels(): Resources.Users.Me.Channels =
    Resources.Users.Me.Channels(Resources.Users.Me())

public inline fun Resources.Users.Me.Companion.Connections(): Resources.Users.Me.Connections =
    Resources.Users.Me.Connections(Resources.Users.Me())

public inline fun Resources.Users.Companion.ById(userId: Snowflake): Resources.Users.ById =
    Resources.Users.ById(userId, Resources.Users())

public inline fun Resources.Guilds.Companion.ById(guildId: Snowflake): Resources.Guilds.ById =
    Resources.Guilds.ById(guildId, Resources.Guilds())

public inline fun Resources.Guilds.ById.Companion.AuditLog(guildId: Snowflake):
    Resources.Guilds.ById.AuditLog = Resources.Guilds.ById.AuditLog(Resources.Guilds.ById(guildId))

public inline fun Resources.Guilds.ById.Companion.MFA(guildId: Snowflake): Resources.Guilds.ById.MFA
    = Resources.Guilds.ById.MFA(Resources.Guilds.ById(guildId))

public inline fun Resources.Guilds.ById.Companion.Prune(guildId: Snowflake):
    Resources.Guilds.ById.Prune = Resources.Guilds.ById.Prune(Resources.Guilds.ById(guildId))

public inline fun Resources.Guilds.ById.Companion.Regions(guildId: Snowflake):
    Resources.Guilds.ById.Regions = Resources.Guilds.ById.Regions(Resources.Guilds.ById(guildId))

public inline fun Resources.Guilds.ById.Companion.Widget(guildId: Snowflake):
    Resources.Guilds.ById.Widget = Resources.Guilds.ById.Widget(Resources.Guilds.ById(guildId))

public inline fun Resources.Guilds.ById.Companion.VanityUrl(guildId: Snowflake):
    Resources.Guilds.ById.VanityUrl =
    Resources.Guilds.ById.VanityUrl(Resources.Guilds.ById(guildId))

public inline fun Resources.Guilds.ById.Companion.WelcomeScreen(guildId: Snowflake):
    Resources.Guilds.ById.WelcomeScreen =
    Resources.Guilds.ById.WelcomeScreen(Resources.Guilds.ById(guildId))

public inline fun Resources.Guilds.ById.Companion.Preview(guildId: Snowflake):
    Resources.Guilds.ById.Preview = Resources.Guilds.ById.Preview(Resources.Guilds.ById(guildId))

public inline fun Resources.Guilds.ById.Companion.Members(guildId: Snowflake):
    Resources.Guilds.ById.Members = Resources.Guilds.ById.Members(Resources.Guilds.ById(guildId))

public inline fun Resources.Guilds.ById.Members.Companion.ById(guildId: Snowflake,
    userId: Snowflake): Resources.Guilds.ById.Members.ById =
    Resources.Guilds.ById.Members.ById(userId, Resources.Guilds.ById.Members(guildId))

public inline fun Resources.Guilds.ById.Members.ById.Companion.Roles(guildId: Snowflake,
    userId: Snowflake): Resources.Guilds.ById.Members.ById.Roles =
    Resources.Guilds.ById.Members.ById.Roles(Resources.Guilds.ById.Members.ById(guildId, userId))

public inline fun Resources.Guilds.ById.Members.ById.Roles.Companion.ById(
  guildId: Snowflake,
  userId: Snowflake,
  roleId: Snowflake,
): Resources.Guilds.ById.Members.ById.Roles.ById =
    Resources.Guilds.ById.Members.ById.Roles.ById(roleId,
    Resources.Guilds.ById.Members.ById.Roles(guildId, userId))

public inline fun Resources.Guilds.ById.Members.Companion.Search(guildId: Snowflake):
    Resources.Guilds.ById.Members.Search =
    Resources.Guilds.ById.Members.Search(Resources.Guilds.ById.Members(guildId))

public inline fun Resources.Guilds.ById.Members.Companion.Me(guildId: Snowflake):
    Resources.Guilds.ById.Members.Me =
    Resources.Guilds.ById.Members.Me(Resources.Guilds.ById.Members(guildId))

public inline fun Resources.Guilds.ById.Members.Me.Companion.Nick(guildId: Snowflake):
    Resources.Guilds.ById.Members.Me.Nick =
    Resources.Guilds.ById.Members.Me.Nick(Resources.Guilds.ById.Members.Me(guildId))

public inline fun Resources.Guilds.ById.Companion.ScheduledEvents(guildId: Snowflake):
    Resources.Guilds.ById.ScheduledEvents =
    Resources.Guilds.ById.ScheduledEvents(Resources.Guilds.ById(guildId))

public inline fun Resources.Guilds.ById.ScheduledEvents.Companion.ById(guildId: Snowflake,
    scheduledEventId: Snowflake): Resources.Guilds.ById.ScheduledEvents.ById =
    Resources.Guilds.ById.ScheduledEvents.ById(scheduledEventId,
    Resources.Guilds.ById.ScheduledEvents(guildId))

public inline fun Resources.Guilds.ById.ScheduledEvents.ById.Companion.Users(guildId: Snowflake,
    scheduledEventId: Snowflake): Resources.Guilds.ById.ScheduledEvents.ById.Users =
    Resources.Guilds.ById.ScheduledEvents.ById.Users(Resources.Guilds.ById.ScheduledEvents.ById(guildId,
    scheduledEventId))

public inline fun Resources.Guilds.ById.Companion.Stickers(guildId: Snowflake):
    Resources.Guilds.ById.Stickers = Resources.Guilds.ById.Stickers(Resources.Guilds.ById(guildId))

public inline fun Resources.Guilds.ById.Stickers.Companion.ById(guildId: Snowflake,
    stickerId: Snowflake): Resources.Guilds.ById.Stickers.ById =
    Resources.Guilds.ById.Stickers.ById(stickerId, Resources.Guilds.ById.Stickers(guildId))

public inline fun Resources.Guilds.ById.Companion.Bans(guildId: Snowflake):
    Resources.Guilds.ById.Bans = Resources.Guilds.ById.Bans(Resources.Guilds.ById(guildId))

public inline fun Resources.Guilds.ById.Bans.Companion.ById(guildId: Snowflake, userId: Snowflake):
    Resources.Guilds.ById.Bans.ById = Resources.Guilds.ById.Bans.ById(userId,
    Resources.Guilds.ById.Bans(guildId))

public inline fun Resources.Guilds.ById.Companion.Channels(guildId: Snowflake,
    scheduledEventId: Snowflake): Resources.Guilds.ById.Channels =
    Resources.Guilds.ById.Channels(Resources.Guilds.ById.ScheduledEvents.ById(guildId,
    scheduledEventId))

public inline fun Resources.Guilds.ById.Companion.Threads(guildId: Snowflake):
    Resources.Guilds.ById.Threads = Resources.Guilds.ById.Threads(Resources.Guilds.ById(guildId))

public inline fun Resources.Guilds.ById.Threads.Companion.Active(guildId: Snowflake):
    Resources.Guilds.ById.Threads.Active =
    Resources.Guilds.ById.Threads.Active(Resources.Guilds.ById.Threads(guildId))

public inline fun Resources.Guilds.ById.Companion.Webhooks(guildId: Snowflake):
    Resources.Guilds.ById.Webhooks = Resources.Guilds.ById.Webhooks(Resources.Guilds.ById(guildId))

public inline fun Resources.Guilds.ById.Companion.Templates(guildId: Snowflake):
    Resources.Guilds.ById.Templates =
    Resources.Guilds.ById.Templates(Resources.Guilds.ById(guildId))

public inline fun Resources.Guilds.ById.Templates.Companion.ById(guildId: Snowflake,
    templateCode: String): Resources.Guilds.ById.Templates.ById =
    Resources.Guilds.ById.Templates.ById(templateCode, Resources.Guilds.ById.Templates(guildId))

public inline fun Resources.Guilds.ById.Companion.Invites(guildId: Snowflake):
    Resources.Guilds.ById.Invites = Resources.Guilds.ById.Invites(Resources.Guilds.ById(guildId))

public inline fun Resources.Guilds.ById.Invites.Companion.ById(guildId: Snowflake,
    inviteId: Snowflake): Resources.Guilds.ById.Invites.ById =
    Resources.Guilds.ById.Invites.ById(inviteId, Resources.Guilds.ById.Invites(guildId))

public inline fun Resources.Guilds.ById.Companion.Integrations(guildId: Snowflake):
    Resources.Guilds.ById.Integrations =
    Resources.Guilds.ById.Integrations(Resources.Guilds.ById(guildId))

public inline fun Resources.Guilds.ById.Integrations.Companion.ById(guildId: Snowflake,
    integrationId: Snowflake): Resources.Guilds.ById.Integrations.ById =
    Resources.Guilds.ById.Integrations.ById(integrationId,
    Resources.Guilds.ById.Integrations(guildId))

public inline fun Resources.Guilds.ById.Integrations.ById.Companion.Sync(guildId: Snowflake,
    integrationId: Snowflake): Resources.Guilds.ById.Integrations.ById.Sync =
    Resources.Guilds.ById.Integrations.ById.Sync(Resources.Guilds.ById.Integrations.ById(guildId,
    integrationId))

public inline fun Resources.Guilds.ById.Companion.VoiceStates(guildId: Snowflake):
    Resources.Guilds.ById.VoiceStates =
    Resources.Guilds.ById.VoiceStates(Resources.Guilds.ById(guildId))

public inline fun Resources.Guilds.ById.VoiceStates.Companion.ById(guildId: Snowflake,
    id: Snowflake): Resources.Guilds.ById.VoiceStates.ById =
    Resources.Guilds.ById.VoiceStates.ById(id, Resources.Guilds.ById.VoiceStates(guildId))

public inline fun Resources.Guilds.ById.Companion.Emojis(guildId: Snowflake):
    Resources.Guilds.ById.Emojis = Resources.Guilds.ById.Emojis(Resources.Guilds.ById(guildId))

public inline fun Resources.Guilds.ById.Companion.AutoModeration(guildId: Snowflake):
    Resources.Guilds.ById.AutoModeration =
    Resources.Guilds.ById.AutoModeration(Resources.Guilds.ById(guildId))

public inline fun Resources.Guilds.ById.AutoModeration.Companion.Rules(guildId: Snowflake):
    Resources.Guilds.ById.AutoModeration.Rules =
    Resources.Guilds.ById.AutoModeration.Rules(Resources.Guilds.ById.AutoModeration(guildId))

public inline fun Resources.Channels.Companion.ById(channelId: Snowflake): Resources.Channels.ById =
    Resources.Channels.ById(channelId, Resources.Channels())

public inline fun Resources.Channels.ById.Companion.Typing(channelId: Snowflake):
    Resources.Channels.ById.Typing =
    Resources.Channels.ById.Typing(Resources.Channels.ById(channelId))

public inline fun Resources.Channels.ById.Companion.Recipients(channelId: Snowflake):
    Resources.Channels.ById.Recipients =
    Resources.Channels.ById.Recipients(Resources.Channels.ById(channelId))

public inline fun Resources.Channels.ById.Recipients.Companion.ById(channelId: Snowflake,
    userId: Snowflake): Resources.Channels.ById.Recipients.ById =
    Resources.Channels.ById.Recipients.ById(userId, Resources.Channels.ById.Recipients(channelId))

public inline fun Resources.Channels.ById.Companion.Threads(channelId: Snowflake):
    Resources.Channels.ById.Threads =
    Resources.Channels.ById.Threads(Resources.Channels.ById(channelId))

public inline fun Resources.Channels.ById.Threads.Companion.Private(channelId: Snowflake):
    Resources.Channels.ById.Threads.Private =
    Resources.Channels.ById.Threads.Private(Resources.Channels.ById.Threads(channelId))

public inline fun Resources.Channels.ById.Threads.Companion.Archived(channelId: Snowflake):
    Resources.Channels.ById.Threads.Archived =
    Resources.Channels.ById.Threads.Archived(Resources.Channels.ById.Threads(channelId))

public inline fun Resources.Channels.ById.Threads.Archived.Companion.Private(channelId: Snowflake):
    Resources.Channels.ById.Threads.Archived.Private =
    Resources.Channels.ById.Threads.Archived.Private(Resources.Channels.ById.Threads.Archived(channelId))

public inline fun Resources.Channels.ById.Threads.Archived.Companion.Public(channelId: Snowflake):
    Resources.Channels.ById.Threads.Archived.Public =
    Resources.Channels.ById.Threads.Archived.Public(Resources.Channels.ById.Threads.Archived(channelId))

public inline fun Resources.Channels.ById.Companion.ThreadMembers(channelId: Snowflake):
    Resources.Channels.ById.ThreadMembers =
    Resources.Channels.ById.ThreadMembers(Resources.Channels.ById(channelId))

public inline fun Resources.Channels.ById.ThreadMembers.Companion.Me(channelId: Snowflake):
    Resources.Channels.ById.ThreadMembers.Me =
    Resources.Channels.ById.ThreadMembers.Me(Resources.Channels.ById.ThreadMembers(channelId))

public inline fun Resources.Channels.ById.ThreadMembers.Companion.ById(channelId: Snowflake,
    userId: Snowflake): Resources.Channels.ById.ThreadMembers.ById =
    Resources.Channels.ById.ThreadMembers.ById(userId,
    Resources.Channels.ById.ThreadMembers(channelId))

public inline fun Resources.Channels.ById.Companion.Invites(channelId: Snowflake):
    Resources.Channels.ById.Invites =
    Resources.Channels.ById.Invites(Resources.Channels.ById(channelId))

public inline fun Resources.Channels.ById.Companion.Pins(channelId: Snowflake):
    Resources.Channels.ById.Pins = Resources.Channels.ById.Pins(Resources.Channels.ById(channelId))

public inline fun Resources.Channels.ById.Pins.Companion.ById(channelId: Snowflake,
    messageId: Snowflake): Resources.Channels.ById.Pins.ById =
    Resources.Channels.ById.Pins.ById(messageId, Resources.Channels.ById.Pins(channelId))

public inline fun Resources.Channels.ById.Companion.Messages(channelId: Snowflake):
    Resources.Channels.ById.Messages =
    Resources.Channels.ById.Messages(Resources.Channels.ById(channelId))

public inline fun Resources.Channels.ById.Messages.Companion.BulkDelete(channelId: Snowflake):
    Resources.Channels.ById.Messages.BulkDelete =
    Resources.Channels.ById.Messages.BulkDelete(Resources.Channels.ById.Messages(channelId))

public inline fun Resources.Channels.ById.Messages.Companion.ById(channelId: Snowflake,
    messageId: Snowflake): Resources.Channels.ById.Messages.ById =
    Resources.Channels.ById.Messages.ById(messageId, Resources.Channels.ById.Messages(channelId))

public inline fun Resources.Channels.ById.Messages.ById.Companion.Threads(channelId: Snowflake):
    Resources.Channels.ById.Messages.ById.Threads =
    Resources.Channels.ById.Messages.ById.Threads(Resources.Channels.ById(channelId))

public inline fun Resources.Channels.ById.Messages.ById.Companion.CrossPost(channelId: Snowflake):
    Resources.Channels.ById.Messages.ById.CrossPost =
    Resources.Channels.ById.Messages.ById.CrossPost(Resources.Channels.ById(channelId))

public inline fun Resources.Channels.ById.Messages.ById.Companion.Reactions(channelId: Snowflake,
    messageId: Snowflake): Resources.Channels.ById.Messages.ById.Reactions =
    Resources.Channels.ById.Messages.ById.Reactions(Resources.Channels.ById.Messages.ById(channelId,
    messageId))

public inline fun Resources.Channels.ById.Messages.ById.Reactions.Companion.ById(
  channelId: Snowflake,
  messageId: Snowflake,
  emojiId: Snowflake,
): Resources.Channels.ById.Messages.ById.Reactions.ById =
    Resources.Channels.ById.Messages.ById.Reactions.ById(emojiId,
    Resources.Channels.ById.Messages.ById.Reactions(channelId, messageId))

public inline fun Resources.Channels.ById.Messages.ById.Reactions.ById.Companion.Me(
  channelId: Snowflake,
  messageId: Snowflake,
  emojiId: Snowflake,
): Resources.Channels.ById.Messages.ById.Reactions.ById.Me =
    Resources.Channels.ById.Messages.ById.Reactions.ById.Me(Resources.Channels.ById.Messages.ById.Reactions.ById(channelId,
    messageId, emojiId))

public inline fun Resources.Channels.ById.Messages.ById.Reactions.ById.Companion.ReactorById(
  channelId: Snowflake,
  messageId: Snowflake,
  emojiId: Snowflake,
  userId: Snowflake,
): Resources.Channels.ById.Messages.ById.Reactions.ById.ReactorById =
    Resources.Channels.ById.Messages.ById.Reactions.ById.ReactorById(userId,
    Resources.Channels.ById.Messages.ById.Reactions.ById(channelId, messageId, emojiId))

public inline fun Resources.Channels.ById.Companion.Permissions(channelId: Snowflake):
    Resources.Channels.ById.Permissions =
    Resources.Channels.ById.Permissions(Resources.Channels.ById.Messages(channelId))

public inline fun Resources.Channels.ById.Companion.Webhooks(channelId: Snowflake):
    Resources.Channels.ById.Webhooks =
    Resources.Channels.ById.Webhooks(Resources.Channels.ById(channelId))

public inline fun Resources.Invites.Companion.ById(inviteCode: String): Resources.Invites.ById =
    Resources.Invites.ById(inviteCode, Resources.Invites())

public inline fun Resources.Webhooks.Companion.ById(webhookId: Snowflake): Resources.Webhooks.ById =
    Resources.Webhooks.ById(webhookId, Resources.Webhooks())

public inline fun Resources.Webhooks.ById.Companion.WithToken(channelId: Snowflake, token: String):
    Resources.Webhooks.ById.WithToken = Resources.Webhooks.ById.WithToken(token,
    Resources.Channels.ById(channelId))

public inline fun Resources.Webhooks.ById.WithToken.Companion.Github(channelId: Snowflake,
    token: String): Resources.Webhooks.ById.WithToken.Github =
    Resources.Webhooks.ById.WithToken.Github(Resources.Webhooks.ById.WithToken(channelId, token))

public inline fun Resources.Webhooks.ById.WithToken.Companion.Slack(channelId: Snowflake,
    token: String): Resources.Webhooks.ById.WithToken.Slack =
    Resources.Webhooks.ById.WithToken.Slack(Resources.Webhooks.ById.WithToken(channelId, token))

public inline fun Resources.Webhooks.ById.WithToken.Companion.Messages(channelId: Snowflake,
    token: String): Resources.Webhooks.ById.WithToken.Messages =
    Resources.Webhooks.ById.WithToken.Messages(Resources.Webhooks.ById.WithToken(channelId, token))

public inline fun Resources.Webhooks.ById.WithToken.Messages.Companion.ById(
  channelId: Snowflake,
  token: String,
  messageId: Snowflake,
): Resources.Webhooks.ById.WithToken.Messages.ById =
    Resources.Webhooks.ById.WithToken.Messages.ById(messageId,
    Resources.Webhooks.ById.WithToken.Messages(channelId, token))

public inline
    fun Resources.Webhooks.ById.WithToken.Messages.Companion.Original(channelId: Snowflake,
    token: String): Resources.Webhooks.ById.WithToken.Messages.Original =
    Resources.Webhooks.ById.WithToken.Messages.Original(Resources.Webhooks.ById.WithToken.Messages(channelId,
    token))
