package dev.kord.common.entity

import dev.kord.common.DiscordBitSet
import dev.kord.common.EmptyBitSet
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


@Serializable(with = Permissions.Companion::class)
public data class Permissions(val code: DiscordBitSet) {
    /**
     * Returns this [Permissions] as a [Set] of [Permission]s, not including any [unknown][Permission.Unknown]
     * permissions.
     */
    val values: Set<Permission> get() = Permission.values.filter { it.code in code }.toSet()

    public operator fun plus(permission: Permission): Permissions = Permissions(code + permission.code)


    public operator fun minus(permission: Permission): Permissions = Permissions(code - permission.code)


    public operator fun contains(permission: Permission): Boolean {
        return permission.code in code
    }

    public operator fun plus(permission: Permissions): Permissions = Permissions(code + permission.code)


    public operator fun minus(permission: Permissions): Permissions = Permissions(code - permission.code)


    public operator fun contains(permission: Permissions): Boolean {
        return permission.code in code
    }

    public inline fun copy(block: PermissionsBuilder.() -> Unit): Permissions {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }
        val builder = PermissionsBuilder(code)
        builder.apply(block)
        return builder.permissions()
    }

    override fun toString(): String {
        return "Permissions(values=$code)"
    }

    public companion object : KSerializer<Permissions> {

        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("permission", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): Permissions {
            val permissions = decoder.decodeString()
            return Permissions(permissions)
        }

        override fun serialize(encoder: Encoder, value: Permissions) {
            val permissionsSet = value.code.value
            encoder.encodeString(permissionsSet)
        }

    }

    public class PermissionsBuilder(internal val code: DiscordBitSet) {
        public operator fun Permissions.unaryPlus() {
            this@PermissionsBuilder.code.add(code)
        }

        public operator fun Permissions.unaryMinus() {
            this@PermissionsBuilder.code.remove(code)
        }

        public operator fun Permission.unaryPlus() {
            this@PermissionsBuilder.code.add(code)
        }

        public operator fun Permission.unaryMinus() {
            this@PermissionsBuilder.code.remove(code)
        }

        public fun permissions(): Permissions = Permissions(code)
    }
}

public fun Permissions(value: String): Permissions = Permissions(DiscordBitSet(value))

public inline fun Permissions(block: Permissions.PermissionsBuilder.() -> Unit = {}): Permissions {
    val builder = Permissions.PermissionsBuilder(DiscordBitSet(0))
    builder.apply(block)
    return builder.permissions()
}

public fun Permissions(vararg permissions: Permission): Permissions = Permissions {
    permissions.forEach { +it }
}

public fun Permissions(vararg permissions: Permissions): Permissions = Permissions {
    permissions.forEach { +it }
}

public fun Permissions(permissions: Iterable<Permission>): Permissions = Permissions {
    permissions.forEach { +it }
}


@JvmName("PermissionWithIterable")
public fun Permissions(permissions: Iterable<Permissions>): Permissions = Permissions {
    permissions.forEach { +it }
}


/** A [Permission](https://discord.com/developers/docs/topics/permissions) in Discord. */
public sealed class Permission(public val code: DiscordBitSet) {
    protected constructor(vararg values: Long) : this(DiscordBitSet(values))

    /**
     * A fallback [Permission] for permissions that haven't been added to Kord yet.
     *
     * You can use this to check if an instance of [Permissions] does [contain][Permissions.contains] an unknown
     * permission:
     *
     * ```kotlin
     * val permissions: Permissions = ...
     * // 1 << 63 as an example for some new permission
     * val hasNewPermission = Permission.Unknown(1L shl 63) in permissions
     * ```
     *
     * See [here](https://discord.com/developers/docs/topics/permissions#permissions-bitwise-permission-flags) for a
     * listing of the bitwise permission flags.
     */
    public class Unknown : Permission {
        public constructor(code: DiscordBitSet) : super(code)
        public constructor(vararg values: Long) : super(*values)
    }

    /** Allows creation of instant invites. */
    public object CreateInstantInvite : Permission(1L shl 0)

    /** Allows kicking members. */
    public object KickMembers : Permission(1L shl 1)

    /** Allows banning members. */
    public object BanMembers : Permission(1L shl 2)

    /** Allows all permissions and bypasses channel permission overwrites. */
    public object Administrator : Permission(1L shl 3)

    /** Allows management and editing of channels. */
    public object ManageChannels : Permission(1L shl 4)

    /** Allows management and editing of the guild. */
    public object ManageGuild : Permission(1L shl 5)

    /** Allows for the addition of reactions to messages. */
    public object AddReactions : Permission(1L shl 6)

    /** Allows for viewing of audit logs. */
    public object ViewAuditLog : Permission(1L shl 7)

    /** Allows for using priority speaker in a voice channel. */
    public object PrioritySpeaker : Permission(1L shl 8)

    /** Allows the user to go live. */
    public object Stream : Permission(1L shl 9)

    /**
     * Allows guild members to view a channel, which includes reading messages in text channels and joining voice
     * channels.
     */
    public object ViewChannel : Permission(1L shl 10)

    /** Allows for sending messages in a channel (does not allow sending messages in threads). */
    public object SendMessages : Permission(1L shl 11)

    /** Allows for sending of `/tts` messages. */
    public object SendTTSMessages : Permission(1L shl 12)

    /** Allows for deletion of other users messages. */
    public object ManageMessages : Permission(1L shl 13)

    /** Links sent by users with this permission will be auto-embedded. */
    public object EmbedLinks : Permission(1L shl 14)

    /** Allows for uploading images and files. */
    public object AttachFiles : Permission(1L shl 15)

    /** Allows for reading of message history. */
    public object ReadMessageHistory : Permission(1L shl 16)

    /**
     * Allows for using the `@everyone` tag to notify all users in a channel, and the `@here` tag to notify all online
     * users in a channel.
     */
    public object MentionEveryone : Permission(1L shl 17)

    /** Allows the usage of custom emojis from other servers. */
    public object UseExternalEmojis : Permission(1L shl 18)

    /** Allows for viewing guild insights. */
    public object ViewGuildInsights : Permission(1L shl 19)

    /** Allows for joining of a voice channel. */
    public object Connect : Permission(1L shl 20)

    /** Allows for speaking in a voice channel. */
    public object Speak : Permission(1L shl 21)

    /** Allows for muting members in a voice channel. */
    public object MuteMembers : Permission(1L shl 22)

    /** Allows for deafening of members in a voice channel. */
    public object DeafenMembers : Permission(1L shl 23)

    /** Allows for moving of members between voice channels. */
    public object MoveMembers : Permission(1L shl 24)

    /** Allows for using voice-activity-detection in a voice channel. */
    public object UseVAD : Permission(1L shl 25)

    /** Allows for modification of own nickname. */
    public object ChangeNickname : Permission(1L shl 26)

    /** Allows for modification of other users nicknames. */
    public object ManageNicknames : Permission(1L shl 27)

    /** Allows management and editing of roles. */
    public object ManageRoles : Permission(1L shl 28)

    /** Allows management and editing of webhooks. */
    public object ManageWebhooks : Permission(1L shl 29)

    /** Allows management and editing of emojis and stickers. */
    public object ManageEmojisAndStickers : Permission(1L shl 30)

    /** Allows members to use application commands, including slash commands and context menu commands. */
    public object UseApplicationCommands : Permission(1L shl 31)

    /**
     * Allows for requesting to speak in stage channels.
     *
     * _This permission is under active development and may be changed or removed._
     */
    public object RequestToSpeak : Permission(1L shl 32)

    /** Allows for creating, editing, and deleting scheduled events. */
    public object ManageEvents : Permission(1L shl 33)

    /** Allows for deleting and archiving threads, and viewing all private threads. */
    public object ManageThreads : Permission(1L shl 34)

    /** Allows for creating public and announcement threads. */
    public object CreatePublicThreads : Permission(1L shl 35)

    /** Allows for creating private threads. */
    public object CreatePrivateThreads : Permission(1L shl 36)

    /** Allows the usage of custom stickers from other servers. */
    public object UseExternalStickers : Permission(1L shl 37)

    /** Allows for sending messages in threads. */
    public object SendMessagesInThreads : Permission(1L shl 38)

    /**
     * Allows for using Activities (applications with the [Embedded][ApplicationFlag.Embedded] flag) in a voice channel.
     */
    public object UseEmbeddedActivities : Permission(1L shl 39)

    /**
     * Allows for timing out users to prevent them from sending or reacting to messages in chat and threads, and from
     * speaking in voice and stage channels.
     */
    public object ModerateMembers : Permission(1L shl 40)


    /** All [Permission]s combined into one. */
    public object All : Permission(buildAll())


    public companion object {

        @Deprecated(
            "Renamed to 'ManageEmojisAndStickers'.",
            ReplaceWith("Permission.ManageEmojisAndStickers", "dev.kord.common.entity.Permission"),
            DeprecationLevel.ERROR,
        )
        public val ManageEmojis: ManageEmojisAndStickers
            get() = ManageEmojisAndStickers

        @Deprecated(
            "Renamed to 'UseApplicationCommands'.",
            ReplaceWith("Permission.UseApplicationCommands", "dev.kord.common.entity.Permission"),
            DeprecationLevel.ERROR,
        )
        public val UseSlashCommands: UseApplicationCommands
            get() = UseApplicationCommands

        // We cannot inline this into the "All" object, because that causes a weird compiler error
        private fun buildAll(): DiscordBitSet = values.fold(EmptyBitSet()) { acc, value -> acc + value.code }

        public val values: Set<Permission>
            get() = setOf(
                CreateInstantInvite,
                KickMembers,
                BanMembers,
                Administrator,
                ManageChannels,
                ManageGuild,
                AddReactions,
                ViewAuditLog,
                PrioritySpeaker,
                Stream,
                ViewChannel,
                SendMessages,
                SendTTSMessages,
                ManageMessages,
                EmbedLinks,
                AttachFiles,
                ReadMessageHistory,
                MentionEveryone,
                UseExternalEmojis,
                ViewGuildInsights,
                Connect,
                Speak,
                MuteMembers,
                DeafenMembers,
                MoveMembers,
                UseVAD,
                ChangeNickname,
                ManageNicknames,
                ManageRoles,
                ManageWebhooks,
                ManageEmojisAndStickers,
                UseApplicationCommands,
                RequestToSpeak,
                ManageEvents,
                ManageThreads,
                CreatePublicThreads,
                CreatePrivateThreads,
                UseExternalStickers,
                SendMessagesInThreads,
                UseEmbeddedActivities,
                ModerateMembers
            )
    }
}
