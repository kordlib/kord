@file:Suppress(names = arrayOf("IncorrectFormatting", "ReplaceArrayOfWithLiteral",
                "SpellCheckingInspection", "GrazieInspection"))

package dev.kord.common.entity

import dev.kord.common.DiscordBitSet
import dev.kord.common.EmptyBitSet
import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import kotlin.jvm.JvmField
import kotlin.jvm.JvmName
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * See [Permission]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/topics/permissions).
 */
public sealed class Permission {
    private val isProper: Boolean
    private val _shift: Int // only use if isProper == true
    private val _code: DiscordBitSet? // only non-null if isProper == false

    private constructor(shift: Int) {
        require(shift >= 0) { """shift has to be >= 0 but was $shift""" }
        isProper = true
        _shift = shift
        _code = null
    }

    private constructor(code: DiscordBitSet) {
        var singleBitIsSet = false
        var shift = -1
        for (i in 0..<code.size) {
            if (code[i]) {
                if (singleBitIsSet) {
                    singleBitIsSet = false
                    break
                } else {
                    singleBitIsSet = true
                    shift = i
                }
            }
        }
        if (singleBitIsSet) {
            isProper = true
            _shift = shift.also { if (it < 0) throw AssertionError() }
            _code = null
        } else {
            isProper = false
            _shift = -1
            _code = code
        }
    }

    /**
     * The position of the bit that is set in this [Permission]. This is always >= 0.
     */
    public val shift: Int
        get() = if (isProper) _shift else throw IllegalArgumentException(
            "$this is not a proper instance of 'Permission' because multiple bits are set."
        )

    /**
     * The raw code used by Discord.
     */
    public val code: DiscordBitSet
        get() = if (isProper) EmptyBitSet().also { it[_shift] = true } else _code!!

    /**
     * Returns an instance of [Permissions] that has all bits set that are set in `this` and [flag].
     */
    public operator fun plus(flag: Permission): Permissions = Permissions(this.code + flag.code, null)

    /**
     * Returns an instance of [Permissions] that has all bits set that are set in `this` and
     * [flags].
     */
    public operator fun plus(flags: Permissions): Permissions = Permissions(this.code + flags.code, null)

    final override fun equals(other: Any?): Boolean = this === other ||
            (other is Permission
                && (this.isProper == other.isProper)
                && (if (isProper) this._shift == other._shift else this._code == other._code))

    final override fun hashCode(): Int = if (isProper) _shift.hashCode() else _code.hashCode()

    final override fun toString(): String = if (this is Unknown)
        (if (isProper) "Permission.Unknown(shift=$_shift)" else "Permission.Unknown(code=$_code)")
            else "Permission.${this::class.simpleName}"

    /**
     * An unknown [Permission].
     *
     * This is used as a fallback for [Permission]s that haven't been added to Kord yet.
     */
    public class Unknown : Permission {
        internal constructor(shift: Int) : super(shift)

        // TODO uncomment annotation in Permissions.kt and delete this file when these constructors are removed after
        //  deprecation cycle
        @Deprecated(
            "Construct an unknown 'Permission' with 'Permission.fromShift()' instead.",
            ReplaceWith("Permission.fromShift(TODO())", imports = ["dev.kord.common.entity.Permission"]),
            DeprecationLevel.HIDDEN,
        )
        public constructor(code: DiscordBitSet) : super(code)

        @Deprecated(
            "Construct an unknown 'Permission' with 'Permission.fromShift()' instead.",
            ReplaceWith("Permission.fromShift(TODO())", imports = ["dev.kord.common.entity.Permission"]),
            DeprecationLevel.HIDDEN,
        )
        public constructor(vararg values: Long) : super(DiscordBitSet(values))
    }

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

    // TODO uncomment annotation in Permissions.kt and delete this file when this object is removed after deprecation
    //  cycle
    @Deprecated(
        "'Permission.All' is not a proper 'Permission' instance. Replace with 'Permissions.ALL'.",
        ReplaceWith("Permissions.ALL", imports = ["dev.kord.common.entity.Permissions", "dev.kord.common.entity.ALL"]),
        DeprecationLevel.HIDDEN,
    )
    public object All : Permission(Permissions.ALL.code)

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

        // TODO uncomment annotation in Permissions.kt and delete this file when this property is removed after
        //  deprecation cycle
        @Deprecated(
            "Renamed to 'entries'.",
            ReplaceWith("Permission.entries", imports = ["dev.kord.common.entity.Permission"]),
            DeprecationLevel.HIDDEN,
        )
        public val values: Set<Permission> get() = entries.toSet()

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
@Serializable(with = Permissions.Serializer::class)
public class Permissions internal constructor(
    /**
     * The raw code used by Discord.
     */
    public val code: DiscordBitSet,
    @Suppress("UNUSED_PARAMETER") unused: Nothing?,
) {
    // TODO uncomment annotation in Permissions.kt and delete this file when this constructor is removed after
    //  deprecation cycle
    @Deprecated(
        "Don't construct an instance of 'Permissions' from a raw code. Use the factory functions described in the " +
            "documentation instead.",
        ReplaceWith("Permissions.Builder(code).build()", "dev.kord.common.entity.Permissions"),
        DeprecationLevel.HIDDEN,
    )
    public constructor(code: DiscordBitSet) : this(code, null)

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
    public operator fun plus(flag: Permission): Permissions = Permissions(this.code + flag.code, null)

    /**
     * Returns an instance of [Permissions] that has all bits set that are set in `this` and
     * [flags].
     */
    public operator fun plus(flags: Permissions): Permissions = Permissions(this.code + flags.code, null)

    /**
     * Returns an instance of [Permissions] that has all bits set that are set in `this` except the
     * bits that are set in [flag].
     */
    public operator fun minus(flag: Permission): Permissions = Permissions(this.code - flag.code, null)

    /**
     * Returns an instance of [Permissions] that has all bits set that are set in `this` except the
     * bits that are set in [flags].
     */
    public operator fun minus(flags: Permissions): Permissions = Permissions(this.code - flags.code, null)

    /**
     * Returns a copy of this instance of [Permissions] modified with [builder].
     */
    @JvmName("copy0") // TODO other name when deprecated overload is removed
    public inline fun copy(builder: Builder.() -> Unit): Permissions {
        contract { callsInPlace(builder, EXACTLY_ONCE) }
        return Builder(code.copy()).apply(builder).build()
    }

    @Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE", "DEPRECATION_ERROR")
    @Deprecated(
        "'Permissions.PermissionsBuilder' is deprecated, use 'Permissions.Builder' instead.",
        level = DeprecationLevel.HIDDEN,
    )
    @kotlin.internal.LowPriorityInOverloadResolution
    public inline fun copy(block: PermissionsBuilder.() -> Unit): Permissions {
        contract { callsInPlace(block, EXACTLY_ONCE) }
        return PermissionsBuilder(code.copy()).apply(block).permissions()
    }

    override fun equals(other: Any?): Boolean = this === other ||
            (other is Permissions && this.code == other.code)

    override fun hashCode(): Int = code.hashCode()

    override fun toString(): String = "Permissions(values=$values)"

    /**
     * @suppress
     */
    @Deprecated(
        message = "Permissions is no longer a data class.",
        replaceWith = ReplaceWith(expression = "this.code", imports = arrayOf()),
        DeprecationLevel.HIDDEN,
    )
    public operator fun component1(): DiscordBitSet = code

    /**
     * @suppress
     */
    @Suppress(names = arrayOf("DeprecatedCallableAddReplaceWith"))
    @Deprecated(message =
            "Permissions is no longer a data class. Deprecated without a replacement.", level = DeprecationLevel.HIDDEN)
    public fun copy(code: DiscordBitSet = this.code): Permissions = Permissions(code, null)

    @Deprecated(
        "Renamed to 'Builder'.",
        ReplaceWith("Permissions.Builder", imports = ["dev.kord.common.entity.Permissions"]),
        DeprecationLevel.HIDDEN,
    )
    public class PermissionsBuilder(code: DiscordBitSet) {
        private val delegate = Builder(code)
        public operator fun Permissions.unaryPlus(): Unit = with(delegate) { unaryPlus() }
        public operator fun Permissions.unaryMinus(): Unit = with(delegate) { unaryMinus() }
        public operator fun Permission.unaryPlus(): Unit = with(delegate) { unaryPlus() }
        public operator fun Permission.unaryMinus(): Unit = with(delegate) { unaryMinus() }
        public fun permissions(): Permissions = delegate.build()
    }

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
        public fun build(): Permissions = Permissions(code.copy(), null)
    }

    internal object Serializer : KSerializer<Permissions> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.Permissions",
                PrimitiveKind.STRING)

        private val `delegate`: KSerializer<DiscordBitSet> = DiscordBitSet.serializer()

        override fun serialize(encoder: Encoder, `value`: Permissions) {
            encoder.encodeSerializableValue(delegate, value.code)
        }

        override fun deserialize(decoder: Decoder): Permissions =
                Permissions(decoder.decodeSerializableValue(delegate), null)
    }

    public companion object NewCompanion {

        @Suppress("DEPRECATION_ERROR")
        @Deprecated(
            "Renamed to 'NewCompanion', which no longer implements 'KSerializer<Permissions>'.",
            ReplaceWith("Permissions.serializer()", imports = ["dev.kord.common.entity.Permissions"]),
            DeprecationLevel.HIDDEN,
        )
        @JvmField
        public val Companion: Companion = Companion()
    }

    @Deprecated(
        "Renamed to 'NewCompanion', which no longer implements 'KSerializer<Permissions>'.",
        ReplaceWith("Permissions.serializer()", imports = ["dev.kord.common.entity.Permissions"]),
        DeprecationLevel.HIDDEN,
    )
    public class Companion internal constructor() : KSerializer<Permissions> by Serializer {
        public fun serializer(): KSerializer<Permissions> = this
    }
}

/**
 * Returns an instance of [Permissions] built with [Permissions.Builder].
 */
@JvmName("Permissions0") // TODO other name when deprecated overload is removed
public inline fun Permissions(builder: Permissions.Builder.() -> Unit = {}): Permissions {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    return Permissions.Builder().apply(builder).build()
}

@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE", "DEPRECATION_ERROR")
@Deprecated(
    "'Permissions.PermissionsBuilder' is deprecated, use 'Permissions.Builder' instead.",
    level = DeprecationLevel.HIDDEN,
)
@kotlin.internal.LowPriorityInOverloadResolution
public inline fun Permissions(block: Permissions.PermissionsBuilder.() -> Unit = {}): Permissions {
    contract { callsInPlace(block, EXACTLY_ONCE) }
    return Permissions.PermissionsBuilder(DiscordBitSet(0)).apply(block).permissions()
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
public fun Permissions(vararg flags: Permissions): Permissions = Permissions {
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

// TODO uncomment annotation in Permissions.kt and delete this file when these functions are removed after deprecation
//  cycle
@Suppress("FunctionName")
@Deprecated("Binary compatibility, keep for some releases.", level = DeprecationLevel.HIDDEN)
public fun PermissionWithIterable(flags: Iterable<Permissions>): Permissions = Permissions(flags)

@Deprecated(
    "Don't construct an instance of 'Permissions' from a raw value. Use the factory functions described in the " +
        "documentation instead.",
    ReplaceWith(
        "Permissions.Builder(DiscordBitSet(value)).build()",
        imports = ["dev.kord.common.entity.Permissions", "dev.kord.common.DiscordBitSet"],
    ),
    DeprecationLevel.HIDDEN,
)
public fun Permissions(value: String): Permissions = Permissions(DiscordBitSet(value), null)
