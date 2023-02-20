@file:GenerateKordEnum(
    name = "Permission",
    valueType = ValueType.BITSET,
    isFlags = true,
    docUrl = "https://discord.com/developers/docs/topics/permissions",
    hasCombinerFlag = true,
    bitFlagsDescriptor = BitFlagDescription("member", "permissions", name = "permission"),
    entries = [
        Entry(
            "CreateInstantInvite",
            longValue = 1L shl 0,
            kDoc = "Allows creation of instant invites."
        ),
        Entry("KickMembers", longValue = 1L shl 1, kDoc = "Allows kicking members."),
        Entry("BanMembers", longValue = 1L shl 2, kDoc = "Allows banning members."),
        Entry(
            "Administrator",
            longValue = 1L shl 3,
            kDoc = "Allows all permissions and bypasses channel permission overwrites."
        ),
        Entry(
            "ManageChannels",
            longValue = 1L shl 4,
            kDoc = "Allows management and editing of channels."
        ),
        Entry(
            "ManageGuild",
            longValue = 1L shl 5,
            kDoc = "Allows management and editing of the guild."
        ),
        Entry(
            "AddReactions",
            longValue = 1L shl 6,
            kDoc = "Allows for the addition of reactions to messages."
        ),
        Entry("ViewAuditLog", longValue = 1L shl 7, kDoc = "Allows for viewing of audit logs."),
        Entry(
            "PrioritySpeaker",
            longValue = 1L shl 8,
            kDoc = "Allows for using priority speaker in a voice channel."
        ),
        Entry("Stream", longValue = 1L shl 9, kDoc = "Allows the user to go live."),
        Entry(
            "ViewChannel", longValue = 1L shl 10, kDoc = """
         Allows guild members to view a channel, which includes reading messages in text channels and joining voice
         channels."""
        ),
        Entry(
            "SendMessages",
            longValue = 1L shl 11,
            kDoc = "Allows for sending messages in a channel (does not allow sending messages in threads)."
        ),
        Entry(
            "SendTTSMessages",
            longValue = 1L shl 12,
            kDoc = "Allows for sending of `/tts` messages."
        ),
        Entry(
            "ManageMessages",
            longValue = 1L shl 13,
            kDoc = "Allows for deletion of other users messages."
        ),
        Entry(
            "EmbedLinks",
            longValue = 1L shl 14,
            kDoc = "Links sent by users with this permission will be auto-embedded."
        ),
        Entry("AttachFiles", longValue = 1L shl 15, kDoc = "Allows for uploading images and files."),
        Entry(
            "ReadMessageHistory",
            longValue = 1L shl 16,
            kDoc = "Allows for reading of message history."
        ),
        Entry(
            "MentionEveryone", longValue = 1L shl 17, kDoc = """
         Allows for using the `@everyone` tag to notify all users in a channel, and the `@here` tag to notify all online
         users in a channel."""
        ),
        Entry(
            "UseExternalEmojis",
            longValue = 1L shl 18,
            kDoc = "Allows the usage of custom emojis from other servers."
        ),
        Entry("ViewGuildInsights", longValue = 1L shl 19, kDoc = "Allows for viewing guild insights."),
        Entry("Connect", longValue = 1L shl 20, kDoc = "Allows for joining of a voice channel."),
        Entry("Speak", longValue = 1L shl 21, kDoc = "Allows for speaking in a voice channel."),
        Entry(
            "MuteMembers",
            longValue = 1L shl 22,
            kDoc = "Allows for muting members in a voice channel."
        ),
        Entry(
            "DeafenMembers",
            longValue = 1L shl 23,
            kDoc = "Allows for deafening of members in a voice channel."
        ),
        Entry(
            "MoveMembers",
            longValue = 1L shl 24,
            kDoc = "Allows for moving of members between voice channels."
        ),
        Entry(
            "UseVAD",
            longValue = 1L shl 25,
            kDoc = "Allows for using voice-activity-detection in a voice channel."
        ),
        Entry(
            "ChangeNickname",
            longValue = 1L shl 26,
            kDoc = "Allows for modification of own nickname."
        ),
        Entry(
            "ManageNicknames",
            longValue = 1L shl 27,
            kDoc = "Allows for modification of other users nicknames."
        ),
        Entry("ManageRoles", longValue = 1L shl 28, kDoc = "Allows management and editing of roles."),
        Entry(
            "ManageWebhooks",
            longValue = 1L shl 29,
            kDoc = "Allows management and editing of webhooks."
        ),
        Entry(
            "ManageEmojisAndStickers",
            longValue = 1L shl 30,
            kDoc = "Allows management and editing of emojis and stickers."
        ),
        Entry(
            "UseApplicationCommands",
            longValue = 1L shl 31,
            kDoc = "Allows members to use application commands, including slash commands and context menu commands."
        ),
        Entry(
            "RequestToSpeak", longValue = 1L shl 32, kDoc = """
         Allows for requesting to speak in stage channels.
         
         is permission is under active development and may be changed or removed._"""
        ),
        Entry(
            "ManageEvents",
            longValue = 1L shl 33,
            kDoc = "Allows for creating, editing, and deleting scheduled events."
        ),
        Entry(
            "ManageThreads",
            longValue = 1L shl 34,
            kDoc = "Allows for deleting and archiving threads, and viewing all private threads."
        ),
        Entry(
            "CreatePublicThreads",
            longValue = 1L shl 35,
            kDoc = "Allows for creating public and announcement threads."
        ),
        Entry(
            "CreatePrivateThreads",
            longValue = 1L shl 36,
            kDoc = "Allows for creating private threads."
        ),
        Entry(
            "UseExternalStickers",
            longValue = 1L shl 37,
            kDoc = "Allows the usage of custom stickers from other servers."
        ),
        Entry(
            "SendMessagesInThreads",
            longValue = 1L shl 38,
            kDoc = "Allows for sending messages in threads."
        ),
        Entry(
            "UseEmbeddedActivities", longValue = 1L shl 39, kDoc = """
         Allows for using Activities (applications with the [Embedded][ApplicationFlag.Embedded] flag) in a voice channel."""
        ),
        Entry(
            "ModerateMembers", longValue = 1L shl 40, kDoc = """
         Allows for timing out users to prevent them from sending or reacting to messages in chat and threads, and from
         speaking in voice and stage channels."""
        ),
    ]
)

package dev.kord.common.entity

import dev.kord.ksp.GenerateKordEnum
import dev.kord.ksp.GenerateKordEnum.*
