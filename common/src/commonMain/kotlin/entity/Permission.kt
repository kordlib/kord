@file:Generate(
    BIT_SET_FLAGS, name = "Permission", valueName = "code", collectionWasDataClass = true,
    docUrl = "https://discord.com/developers/docs/topics/permissions",
    bitFlagsDescriptor = BitFlagDescription("member", "permissions", name = "permission"),
    entries = [
        Entry(
            "CreateInstantInvite",
            shift = 0,
            kDoc = "Allows creation of instant invites."
        ),
        Entry("KickMembers", shift = 1, kDoc = "Allows kicking members."),
        Entry("BanMembers", shift = 2, kDoc = "Allows banning members."),
        Entry(
            "Administrator",
            shift = 3,
            kDoc = "Allows all permissions and bypasses channel permission overwrites."
        ),
        Entry(
            "ManageChannels",
            shift = 4,
            kDoc = "Allows management and editing of channels."
        ),
        Entry(
            "ManageGuild",
            shift = 5,
            kDoc = "Allows management and editing of the guild."
        ),
        Entry(
            "AddReactions",
            shift = 6,
            kDoc = "Allows for the addition of reactions to messages."
        ),
        Entry("ViewAuditLog", shift = 7, kDoc = "Allows for viewing of audit logs."),
        Entry(
            "PrioritySpeaker",
            shift = 8,
            kDoc = "Allows for using priority speaker in a voice channel."
        ),
        Entry("Stream", shift = 9, kDoc = "Allows the user to go live."),
        Entry(
            "ViewChannel", shift = 10, kDoc = """
         Allows guild members to view a channel, which includes reading messages in text channels and joining voice
         channels."""
        ),
        Entry(
            "SendMessages",
            shift = 11,
            kDoc = "Allows for sending messages in a channel and creating threads in a forum (does not allow sending " +
                    "messages in threads)."
        ),
        Entry(
            "SendTTSMessages",
            shift = 12,
            kDoc = "Allows for sending of `/tts` messages."
        ),
        Entry(
            "ManageMessages",
            shift = 13,
            kDoc = "Allows for deletion of other users messages."
        ),
        Entry(
            "EmbedLinks",
            shift = 14,
            kDoc = "Links sent by users with this permission will be auto-embedded."
        ),
        Entry("AttachFiles", shift = 15, kDoc = "Allows for uploading images and files."),
        Entry(
            "ReadMessageHistory",
            shift = 16,
            kDoc = "Allows for reading of message history."
        ),
        Entry(
            "MentionEveryone", shift = 17, kDoc = """
         Allows for using the `@everyone` tag to notify all users in a channel, and the `@here` tag to notify all online
         users in a channel."""
        ),
        Entry(
            "UseExternalEmojis",
            shift = 18,
            kDoc = "Allows the usage of custom emojis from other servers."
        ),
        Entry("ViewGuildInsights", shift = 19, kDoc = "Allows for viewing guild insights."),
        Entry("Connect", shift = 20, kDoc = "Allows for joining of a voice channel."),
        Entry("Speak", shift = 21, kDoc = "Allows for speaking in a voice channel."),
        Entry(
            "MuteMembers",
            shift = 22,
            kDoc = "Allows for muting members in a voice channel."
        ),
        Entry(
            "DeafenMembers",
            shift = 23,
            kDoc = "Allows for deafening of members in a voice channel."
        ),
        Entry(
            "MoveMembers",
            shift = 24,
            kDoc = "Allows for moving of members between voice channels."
        ),
        Entry(
            "UseVAD",
            shift = 25,
            kDoc = "Allows for using voice-activity-detection in a voice channel."
        ),
        Entry(
            "ChangeNickname",
            shift = 26,
            kDoc = "Allows for modification of own nickname."
        ),
        Entry(
            "ManageNicknames",
            shift = 27,
            kDoc = "Allows for modification of other users nicknames."
        ),
        Entry("ManageRoles", shift = 28, kDoc = "Allows management and editing of roles."),
        Entry(
            "ManageWebhooks",
            shift = 29,
            kDoc = "Allows management and editing of webhooks."
        ),
        Entry(
            "ManageEmojisAndStickers",
            shift = 30,
            kDoc = "Allows management and editing of emojis and stickers.",
            deprecated = Deprecated("Renamed by discord", ReplaceWith("ManageGuildExpressions"), DeprecationLevel.HIDDEN)
        ),
        Entry(
            "ManageGuildExpressions", shift = 30,
            kDoc = "Allows management and editing of emojis, stickers and soundboard sounds."
        ),
        Entry(
            "UseApplicationCommands",
            shift = 31,
            kDoc = "Allows members to use application commands, including slash commands and context menu commands."
        ),
        Entry(
            "RequestToSpeak", shift = 32, kDoc = """
         Allows for requesting to speak in stage channels.

         is permission is under active development and may be changed or removed._"""
        ),
        Entry(
            "ManageEvents",
            shift = 33,
            kDoc = "Allows for creating, editing, and deleting scheduled events."
        ),
        Entry(
            "ManageThreads",
            shift = 34,
            kDoc = "Allows for deleting and archiving threads, and viewing all private threads."
        ),
        Entry(
            "CreatePublicThreads",
            shift = 35,
            kDoc = "Allows for creating public and announcement threads."
        ),
        Entry(
            "CreatePrivateThreads",
            shift = 36,
            kDoc = "Allows for creating private threads."
        ),
        Entry(
            "UseExternalStickers",
            shift = 37,
            kDoc = "Allows the usage of custom stickers from other servers."
        ),
        Entry(
            "SendMessagesInThreads",
            shift = 38,
            kDoc = "Allows for sending messages in threads."
        ),
        Entry(
            "UseEmbeddedActivities", shift = 39, kDoc = """
         Allows for using Activities (applications with the [Embedded][ApplicationFlag.Embedded] flag) in a voice channel."""
        ),
        Entry(
            "ModerateMembers", shift = 40, kDoc = """
         Allows for timing out users to prevent them from sending or reacting to messages in chat and threads, and from
         speaking in voice and stage channels."""
        ),
        Entry(
            "ViewCreatorMonetizationAnalytics", shift = 41,
            kDoc = "Allows for viewing role subscription insights.",
        ),
        Entry(
            "UseSoundboard", shift = 42,
            kDoc = "Allows for using soundboard in a voice channel.",
        ),
        Entry(
            "UseExternalSounds", shift = 45,
            kDoc = "Allows the usage of custom soundboard sounds from other servers.",
        ),
        Entry(
            "SendVoiceMessages", shift = 46,
            kDoc = "Allows sending voice messages."
        ),
    ]
)

package dev.kord.common.entity

import dev.kord.ksp.Generate
import dev.kord.ksp.Generate.BitFlagDescription
import dev.kord.ksp.Generate.EntityType.BIT_SET_FLAGS
import dev.kord.ksp.Generate.Entry
