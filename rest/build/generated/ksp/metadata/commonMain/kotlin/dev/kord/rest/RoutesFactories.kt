@file:Suppress(names = arrayOf("NOTHING_TO_INLINE"))

package dev.kord.rest

import dev.kord.common.entity.Snowflake
import dev.kord.rest.route.Routes

public inline fun Routes.Users.Companion.Me(): Routes.Users.Me = Routes.Users.Me(Routes.Users())

public inline fun Routes.Users.Me.Companion.Guilds(): Routes.Users.Me.Guilds =
    Routes.Users.Me.Guilds(Routes.Users.Me())

public inline fun Routes.Users.Me.Guilds.Companion.ById(guildId: Snowflake):
    Routes.Users.Me.Guilds.ById = Routes.Users.Me.Guilds.ById(guildId, Routes.Users.Me.Guilds())

public inline fun Routes.Users.Me.Companion.Channels(): Routes.Users.Me.Channels =
    Routes.Users.Me.Channels(Routes.Users.Me())

public inline fun Routes.Users.Me.Companion.Connections(): Routes.Users.Me.Connections =
    Routes.Users.Me.Connections(Routes.Users.Me())

public inline fun Routes.Users.Companion.ById(userId: Snowflake): Routes.Users.ById =
    Routes.Users.ById(userId, Routes.Users())

public inline fun Routes.Guilds.Companion.ById(guildId: Snowflake): Routes.Guilds.ById =
    Routes.Guilds.ById(guildId, Routes.Guilds())

public inline fun Routes.Guilds.ById.Companion.AuditLog(guildId: Snowflake):
    Routes.Guilds.ById.AuditLog = Routes.Guilds.ById.AuditLog(Routes.Guilds.ById(guildId))

public inline fun Routes.Guilds.ById.Companion.MFA(guildId: Snowflake): Routes.Guilds.ById.MFA =
    Routes.Guilds.ById.MFA(Routes.Guilds.ById(guildId))

public inline fun Routes.Guilds.ById.Companion.Prune(guildId: Snowflake): Routes.Guilds.ById.Prune =
    Routes.Guilds.ById.Prune(Routes.Guilds.ById(guildId))

public inline fun Routes.Guilds.ById.Companion.Regions(guildId: Snowflake):
    Routes.Guilds.ById.Regions = Routes.Guilds.ById.Regions(Routes.Guilds.ById(guildId))

public inline fun Routes.Guilds.ById.Companion.Widget(guildId: Snowflake): Routes.Guilds.ById.Widget
    = Routes.Guilds.ById.Widget(Routes.Guilds.ById(guildId))

public inline fun Routes.Guilds.ById.Companion.VanityUrl(guildId: Snowflake):
    Routes.Guilds.ById.VanityUrl = Routes.Guilds.ById.VanityUrl(Routes.Guilds.ById(guildId))

public inline fun Routes.Guilds.ById.Companion.WelcomeScreen(guildId: Snowflake):
    Routes.Guilds.ById.WelcomeScreen = Routes.Guilds.ById.WelcomeScreen(Routes.Guilds.ById(guildId))

public inline fun Routes.Guilds.ById.Companion.Preview(guildId: Snowflake):
    Routes.Guilds.ById.Preview = Routes.Guilds.ById.Preview(Routes.Guilds.ById(guildId))

public inline fun Routes.Guilds.ById.Companion.Members(guildId: Snowflake):
    Routes.Guilds.ById.Members = Routes.Guilds.ById.Members(Routes.Guilds.ById(guildId))

public inline fun Routes.Guilds.ById.Members.Companion.ById(guildId: Snowflake, userId: Snowflake):
    Routes.Guilds.ById.Members.ById = Routes.Guilds.ById.Members.ById(userId,
    Routes.Guilds.ById.Members(guildId))

public inline fun Routes.Guilds.ById.Members.ById.Companion.Roles(guildId: Snowflake,
    userId: Snowflake): Routes.Guilds.ById.Members.ById.Roles =
    Routes.Guilds.ById.Members.ById.Roles(Routes.Guilds.ById.Members.ById(guildId, userId))

public inline fun Routes.Guilds.ById.Members.ById.Roles.Companion.ById(
  guildId: Snowflake,
  userId: Snowflake,
  roleId: Snowflake,
): Routes.Guilds.ById.Members.ById.Roles.ById = Routes.Guilds.ById.Members.ById.Roles.ById(roleId,
    Routes.Guilds.ById.Members.ById.Roles(guildId, userId))

public inline fun Routes.Guilds.ById.Members.Companion.Search(guildId: Snowflake):
    Routes.Guilds.ById.Members.Search =
    Routes.Guilds.ById.Members.Search(Routes.Guilds.ById.Members(guildId))

public inline fun Routes.Guilds.ById.Members.Companion.Me(guildId: Snowflake):
    Routes.Guilds.ById.Members.Me =
    Routes.Guilds.ById.Members.Me(Routes.Guilds.ById.Members(guildId))

public inline fun Routes.Guilds.ById.Members.Me.Companion.Nick(guildId: Snowflake):
    Routes.Guilds.ById.Members.Me.Nick =
    Routes.Guilds.ById.Members.Me.Nick(Routes.Guilds.ById.Members.Me(guildId))

public inline fun Routes.Guilds.ById.Companion.ScheduledEvents(guildId: Snowflake):
    Routes.Guilds.ById.ScheduledEvents =
    Routes.Guilds.ById.ScheduledEvents(Routes.Guilds.ById(guildId))

public inline fun Routes.Guilds.ById.ScheduledEvents.Companion.ById(guildId: Snowflake,
    scheduledEventId: Snowflake): Routes.Guilds.ById.ScheduledEvents.ById =
    Routes.Guilds.ById.ScheduledEvents.ById(scheduledEventId,
    Routes.Guilds.ById.ScheduledEvents(guildId))

public inline fun Routes.Guilds.ById.ScheduledEvents.ById.Companion.Users(guildId: Snowflake,
    scheduledEventId: Snowflake): Routes.Guilds.ById.ScheduledEvents.ById.Users =
    Routes.Guilds.ById.ScheduledEvents.ById.Users(Routes.Guilds.ById.ScheduledEvents.ById(guildId,
    scheduledEventId))

public inline fun Routes.Guilds.ById.Companion.Stickers(guildId: Snowflake):
    Routes.Guilds.ById.Stickers = Routes.Guilds.ById.Stickers(Routes.Guilds.ById(guildId))

public inline fun Routes.Guilds.ById.Stickers.Companion.ById(guildId: Snowflake,
    stickerId: Snowflake): Routes.Guilds.ById.Stickers.ById =
    Routes.Guilds.ById.Stickers.ById(stickerId, Routes.Guilds.ById.Stickers(guildId))

public inline fun Routes.Guilds.ById.Companion.Bans(guildId: Snowflake): Routes.Guilds.ById.Bans =
    Routes.Guilds.ById.Bans(Routes.Guilds.ById(guildId))

public inline fun Routes.Guilds.ById.Bans.Companion.ById(guildId: Snowflake, userId: Snowflake):
    Routes.Guilds.ById.Bans.ById = Routes.Guilds.ById.Bans.ById(userId,
    Routes.Guilds.ById.Bans(guildId))

public inline fun Routes.Guilds.ById.Companion.Channels(guildId: Snowflake,
    scheduledEventId: Snowflake): Routes.Guilds.ById.Channels =
    Routes.Guilds.ById.Channels(Routes.Guilds.ById.ScheduledEvents.ById(guildId, scheduledEventId))

public inline fun Routes.Guilds.ById.Companion.Threads(guildId: Snowflake):
    Routes.Guilds.ById.Threads = Routes.Guilds.ById.Threads(Routes.Guilds.ById(guildId))

public inline fun Routes.Guilds.ById.Threads.Companion.Active(guildId: Snowflake):
    Routes.Guilds.ById.Threads.Active =
    Routes.Guilds.ById.Threads.Active(Routes.Guilds.ById.Threads(guildId))

public inline fun Routes.Guilds.ById.Companion.Webhooks(guildId: Snowflake):
    Routes.Guilds.ById.Webhooks = Routes.Guilds.ById.Webhooks(Routes.Guilds.ById(guildId))

public inline fun Routes.Guilds.ById.Companion.Templates(guildId: Snowflake):
    Routes.Guilds.ById.Templates = Routes.Guilds.ById.Templates(Routes.Guilds.ById(guildId))

public inline fun Routes.Guilds.ById.Templates.Companion.ById(guildId: Snowflake,
    templateCode: String): Routes.Guilds.ById.Templates.ById =
    Routes.Guilds.ById.Templates.ById(templateCode, Routes.Guilds.ById.Templates(guildId))

public inline fun Routes.Guilds.ById.Companion.Invites(guildId: Snowflake):
    Routes.Guilds.ById.Invites = Routes.Guilds.ById.Invites(Routes.Guilds.ById(guildId))

public inline fun Routes.Guilds.ById.Invites.Companion.ById(guildId: Snowflake,
    inviteId: Snowflake): Routes.Guilds.ById.Invites.ById =
    Routes.Guilds.ById.Invites.ById(inviteId, Routes.Guilds.ById.Invites(guildId))

public inline fun Routes.Guilds.ById.Companion.Integrations(guildId: Snowflake):
    Routes.Guilds.ById.Integrations = Routes.Guilds.ById.Integrations(Routes.Guilds.ById(guildId))

public inline fun Routes.Guilds.ById.Integrations.Companion.ById(guildId: Snowflake,
    integrationId: Snowflake): Routes.Guilds.ById.Integrations.ById =
    Routes.Guilds.ById.Integrations.ById(integrationId, Routes.Guilds.ById.Integrations(guildId))

public inline fun Routes.Guilds.ById.Integrations.ById.Companion.Sync(guildId: Snowflake,
    integrationId: Snowflake): Routes.Guilds.ById.Integrations.ById.Sync =
    Routes.Guilds.ById.Integrations.ById.Sync(Routes.Guilds.ById.Integrations.ById(guildId,
    integrationId))

public inline fun Routes.Guilds.ById.Companion.VoiceStates(guildId: Snowflake):
    Routes.Guilds.ById.VoiceStates = Routes.Guilds.ById.VoiceStates(Routes.Guilds.ById(guildId))

public inline fun Routes.Guilds.ById.VoiceStates.Companion.ById(guildId: Snowflake, id: Snowflake):
    Routes.Guilds.ById.VoiceStates.ById = Routes.Guilds.ById.VoiceStates.ById(id,
    Routes.Guilds.ById.VoiceStates(guildId))

public inline fun Routes.Guilds.ById.Companion.Emojis(guildId: Snowflake): Routes.Guilds.ById.Emojis
    = Routes.Guilds.ById.Emojis(Routes.Guilds.ById(guildId))

public inline fun Routes.Guilds.ById.Companion.AutoModeration(guildId: Snowflake):
    Routes.Guilds.ById.AutoModeration =
    Routes.Guilds.ById.AutoModeration(Routes.Guilds.ById(guildId))

public inline fun Routes.Guilds.ById.AutoModeration.Companion.Rules(guildId: Snowflake):
    Routes.Guilds.ById.AutoModeration.Rules =
    Routes.Guilds.ById.AutoModeration.Rules(Routes.Guilds.ById.AutoModeration(guildId))

public inline fun Routes.Guilds.ById.AutoModeration.Rules.Companion.ById(guildId: Snowflake,
    autoModerationRuleId: Snowflake): Routes.Guilds.ById.AutoModeration.Rules.ById =
    Routes.Guilds.ById.AutoModeration.Rules.ById(autoModerationRuleId,
    Routes.Guilds.ById.AutoModeration.Rules(guildId))

public inline fun Routes.Channels.Companion.ById(channelId: Snowflake): Routes.Channels.ById =
    Routes.Channels.ById(channelId, Routes.Channels())

public inline fun Routes.Channels.ById.Companion.Typing(channelId: Snowflake):
    Routes.Channels.ById.Typing = Routes.Channels.ById.Typing(Routes.Channels.ById(channelId))

public inline fun Routes.Channels.ById.Companion.Followers(channelId: Snowflake):
    Routes.Channels.ById.Followers = Routes.Channels.ById.Followers(Routes.Channels.ById(channelId))

public inline fun Routes.Channels.ById.Companion.Recipients(channelId: Snowflake):
    Routes.Channels.ById.Recipients =
    Routes.Channels.ById.Recipients(Routes.Channels.ById(channelId))

public inline fun Routes.Channels.ById.Recipients.Companion.ById(channelId: Snowflake,
    userId: Snowflake): Routes.Channels.ById.Recipients.ById =
    Routes.Channels.ById.Recipients.ById(userId, Routes.Channels.ById.Recipients(channelId))

public inline fun Routes.Channels.ById.Companion.Threads(channelId: Snowflake):
    Routes.Channels.ById.Threads = Routes.Channels.ById.Threads(Routes.Channels.ById(channelId))

public inline fun Routes.Channels.ById.Threads.Companion.Private(channelId: Snowflake):
    Routes.Channels.ById.Threads.Private =
    Routes.Channels.ById.Threads.Private(Routes.Channels.ById.Threads(channelId))

public inline fun Routes.Channels.ById.Threads.Companion.Archived(channelId: Snowflake):
    Routes.Channels.ById.Threads.Archived =
    Routes.Channels.ById.Threads.Archived(Routes.Channels.ById.Threads(channelId))

public inline fun Routes.Channels.ById.Threads.Archived.Companion.Private(channelId: Snowflake):
    Routes.Channels.ById.Threads.Archived.Private =
    Routes.Channels.ById.Threads.Archived.Private(Routes.Channels.ById.Threads.Archived(channelId))

public inline fun Routes.Channels.ById.Threads.Archived.Companion.Public(channelId: Snowflake):
    Routes.Channels.ById.Threads.Archived.Public =
    Routes.Channels.ById.Threads.Archived.Public(Routes.Channels.ById.Threads.Archived(channelId))

public inline fun Routes.Channels.ById.Companion.ThreadMembers(channelId: Snowflake):
    Routes.Channels.ById.ThreadMembers =
    Routes.Channels.ById.ThreadMembers(Routes.Channels.ById(channelId))

public inline fun Routes.Channels.ById.ThreadMembers.Companion.Me(channelId: Snowflake):
    Routes.Channels.ById.ThreadMembers.Me =
    Routes.Channels.ById.ThreadMembers.Me(Routes.Channels.ById.ThreadMembers(channelId))

public inline fun Routes.Channels.ById.ThreadMembers.Companion.ById(channelId: Snowflake,
    userId: Snowflake): Routes.Channels.ById.ThreadMembers.ById =
    Routes.Channels.ById.ThreadMembers.ById(userId, Routes.Channels.ById.ThreadMembers(channelId))

public inline fun Routes.Channels.ById.Companion.Users(channelId: Snowflake):
    Routes.Channels.ById.Users = Routes.Channels.ById.Users(Routes.Channels.ById(channelId))

public inline fun Routes.Channels.ById.Users.Companion.Me(channelId: Snowflake):
    Routes.Channels.ById.Users.Me =
    Routes.Channels.ById.Users.Me(Routes.Channels.ById.Users(channelId))

public inline fun Routes.Channels.ById.Users.Me.Companion.Threads(channelId: Snowflake):
    Routes.Channels.ById.Users.Me.Threads =
    Routes.Channels.ById.Users.Me.Threads(Routes.Channels.ById.Users.Me(channelId))

public inline fun Routes.Channels.ById.Users.Me.Threads.Companion.Archived(channelId: Snowflake):
    Routes.Channels.ById.Users.Me.Threads.Archived =
    Routes.Channels.ById.Users.Me.Threads.Archived(Routes.Channels.ById.Users.Me.Threads(channelId))

public inline
    fun Routes.Channels.ById.Users.Me.Threads.Archived.Companion.Private(channelId: Snowflake):
    Routes.Channels.ById.Users.Me.Threads.Archived.Private =
    Routes.Channels.ById.Users.Me.Threads.Archived.Private(Routes.Channels.ById.Users.Me.Threads.Archived(channelId))

public inline fun Routes.Channels.ById.Companion.Invites(channelId: Snowflake):
    Routes.Channels.ById.Invites = Routes.Channels.ById.Invites(Routes.Channels.ById(channelId))

public inline fun Routes.Channels.ById.Companion.Pins(channelId: Snowflake):
    Routes.Channels.ById.Pins = Routes.Channels.ById.Pins(Routes.Channels.ById(channelId))

public inline fun Routes.Channels.ById.Pins.Companion.ById(channelId: Snowflake,
    messageId: Snowflake): Routes.Channels.ById.Pins.ById =
    Routes.Channels.ById.Pins.ById(messageId, Routes.Channels.ById.Pins(channelId))

public inline fun Routes.Channels.ById.Companion.Messages(channelId: Snowflake):
    Routes.Channels.ById.Messages = Routes.Channels.ById.Messages(Routes.Channels.ById(channelId))

public inline fun Routes.Channels.ById.Messages.Companion.BulkDelete(channelId: Snowflake):
    Routes.Channels.ById.Messages.BulkDelete =
    Routes.Channels.ById.Messages.BulkDelete(Routes.Channels.ById.Messages(channelId))

public inline fun Routes.Channels.ById.Messages.Companion.ById(channelId: Snowflake,
    messageId: Snowflake): Routes.Channels.ById.Messages.ById =
    Routes.Channels.ById.Messages.ById(messageId, Routes.Channels.ById.Messages(channelId))

public inline fun Routes.Channels.ById.Messages.ById.Companion.Threads(channelId: Snowflake,
    messageId: Snowflake): Routes.Channels.ById.Messages.ById.Threads =
    Routes.Channels.ById.Messages.ById.Threads(Routes.Channels.ById.Messages.ById(channelId,
    messageId))

public inline fun Routes.Channels.ById.Messages.ById.Companion.CrossPost(channelId: Snowflake,
    messageId: Snowflake): Routes.Channels.ById.Messages.ById.CrossPost =
    Routes.Channels.ById.Messages.ById.CrossPost(Routes.Channels.ById.Messages.ById(channelId,
    messageId))

public inline fun Routes.Channels.ById.Messages.ById.Companion.Reactions(channelId: Snowflake,
    messageId: Snowflake): Routes.Channels.ById.Messages.ById.Reactions =
    Routes.Channels.ById.Messages.ById.Reactions(Routes.Channels.ById.Messages.ById(channelId,
    messageId))

public inline fun Routes.Channels.ById.Messages.ById.Reactions.Companion.ById(
  channelId: Snowflake,
  messageId: Snowflake,
  emojiId: String,
): Routes.Channels.ById.Messages.ById.Reactions.ById =
    Routes.Channels.ById.Messages.ById.Reactions.ById(emojiId,
    Routes.Channels.ById.Messages.ById.Reactions(channelId, messageId))

public inline fun Routes.Channels.ById.Messages.ById.Reactions.ById.Companion.Me(
  channelId: Snowflake,
  messageId: Snowflake,
  emojiId: String,
): Routes.Channels.ById.Messages.ById.Reactions.ById.Me =
    Routes.Channels.ById.Messages.ById.Reactions.ById.Me(Routes.Channels.ById.Messages.ById.Reactions.ById(channelId,
    messageId, emojiId))

public inline fun Routes.Channels.ById.Messages.ById.Reactions.ById.Companion.ReactorById(
  channelId: Snowflake,
  messageId: Snowflake,
  emojiId: String,
  userId: Snowflake,
): Routes.Channels.ById.Messages.ById.Reactions.ById.ReactorById =
    Routes.Channels.ById.Messages.ById.Reactions.ById.ReactorById(userId,
    Routes.Channels.ById.Messages.ById.Reactions.ById(channelId, messageId, emojiId))

public inline fun Routes.Channels.ById.Companion.Permissions(channelId: Snowflake):
    Routes.Channels.ById.Permissions =
    Routes.Channels.ById.Permissions(Routes.Channels.ById.Messages(channelId))

public inline fun Routes.Channels.ById.Permissions.Companion.ById(channelId: Snowflake,
    overwriteId: Snowflake): Routes.Channels.ById.Permissions.ById =
    Routes.Channels.ById.Permissions.ById(overwriteId, Routes.Channels.ById.Permissions(channelId))

public inline fun Routes.Channels.ById.Companion.Webhooks(channelId: Snowflake):
    Routes.Channels.ById.Webhooks = Routes.Channels.ById.Webhooks(Routes.Channels.ById(channelId))

public inline fun Routes.Invites.Companion.ById(inviteCode: String): Routes.Invites.ById =
    Routes.Invites.ById(inviteCode, Routes.Invites())

public inline fun Routes.Webhooks.Companion.ById(webhookId: Snowflake): Routes.Webhooks.ById =
    Routes.Webhooks.ById(webhookId, Routes.Webhooks())

public inline fun Routes.Webhooks.ById.Companion.WithToken(channelId: Snowflake, token: String):
    Routes.Webhooks.ById.WithToken = Routes.Webhooks.ById.WithToken(token,
    Routes.Channels.ById(channelId))

public inline fun Routes.Webhooks.ById.WithToken.Companion.Github(channelId: Snowflake,
    token: String): Routes.Webhooks.ById.WithToken.Github =
    Routes.Webhooks.ById.WithToken.Github(Routes.Webhooks.ById.WithToken(channelId, token))

public inline fun Routes.Webhooks.ById.WithToken.Companion.Slack(channelId: Snowflake,
    token: String): Routes.Webhooks.ById.WithToken.Slack =
    Routes.Webhooks.ById.WithToken.Slack(Routes.Webhooks.ById.WithToken(channelId, token))

public inline fun Routes.Webhooks.ById.WithToken.Companion.Messages(channelId: Snowflake,
    token: String): Routes.Webhooks.ById.WithToken.Messages =
    Routes.Webhooks.ById.WithToken.Messages(Routes.Webhooks.ById.WithToken(channelId, token))

public inline fun Routes.Webhooks.ById.WithToken.Messages.Companion.ById(
  channelId: Snowflake,
  token: String,
  messageId: Snowflake,
): Routes.Webhooks.ById.WithToken.Messages.ById =
    Routes.Webhooks.ById.WithToken.Messages.ById(messageId,
    Routes.Webhooks.ById.WithToken.Messages(channelId, token))

public inline fun Routes.Webhooks.ById.WithToken.Messages.Companion.Original(channelId: Snowflake,
    token: String): Routes.Webhooks.ById.WithToken.Messages.Original =
    Routes.Webhooks.ById.WithToken.Messages.Original(Routes.Webhooks.ById.WithToken.Messages(channelId,
    token))

public inline fun Routes.Voice.Companion.Regions(): Routes.Voice.Regions =
    Routes.Voice.Regions(Routes.Voice())
