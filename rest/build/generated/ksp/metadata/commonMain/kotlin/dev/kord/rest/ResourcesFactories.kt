@file:Suppress(names = arrayOf("NOTHING_TO_INLINE"))

package dev.kord.rest

import dev.kord.common.entity.Snowflake
import dev.kord.rest.route.Channels
import dev.kord.rest.route.Guilds
import dev.kord.rest.route.Invites
import dev.kord.rest.route.Users
import dev.kord.rest.route.Webhooks

public inline fun Users.Companion.Me(): Users.Me = Users.Me(Users())

public inline fun Users.Me.Companion.Guilds(): Users.Me.Guilds = Users.Me.Guilds(Users.Me())

public inline fun Users.Me.Guilds.Companion.ById(guildId: Snowflake): Users.Me.Guilds.ById =
    Users.Me.Guilds.ById(guildId, Users.Me.Guilds())

public inline fun Users.Me.Companion.Channels(): Users.Me.Channels = Users.Me.Channels(Users.Me())

public inline fun Users.Me.Companion.Connections(): Users.Me.Connections =
    Users.Me.Connections(Users.Me())

public inline fun Users.Companion.ById(userId: Snowflake): Users.ById = Users.ById(userId, Users())

public inline fun Guilds.Companion.ById(guildId: Snowflake): Guilds.ById = Guilds.ById(guildId,
    Guilds())

public inline fun Guilds.ById.Companion.AuditLog(guildId: Snowflake): Guilds.ById.AuditLog =
    Guilds.ById.AuditLog(Guilds.ById(guildId))

public inline fun Guilds.ById.Companion.MFA(guildId: Snowflake): Guilds.ById.MFA =
    Guilds.ById.MFA(Guilds.ById(guildId))

public inline fun Guilds.ById.Companion.Prune(guildId: Snowflake): Guilds.ById.Prune =
    Guilds.ById.Prune(Guilds.ById(guildId))

public inline fun Guilds.ById.Companion.Regions(guildId: Snowflake): Guilds.ById.Regions =
    Guilds.ById.Regions(Guilds.ById(guildId))

public inline fun Guilds.ById.Companion.Widget(guildId: Snowflake): Guilds.ById.Widget =
    Guilds.ById.Widget(Guilds.ById(guildId))

public inline fun Guilds.ById.Companion.VanityUrl(guildId: Snowflake): Guilds.ById.VanityUrl =
    Guilds.ById.VanityUrl(Guilds.ById(guildId))

public inline fun Guilds.ById.Companion.WelcomeScreen(guildId: Snowflake): Guilds.ById.WelcomeScreen
    = Guilds.ById.WelcomeScreen(Guilds.ById(guildId))

public inline fun Guilds.ById.Companion.Preview(guildId: Snowflake): Guilds.ById.Preview =
    Guilds.ById.Preview(Guilds.ById(guildId))

public inline fun Guilds.ById.Companion.Members(guildId: Snowflake): Guilds.ById.Members =
    Guilds.ById.Members(Guilds.ById(guildId))

public inline fun Guilds.ById.Members.Companion.ById(guildId: Snowflake, userId: Snowflake):
    Guilds.ById.Members.ById = Guilds.ById.Members.ById(userId, Guilds.ById.Members(guildId))

public inline fun Guilds.ById.Members.ById.Companion.Roles(guildId: Snowflake, userId: Snowflake):
    Guilds.ById.Members.ById.Roles =
    Guilds.ById.Members.ById.Roles(Guilds.ById.Members.ById(guildId, userId))

public inline fun Guilds.ById.Members.ById.Roles.Companion.ById(
  guildId: Snowflake,
  userId: Snowflake,
  roleId: Snowflake,
): Guilds.ById.Members.ById.Roles.ById = Guilds.ById.Members.ById.Roles.ById(roleId,
    Guilds.ById.Members.ById.Roles(guildId, userId))

public inline fun Guilds.ById.Members.Companion.Search(guildId: Snowflake):
    Guilds.ById.Members.Search = Guilds.ById.Members.Search(Guilds.ById.Members(guildId))

public inline fun Guilds.ById.Members.Companion.Me(guildId: Snowflake): Guilds.ById.Members.Me =
    Guilds.ById.Members.Me(Guilds.ById.Members(guildId))

public inline fun Guilds.ById.Members.Me.Companion.Nick(guildId: Snowflake):
    Guilds.ById.Members.Me.Nick = Guilds.ById.Members.Me.Nick(Guilds.ById.Members.Me(guildId))

public inline fun Guilds.ById.Companion.ScheduledEvents(guildId: Snowflake):
    Guilds.ById.ScheduledEvents = Guilds.ById.ScheduledEvents(Guilds.ById(guildId))

public inline fun Guilds.ById.ScheduledEvents.Companion.ById(guildId: Snowflake,
    scheduledEventId: Snowflake): Guilds.ById.ScheduledEvents.ById =
    Guilds.ById.ScheduledEvents.ById(scheduledEventId, Guilds.ById.ScheduledEvents(guildId))

public inline fun Guilds.ById.ScheduledEvents.ById.Companion.Users(guildId: Snowflake,
    scheduledEventId: Snowflake): Guilds.ById.ScheduledEvents.ById.Users =
    Guilds.ById.ScheduledEvents.ById.Users(Guilds.ById.ScheduledEvents.ById(guildId,
    scheduledEventId))

public inline fun Guilds.ById.Companion.Stickers(guildId: Snowflake): Guilds.ById.Stickers =
    Guilds.ById.Stickers(Guilds.ById(guildId))

public inline fun Guilds.ById.Stickers.Companion.ById(guildId: Snowflake, stickerId: Snowflake):
    Guilds.ById.Stickers.ById = Guilds.ById.Stickers.ById(stickerId, Guilds.ById.Stickers(guildId))

public inline fun Guilds.ById.Companion.Bans(guildId: Snowflake): Guilds.ById.Bans =
    Guilds.ById.Bans(Guilds.ById(guildId))

public inline fun Guilds.ById.Bans.Companion.ById(guildId: Snowflake, userId: Snowflake):
    Guilds.ById.Bans.ById = Guilds.ById.Bans.ById(userId, Guilds.ById.Bans(guildId))

public inline fun Guilds.ById.Companion.Channels(guildId: Snowflake): Guilds.ById.Channels =
    Guilds.ById.Channels(Guilds.ById(guildId))

public inline fun Guilds.ById.Companion.Threads(guildId: Snowflake): Guilds.ById.Threads =
    Guilds.ById.Threads(Guilds.ById(guildId))

public inline fun Guilds.ById.Threads.Companion.Active(guildId: Snowflake):
    Guilds.ById.Threads.Active = Guilds.ById.Threads.Active(Guilds.ById.Threads(guildId))

public inline fun Guilds.ById.Companion.Webhooks(guildId: Snowflake): Guilds.ById.Webhooks =
    Guilds.ById.Webhooks(Guilds.ById(guildId))

public inline fun Guilds.ById.Companion.Templates(guildId: Snowflake): Guilds.ById.Templates =
    Guilds.ById.Templates(Guilds.ById(guildId))

public inline fun Guilds.ById.Templates.Companion.ById(guildId: Snowflake, templateCode: String):
    Guilds.ById.Templates.ById = Guilds.ById.Templates.ById(templateCode,
    Guilds.ById.Templates(guildId))

public inline fun Guilds.ById.Companion.Invites(guildId: Snowflake): Guilds.ById.Invites =
    Guilds.ById.Invites(Guilds.ById(guildId))

public inline fun Guilds.ById.Invites.Companion.ById(guildId: Snowflake, inviteId: Snowflake):
    Guilds.ById.Invites.ById = Guilds.ById.Invites.ById(inviteId, Guilds.ById.Invites(guildId))

public inline fun Guilds.ById.Companion.Integrations(guildId: Snowflake): Guilds.ById.Integrations =
    Guilds.ById.Integrations(Guilds.ById(guildId))

public inline fun Guilds.ById.Integrations.Companion.ById(guildId: Snowflake,
    integrationId: Snowflake): Guilds.ById.Integrations.ById =
    Guilds.ById.Integrations.ById(integrationId, Guilds.ById.Integrations(guildId))

public inline fun Guilds.ById.Integrations.ById.Companion.Sync(guildId: Snowflake,
    integrationId: Snowflake): Guilds.ById.Integrations.ById.Sync =
    Guilds.ById.Integrations.ById.Sync(Guilds.ById.Integrations.ById(guildId, integrationId))

public inline fun Guilds.ById.Companion.VoiceStates(guildId: Snowflake): Guilds.ById.VoiceStates =
    Guilds.ById.VoiceStates(Guilds.ById(guildId))

public inline fun Guilds.ById.VoiceStates.Companion.ById(guildId: Snowflake, id: Snowflake):
    Guilds.ById.VoiceStates.ById = Guilds.ById.VoiceStates.ById(id,
    Guilds.ById.VoiceStates(guildId))

public inline fun Guilds.ById.Companion.Emojis(guildId: Snowflake): Guilds.ById.Emojis =
    Guilds.ById.Emojis(Guilds.ById(guildId))

public inline fun Guilds.ById.Companion.AutoModeration(guildId: Snowflake):
    Guilds.ById.AutoModeration = Guilds.ById.AutoModeration(Guilds.ById(guildId))

public inline fun Guilds.ById.AutoModeration.Companion.Rules(guildId: Snowflake):
    Guilds.ById.AutoModeration.Rules =
    Guilds.ById.AutoModeration.Rules(Guilds.ById.AutoModeration(guildId))

public inline fun Channels.Companion.ById(channelId: Snowflake): Channels.ById =
    Channels.ById(channelId, Channels())

public inline fun Channels.ById.Companion.Typing(channelId: Snowflake): Channels.ById.Typing =
    Channels.ById.Typing(Channels.ById(channelId))

public inline fun Channels.ById.Companion.Recipients(channelId: Snowflake): Channels.ById.Recipients
    = Channels.ById.Recipients(Channels.ById(channelId))

public inline fun Channels.ById.Recipients.Companion.ById(channelId: Snowflake, userId: Snowflake):
    Channels.ById.Recipients.ById = Channels.ById.Recipients.ById(userId,
    Channels.ById.Recipients(channelId))

public inline fun Channels.ById.Companion.Threads(channelId: Snowflake): Channels.ById.Threads =
    Channels.ById.Threads(Channels.ById(channelId))

public inline fun Channels.ById.Threads.Companion.Private(channelId: Snowflake):
    Channels.ById.Threads.Private = Channels.ById.Threads.Private(Channels.ById.Threads(channelId))

public inline fun Channels.ById.Threads.Companion.Archived(channelId: Snowflake):
    Channels.ById.Threads.Archived =
    Channels.ById.Threads.Archived(Channels.ById.Threads(channelId))

public inline fun Channels.ById.Threads.Archived.Companion.Private(channelId: Snowflake):
    Channels.ById.Threads.Archived.Private =
    Channels.ById.Threads.Archived.Private(Channels.ById.Threads.Archived(channelId))

public inline fun Channels.ById.Threads.Archived.Companion.Public(channelId: Snowflake):
    Channels.ById.Threads.Archived.Public =
    Channels.ById.Threads.Archived.Public(Channels.ById.Threads.Archived(channelId))

public inline fun Channels.ById.Companion.ThreadMembers(channelId: Snowflake):
    Channels.ById.ThreadMembers = Channels.ById.ThreadMembers(Channels.ById(channelId))

public inline fun Channels.ById.ThreadMembers.Companion.Me(channelId: Snowflake):
    Channels.ById.ThreadMembers.Me =
    Channels.ById.ThreadMembers.Me(Channels.ById.ThreadMembers(channelId))

public inline fun Channels.ById.ThreadMembers.Companion.ById(channelId: Snowflake,
    userId: Snowflake): Channels.ById.ThreadMembers.ById = Channels.ById.ThreadMembers.ById(userId,
    Channels.ById.ThreadMembers(channelId))

public inline fun Channels.ById.Companion.Invites(channelId: Snowflake): Channels.ById.Invites =
    Channels.ById.Invites(Channels.ById(channelId))

public inline fun Channels.ById.Companion.Pins(channelId: Snowflake): Channels.ById.Pins =
    Channels.ById.Pins(Channels.ById(channelId))

public inline fun Channels.ById.Pins.Companion.ById(channelId: Snowflake, messageId: Snowflake):
    Channels.ById.Pins.ById = Channels.ById.Pins.ById(messageId, Channels.ById.Pins(channelId))

public inline fun Channels.ById.Companion.Messages(channelId: Snowflake): Channels.ById.Messages =
    Channels.ById.Messages(Channels.ById(channelId))

public inline fun Channels.ById.Messages.Companion.BulkDelete(channelId: Snowflake):
    Channels.ById.Messages.BulkDelete =
    Channels.ById.Messages.BulkDelete(Channels.ById.Messages(channelId))

public inline fun Channels.ById.Messages.Companion.ById(channelId: Snowflake, messageId: Snowflake):
    Channels.ById.Messages.ById = Channels.ById.Messages.ById(messageId,
    Channels.ById.Messages(channelId))

public inline fun Channels.ById.Messages.ById.Companion.Threads(channelId: Snowflake,
    messageId: Snowflake): Channels.ById.Messages.ById.Threads =
    Channels.ById.Messages.ById.Threads(Channels.ById.Messages.ById(channelId, messageId))

public inline fun Channels.ById.Messages.ById.Companion.CrossPost(channelId: Snowflake,
    messageId: Snowflake): Channels.ById.Messages.ById.CrossPost =
    Channels.ById.Messages.ById.CrossPost(Channels.ById.Messages.ById(channelId, messageId))

public inline fun Channels.ById.Messages.ById.Companion.Reactions(channelId: Snowflake,
    messageId: Snowflake): Channels.ById.Messages.ById.Reactions =
    Channels.ById.Messages.ById.Reactions(Channels.ById.Messages.ById(channelId, messageId))

public inline fun Channels.ById.Messages.ById.Reactions.Companion.ById(
  channelId: Snowflake,
  messageId: Snowflake,
  emojiId: Snowflake,
): Channels.ById.Messages.ById.Reactions.ById = Channels.ById.Messages.ById.Reactions.ById(emojiId,
    Channels.ById.Messages.ById.Reactions(channelId, messageId))

public inline fun Channels.ById.Messages.ById.Reactions.ById.Companion.Me(guildId: Snowflake):
    Channels.ById.Messages.ById.Reactions.ById.Me =
    Channels.ById.Messages.ById.Reactions.ById.Me(Guilds.ById.Emojis(guildId))

public inline fun Channels.ById.Messages.ById.Reactions.ById.Companion.ReactorById(
  channelId: Snowflake,
  messageId: Snowflake,
  emojiId: Snowflake,
  userId: Snowflake,
): Channels.ById.Messages.ById.Reactions.ById.ReactorById =
    Channels.ById.Messages.ById.Reactions.ById.ReactorById(userId,
    Channels.ById.Messages.ById.Reactions.ById(channelId, messageId, emojiId))

public inline fun Channels.ById.Companion.Permissions(channelId: Snowflake):
    Channels.ById.Permissions = Channels.ById.Permissions(Channels.ById.Messages(channelId))

public inline fun Channels.ById.Companion.Webhooks(channelId: Snowflake): Channels.ById.Webhooks =
    Channels.ById.Webhooks(Channels.ById(channelId))

public inline fun Invites.Companion.ById(inviteCode: String): Invites.ById =
    Invites.ById(inviteCode, Invites())

public inline fun Webhooks.Companion.ById(webhookId: Snowflake): Webhooks.ById =
    Webhooks.ById(webhookId, Webhooks())

public inline fun Webhooks.ById.Companion.WithToken(webhookId: Snowflake, token: String):
    Webhooks.ById.WithToken = Webhooks.ById.WithToken(token, Webhooks.ById(webhookId))

public inline fun Webhooks.ById.WithToken.Companion.Github(webhookId: Snowflake, token: String):
    Webhooks.ById.WithToken.Github =
    Webhooks.ById.WithToken.Github(Webhooks.ById.WithToken(webhookId, token))

public inline fun Webhooks.ById.WithToken.Companion.Slack(webhookId: Snowflake, token: String):
    Webhooks.ById.WithToken.Slack = Webhooks.ById.WithToken.Slack(Webhooks.ById.WithToken(webhookId,
    token))

public inline fun Webhooks.ById.WithToken.Companion.Messages(webhookId: Snowflake, token: String):
    Webhooks.ById.WithToken.Messages =
    Webhooks.ById.WithToken.Messages(Webhooks.ById.WithToken(webhookId, token))

public inline fun Webhooks.ById.WithToken.Messages.Companion.ById(
  webhookId: Snowflake,
  token: String,
  messageId: Snowflake,
): Webhooks.ById.WithToken.Messages.ById = Webhooks.ById.WithToken.Messages.ById(messageId,
    Webhooks.ById.WithToken.Messages(webhookId, token))

public inline fun Webhooks.ById.WithToken.Messages.Companion.Original(webhookId: Snowflake,
    token: String): Webhooks.ById.WithToken.Messages.Original =
    Webhooks.ById.WithToken.Messages.Original(Webhooks.ById.WithToken.Messages(webhookId, token))
