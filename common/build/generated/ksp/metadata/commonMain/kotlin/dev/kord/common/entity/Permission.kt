// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("IncorrectFormatting", "ReplaceArrayOfWithLiteral",
                "SpellCheckingInspection", "GrazieInspection", "MemberVisibilityCanBePrivate"))

package dev.kord.common.entity

import dev.kord.common.DiscordBitSet
import dev.kord.common.EmptyBitSet
import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import kotlin.jvm.JvmField
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmName
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

/**
 * See [Permission]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/topics/permissions).
 */
public sealed class Permission(
    /**
     * The position of the bit that is set in this [Permission]. This is always >= 0.
     */
    public val shift: Int,
) {
    init {
        require(shift >= 0) { """shift has to be >= 0 but was $shift""" }
    }

    /**
     * The raw code used by Discord.
     */
    public val code: DiscordBitSet
        get() = EmptyBitSet().also { it[shift] = true }

    /**
     * Returns an instance of [Permissions] that has all bits set that are set in `this` and [flag].
     */
    public operator fun plus(flag: Permission): Permissions = Permissions(this.code + flag.code)

    /**
     * Returns an instance of [Permissions] that has all bits set that are set in `this` and
     * [flags].
     */
    public operator fun plus(flags: Permissions): Permissions = Permissions(this.code + flags.code)

    final override fun equals(other: Any?): Boolean = this === other ||
            (other is Permission && this.shift == other.shift)

    final override fun hashCode(): Int = shift.hashCode()

    final override fun toString(): String = if (this is Unknown) "Permission.Unknown(shift=$shift)"
            else "Permission.${this::class.simpleName}"

    /**
     * An unknown [Permission].
     *
     * This is used as a fallback for [Permission]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        shift: Int,
    ) : Permission(shift)

    /**
     * Allows creation of instant invites.
     */
    public object CreateInstantInvite : Permission(0)

    /**
     * Allows kicking members.
     */
    public object KickMembers : Permission(1)

    /**
     * Allows banning members.
     */
    public object BanMembers : Permission(2)

    /**
     * Allows all permissions and bypasses channel permission overwrites.
     */
    public object Administrator : Permission(3)

    /**
     * Allows management and editing of channels.
     */
    public object ManageChannels : Permission(4)

    /**
     * Allows management and editing of the guild.
     */
    public object ManageGuild : Permission(5)

    /**
     * Allows for the addition of reactions to messages.
     */
    public object AddReactions : Permission(6)

    /**
     * Allows for viewing of audit logs.
     */
    public object ViewAuditLog : Permission(7)

    /**
     * Allows for using priority speaker in a voice channel.
     */
    public object PrioritySpeaker : Permission(8)

    /**
     * Allows the user to go live.
     */
    public object Stream : Permission(9)

    /**
     * Allows guild members to view a channel, which includes reading messages in text channels and
     * joining voice channels.
     */
    public object ViewChannel : Permission(10)

    /**
     * Allows for sending messages in a channel and creating threads in a forum (does not allow
     * sending messages in threads).
     */
    public object SendMessages : Permission(11)

    /**
     * Allows for sending of `/tts` messages.
     */
    public object SendTTSMessages : Permission(12)

    /**
     * Allows for deletion of other users' messages.
     */
    public object ManageMessages : Permission(13)

    /**
     * Links sent by users with this permission will be auto-embedded.
     */
    public object EmbedLinks : Permission(14)

    /**
     * Allows for uploading images and files.
     */
    public object AttachFiles : Permission(15)

    /**
     * Allows for reading of message history.
     */
    public object ReadMessageHistory : Permission(16)

    /**
     * Allows for using the `@everyone` tag to notify all users in a channel, and the `@here` tag to
     * notify all online users in a channel.
     */
    public object MentionEveryone : Permission(17)

    /**
     * Allows the usage of custom emojis from other servers.
     */
    public object UseExternalEmojis : Permission(18)

    /**
     * Allows for viewing guild insights.
     */
    public object ViewGuildInsights : Permission(19)

    /**
     * Allows for joining of a voice channel.
     */
    public object Connect : Permission(20)

    /**
     * Allows for speaking in a voice channel.
     */
    public object Speak : Permission(21)

    /**
     * Allows for muting members in a voice channel.
     */
    public object MuteMembers : Permission(22)

    /**
     * Allows for deafening of members in a voice channel.
     */
    public object DeafenMembers : Permission(23)

    /**
     * Allows for moving of members between voice channels.
     */
    public object MoveMembers : Permission(24)

    /**
     * Allows for using voice-activity-detection in a voice channel.
     */
    public object UseVAD : Permission(25)

    /**
     * Allows for modification of own nickname.
     */
    public object ChangeNickname : Permission(26)

    /**
     * Allows for modification of other users' nicknames.
     */
    public object ManageNicknames : Permission(27)

    /**
     * Allows management and editing of roles.
     */
    public object ManageRoles : Permission(28)

    /**
     * Allows management and editing of webhooks.
     */
    public object ManageWebhooks : Permission(29)

    /**
     * Allows for editing and deleting emojis, stickers, and soundboard sounds created by all users.
     */
    public object ManageGuildExpressions : Permission(30)

    /**
     * Allows members to use application commands, including slash commands and context menu
     * commands.
     */
    public object UseApplicationCommands : Permission(31)

    /**
     * Allows for requesting to speak in stage channels.
     *
     * _This permission is under active development and may be changed or removed._
     */
    public object RequestToSpeak : Permission(32)

    /**
     * Allows for editing and deleting scheduled events created by all users.
     */
    public object ManageEvents : Permission(33)

    /**
     * Allows for deleting and archiving threads, and viewing all private threads.
     */
    public object ManageThreads : Permission(34)

    /**
     * Allows for creating public and announcement threads.
     */
    public object CreatePublicThreads : Permission(35)

    /**
     * Allows for creating private threads.
     */
    public object CreatePrivateThreads : Permission(36)

    /**
     * Allows the usage of custom stickers from other servers.
     */
    public object UseExternalStickers : Permission(37)

    /**
     * Allows for sending messages in threads.
     */
    public object SendMessagesInThreads : Permission(38)

    /**
     * Allows for using Activities (applications with the [Embedded][ApplicationFlag.Embedded] flag)
     * in a voice channel.
     */
    public object UseEmbeddedActivities : Permission(39)

    /**
     * Allows for timing out users to prevent them from sending or reacting to messages in chat and
     * threads, and from speaking in voice and stage channels.
     */
    public object ModerateMembers : Permission(40)

    /**
     * Allows for viewing role subscription insights.
     */
    public object ViewCreatorMonetizationAnalytics : Permission(41)

    /**
     * Allows for using soundboard in a voice channel.
     */
    public object UseSoundboard : Permission(42)

    /**
     * Allows for creating emojis, stickers, and soundboard sounds, and editing and deleting those
     * created by the current user.
     */
    public object CreateGuildExpressions : Permission(43)

    /**
     * Allows for creating scheduled events, and editing and deleting those created by the current
     * user.
     */
    public object CreateEvents : Permission(44)

    /**
     * Allows the usage of custom soundboard sounds from other servers.
     */
    public object UseExternalSounds : Permission(45)

    /**
     * Allows sending voice messages.
     */
    public object SendVoiceMessages : Permission(46)

    public companion object {
        /**
         * A [List] of all known [Permission]s.
         */
        public val entries: List<Permission> by lazy(mode = PUBLICATION) {
            listOf(
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
                ManageGuildExpressions,
                UseApplicationCommands,
                RequestToSpeak,
                ManageEvents,
                ManageThreads,
                CreatePublicThreads,
                CreatePrivateThreads,
                UseExternalStickers,
                SendMessagesInThreads,
                UseEmbeddedActivities,
                ModerateMembers,
                ViewCreatorMonetizationAnalytics,
                UseSoundboard,
                CreateGuildExpressions,
                CreateEvents,
                UseExternalSounds,
                SendVoiceMessages,
            )
        }


        /**
         * Returns an instance of [Permission] with [Permission.shift] equal to the specified
         * [shift].
         *
         * @throws IllegalArgumentException if [shift] is not >= 0.
         */
        public fun fromShift(shift: Int): Permission = when (shift) {
            0 -> CreateInstantInvite
            1 -> KickMembers
            2 -> BanMembers
            3 -> Administrator
            4 -> ManageChannels
            5 -> ManageGuild
            6 -> AddReactions
            7 -> ViewAuditLog
            8 -> PrioritySpeaker
            9 -> Stream
            10 -> ViewChannel
            11 -> SendMessages
            12 -> SendTTSMessages
            13 -> ManageMessages
            14 -> EmbedLinks
            15 -> AttachFiles
            16 -> ReadMessageHistory
            17 -> MentionEveryone
            18 -> UseExternalEmojis
            19 -> ViewGuildInsights
            20 -> Connect
            21 -> Speak
            22 -> MuteMembers
            23 -> DeafenMembers
            24 -> MoveMembers
            25 -> UseVAD
            26 -> ChangeNickname
            27 -> ManageNicknames
            28 -> ManageRoles
            29 -> ManageWebhooks
            30 -> ManageGuildExpressions
            31 -> UseApplicationCommands
            32 -> RequestToSpeak
            33 -> ManageEvents
            34 -> ManageThreads
            35 -> CreatePublicThreads
            36 -> CreatePrivateThreads
            37 -> UseExternalStickers
            38 -> SendMessagesInThreads
            39 -> UseEmbeddedActivities
            40 -> ModerateMembers
            41 -> ViewCreatorMonetizationAnalytics
            42 -> UseSoundboard
            43 -> CreateGuildExpressions
            44 -> CreateEvents
            45 -> UseExternalSounds
            46 -> SendVoiceMessages
            else -> Unknown(shift)
        }
    }
}

/**
 * A collection of multiple [Permission]s.
 *
 * ## Creating an instance of [Permissions]
 *
 * You can create an instance of [Permissions] using the following methods:
 * ```kotlin
 * // from individual Permissions
 * val permissions1 = Permissions(Permission.CreateInstantInvite, Permission.KickMembers)
 *
 * // from an Iterable
 * val iterable: Iterable<Permission> = TODO()
 * val permissions2 = Permissions(iterable)
 *
 * // using a builder
 * val permissions3 = Permissions {
 *     +permissions2
 *     +Permission.CreateInstantInvite
 *     -Permission.KickMembers
 * }
 * ```
 *
 * ## Modifying an existing instance of [Permissions]
 *
 * You can create a modified copy of an existing instance of [Permissions] using the [copy] method:
 * ```kotlin
 * permissions.copy {
 *     +Permission.CreateInstantInvite
 * }
 * ```
 *
 * ## Mathematical operators
 *
 * All [Permissions] objects can use `+`/`-` operators:
 * ```kotlin
 * val permissions1 = permissions + Permission.CreateInstantInvite
 * val permissions2 = permissions - Permission.KickMembers
 * val permissions3 = permissions1 + permissions2
 * ```
 *
 * ## Checking for [Permission]s
 *
 * You can use the [contains] operator to check whether an instance of [Permissions] contains
 * specific [Permission]s:
 * ```kotlin
 * val hasPermission = Permission.CreateInstantInvite in permissions
 * val hasPermissions = Permissions(Permission.CreateInstantInvite, Permission.KickMembers) in permissions
 * ```
 *
 * ## Unknown [Permission]s
 *
 * Whenever [Permission]s haven't been added to Kord yet, they will be deserialized as instances of
 * [Permission.Unknown].
 *
 * You can also use [Permission.fromShift] to check for [unknown][Permission.Unknown] [Permission]s.
 * ```kotlin
 * val hasUnknownPermission = Permission.fromShift(23) in permissions
 * ```
 *
 * @see Permission
 * @see Permissions.Builder
 */
@JvmInline
@Serializable
public value class Permissions internal constructor(
    /**
     * The raw code used by Discord.
     */
    public val code: DiscordBitSet,
) {
    /**
     * A [Set] of all [Permission]s contained in this instance of [Permissions].
     */
    public val values: Set<Permission>
        get() = buildSet {
            for (shift in 0..<code.size) {
                if (code[shift]) add(Permission.fromShift(shift))
            }
        }

    /**
     * Checks if this instance of [Permissions] has all bits set that are set in [flag].
     */
    public operator fun contains(flag: Permission): Boolean = flag.code in this.code

    /**
     * Checks if this instance of [Permissions] has all bits set that are set in [flags].
     */
    public operator fun contains(flags: Permissions): Boolean = flags.code in this.code

    /**
     * Returns an instance of [Permissions] that has all bits set that are set in `this` and [flag].
     */
    public operator fun plus(flag: Permission): Permissions = Permissions(this.code + flag.code)

    /**
     * Returns an instance of [Permissions] that has all bits set that are set in `this` and
     * [flags].
     */
    public operator fun plus(flags: Permissions): Permissions = Permissions(this.code + flags.code)

    /**
     * Returns an instance of [Permissions] that has all bits set that are set in `this` except the
     * bits that are set in [flag].
     */
    public operator fun minus(flag: Permission): Permissions = Permissions(this.code - flag.code)

    /**
     * Returns an instance of [Permissions] that has all bits set that are set in `this` except the
     * bits that are set in [flags].
     */
    public operator fun minus(flags: Permissions): Permissions = Permissions(this.code - flags.code)

    /**
     * Returns a copy of this instance of [Permissions] modified with [builder].
     */
    public inline fun copy(builder: Builder.() -> Unit): Permissions {
        contract { callsInPlace(builder, EXACTLY_ONCE) }
        return Builder(code.copy()).apply(builder).build()
    }

    @Deprecated(
        level = DeprecationLevel.HIDDEN,
        message = "Binary compatibility, keep for some releases.",
    )
    public inline fun copy0(builder: Builder.() -> Unit): Permissions {
        contract { callsInPlace(builder, EXACTLY_ONCE) }
        return copy(builder)
    }

    override fun toString(): String = "Permissions(values=$values)"

    public class Builder(
        private val code: DiscordBitSet = EmptyBitSet(),
    ) {
        /**
         * Sets all bits in the [Builder] that are set in this [Permission].
         */
        public operator fun Permission.unaryPlus() {
            this@Builder.code.add(this.code)
        }

        /**
         * Sets all bits in the [Builder] that are set in this [Permissions].
         */
        public operator fun Permissions.unaryPlus() {
            this@Builder.code.add(this.code)
        }

        /**
         * Unsets all bits in the [Builder] that are set in this [Permission].
         */
        public operator fun Permission.unaryMinus() {
            this@Builder.code.remove(this.code)
        }

        /**
         * Unsets all bits in the [Builder] that are set in this [Permissions].
         */
        public operator fun Permissions.unaryMinus() {
            this@Builder.code.remove(this.code)
        }

        /**
         * Returns an instance of [Permissions] that has all bits set that are currently set in this
         * [Builder].
         */
        public fun build(): Permissions = Permissions(code.copy())
    }

    public companion object {
        @Suppress(names = arrayOf("DEPRECATION_ERROR"))
        @Deprecated(
            level = DeprecationLevel.ERROR,
            message = "Renamed to 'Companion'.",
            replaceWith = ReplaceWith(expression = "Permissions.Companion", imports =
                        arrayOf("dev.kord.common.entity.Permissions")),
        )
        @JvmField
        public val NewCompanion: NewCompanion = NewCompanion()
    }

    @Deprecated(
        level = DeprecationLevel.ERROR,
        message = "Renamed to 'Companion'.",
        replaceWith = ReplaceWith(expression = "Permissions.Companion", imports =
                    arrayOf("dev.kord.common.entity.Permissions")),
    )
    public class NewCompanion internal constructor() {
        public fun serializer(): KSerializer<Permissions> = Permissions.serializer()
    }
}

/**
 * Returns an instance of [Permissions] built with [Permissions.Builder].
 */
public inline fun Permissions(builder: Permissions.Builder.() -> Unit = {}): Permissions {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    return Permissions.Builder().apply(builder).build()
}

@Suppress(names = arrayOf("FunctionName"))
@Deprecated(
    level = DeprecationLevel.HIDDEN,
    message = "Binary compatibility, keep for some releases.",
)
public inline fun Permissions0(builder: Permissions.Builder.() -> Unit = {}): Permissions {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    return Permissions(builder)
}

/**
 * Returns an instance of [Permissions] that has all bits set that are set in any element of
 * [flags].
 */
public fun Permissions(vararg flags: Permission): Permissions = Permissions {
    flags.forEach { +it }
}

/**
 * Returns an instance of [Permissions] that has all bits set that are set in any element of
 * [flags].
 */
public fun Permissions(flags: Iterable<Permission>): Permissions = Permissions {
    flags.forEach { +it }
}

/**
 * Returns an instance of [Permissions] that has all bits set that are set in any element of
 * [flags].
 */
@JvmName("Permissions0")
public fun Permissions(flags: Iterable<Permissions>): Permissions = Permissions {
    flags.forEach { +it }
}
