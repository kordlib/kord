// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("RedundantVisibilityModifier", "IncorrectFormatting",
                "ReplaceArrayOfWithLiteral", "SpellCheckingInspection", "GrazieInspection",
                "RedundantUnitReturnType"))

package dev.kord.common.entity

import dev.kord.common.DiscordBitSet
import dev.kord.common.EmptyBitSet
import dev.kord.common.`annotation`.KordUnsafe
import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import kotlin.jvm.JvmName
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Convenience container of multiple [Permissions][Permission] which can be combined into one.
 *
 * ## Creating a collection of message flags
 * You can create an [Permissions] object using the following methods
 * ```kotlin
 * // From flags
 * val flags1 = Permissions(Permission.CreateInstantInvite, Permission.KickMembers)
 * // From an iterable
 * val flags2 = Permissions(listOf(Permission.CreateInstantInvite, Permission.KickMembers))
 * // Using a builder
 * val flags3 = Permissions {
 *  +Permission.CreateInstantInvite
 *  -Permission.KickMembers
 * }
 * ```
 *
 * ## Modifying existing permissions
 * You can crate a modified copy of a [Permissions] instance using the [copy] method
 *
 * ```kotlin
 * flags.copy {
 *  +Permission.CreateInstantInvite
 * }
 * ```
 *
 * ## Mathematical operators
 * All [Permissions] objects can use +/- operators
 *
 * ```kotlin
 * val flags = Permissions(Permission.CreateInstantInvite)
 * val flags2 = flags + Permission.KickMembers
 * val otherFlags = flags - Permission.KickMembers
 * val flags3 = flags + otherFlags
 * ```
 *
 * ## Checking for a permission
 * You can use the [contains] operator to check whether a collection contains a specific flag
 * ```kotlin
 * val hasFlag = Permission.CreateInstantInvite in member.permissions
 * val hasFlags = Permission(Permission.KickMembers, Permission.KickMembers) in member.permissions
 * ```
 *
 * ## Unknown permission
 *
 * Whenever a newly added flag has not been added to Kord yet it will get deserialized as
 * [Permission.Unknown].
 * You can also use that to check for an yet unsupported flag
 * ```kotlin
 * val hasFlags = Permission.Unknown(1 shl 69) in member.permissions
 * ```
 * @see Permission
 * @see Permissions.Builder
 * @property code numeric value of all [Permissions]s
 */
@Serializable(with = Permissions.Serializer::class)
public class Permissions(
    public val code: DiscordBitSet = EmptyBitSet(),
) {
    public val values: Set<Permission>
        get() = Permission.entries.filter { it in this }.toSet()

    public operator fun contains(flag: Permission): Boolean = flag.code in this.code

    public operator fun contains(flags: Permissions): Boolean = flags.code in this.code

    public operator fun plus(flag: Permission): Permissions = Permissions(this.code + flag.code)

    public operator fun plus(flags: Permissions): Permissions = Permissions(this.code + flags.code)

    public operator fun minus(flag: Permission): Permissions = Permissions(this.code - flag.code)

    public operator fun minus(flags: Permissions): Permissions = Permissions(this.code - flags.code)

    public override fun equals(other: Any?): Boolean = this === other ||
            (other is Permissions && this.code == other.code)

    public override fun hashCode(): Int = code.hashCode()

    public override fun toString(): String = "Permissions(values=$values)"

    public class Builder(
        private var code: DiscordBitSet = EmptyBitSet(),
    ) {
        public operator fun Permission.unaryPlus(): Unit {
            this@Builder.code.add(this.code)
        }

        public operator fun Permissions.unaryPlus(): Unit {
            this@Builder.code.add(this.code)
        }

        public operator fun Permission.unaryMinus(): Unit {
            this@Builder.code.remove(this.code)
        }

        public operator fun Permissions.unaryMinus(): Unit {
            this@Builder.code.remove(this.code)
        }

        public fun flags(): Permissions = Permissions(code)
    }

    internal object Serializer : KSerializer<Permissions> {
        public override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.Permissions",
                PrimitiveKind.STRING)

        private val `delegate`: KSerializer<DiscordBitSet> = DiscordBitSet.serializer()

        public override fun serialize(encoder: Encoder, `value`: Permissions) =
                encoder.encodeSerializableValue(delegate, value.code)

        public override fun deserialize(decoder: Decoder) =
                Permissions(decoder.decodeSerializableValue(delegate))
    }
}

public inline fun Permissions(builder: Permissions.Builder.() -> Unit): Permissions {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    return Permissions.Builder().apply(builder).flags()
}

public fun Permissions(vararg flags: Permission): Permissions = Permissions { flags.forEach { +it }
        }

public fun Permissions(vararg flags: Permissions): Permissions = Permissions { flags.forEach { +it }
        }

public fun Permissions(flags: Iterable<Permission>): Permissions = Permissions { flags.forEach { +it
        } }

@JvmName("Permissions0")
public fun Permissions(flags: Iterable<Permissions>): Permissions = Permissions {
        flags.forEach { +it } }

public inline fun Permissions.copy(block: Permissions.Builder.() -> Unit): Permissions {
    contract { callsInPlace(block, EXACTLY_ONCE) }
    return Permissions.Builder(code).apply(block).flags()
}

/**
 * See [Permission]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/topics/permissions).
 */
public sealed class Permission(
    /**
     * The raw code used by Discord.
     */
    public val code: DiscordBitSet,
) {
    protected constructor(vararg values: Long) : this(DiscordBitSet(values))

    public final override fun equals(other: Any?): Boolean = this === other ||
            (other is Permission && this.code == other.code)

    public final override fun hashCode(): Int = code.hashCode()

    public final override fun toString(): String =
            "Permission.${this::class.simpleName}(code=$code)"

    /**
     * An unknown [Permission].
     *
     * This is used as a fallback for [Permission]s that haven't been added to Kord yet.
     */
    public class Unknown @KordUnsafe constructor(
        code: DiscordBitSet,
    ) : Permission(code) {
        @KordUnsafe
        public constructor(vararg code: Long) : this(DiscordBitSet(code))
    }

    /**
     * Allows creation of instant invites.
     */
    public object CreateInstantInvite : Permission(1L)

    /**
     * Allows kicking members.
     */
    public object KickMembers : Permission(2L)

    /**
     * Allows banning members.
     */
    public object BanMembers : Permission(4L)

    /**
     * Allows all permissions and bypasses channel permission overwrites.
     */
    public object Administrator : Permission(8L)

    /**
     * Allows management and editing of channels.
     */
    public object ManageChannels : Permission(16L)

    /**
     * Allows management and editing of the guild.
     */
    public object ManageGuild : Permission(32L)

    /**
     * Allows for the addition of reactions to messages.
     */
    public object AddReactions : Permission(64L)

    /**
     * Allows for viewing of audit logs.
     */
    public object ViewAuditLog : Permission(128L)

    /**
     * Allows for using priority speaker in a voice channel.
     */
    public object PrioritySpeaker : Permission(256L)

    /**
     * Allows the user to go live.
     */
    public object Stream : Permission(512L)

    /**
     * Allows guild members to view a channel, which includes reading messages in text channels and
     * joining voice
     * channels.
     */
    public object ViewChannel : Permission(1_024L)

    /**
     * Allows for sending messages in a channel and creating threads in a forum (does not allow
     * sending messages in threads).
     */
    public object SendMessages : Permission(2_048L)

    /**
     * Allows for sending of `/tts` messages.
     */
    public object SendTTSMessages : Permission(4_096L)

    /**
     * Allows for deletion of other users messages.
     */
    public object ManageMessages : Permission(8_192L)

    /**
     * Links sent by users with this permission will be auto-embedded.
     */
    public object EmbedLinks : Permission(16_384L)

    /**
     * Allows for uploading images and files.
     */
    public object AttachFiles : Permission(32_768L)

    /**
     * Allows for reading of message history.
     */
    public object ReadMessageHistory : Permission(65_536L)

    /**
     * Allows for using the `@everyone` tag to notify all users in a channel, and the `@here` tag to
     * notify all online
     * users in a channel.
     */
    public object MentionEveryone : Permission(131_072L)

    /**
     * Allows the usage of custom emojis from other servers.
     */
    public object UseExternalEmojis : Permission(262_144L)

    /**
     * Allows for viewing guild insights.
     */
    public object ViewGuildInsights : Permission(524_288L)

    /**
     * Allows for joining of a voice channel.
     */
    public object Connect : Permission(1_048_576L)

    /**
     * Allows for speaking in a voice channel.
     */
    public object Speak : Permission(2_097_152L)

    /**
     * Allows for muting members in a voice channel.
     */
    public object MuteMembers : Permission(4_194_304L)

    /**
     * Allows for deafening of members in a voice channel.
     */
    public object DeafenMembers : Permission(8_388_608L)

    /**
     * Allows for moving of members between voice channels.
     */
    public object MoveMembers : Permission(16_777_216L)

    /**
     * Allows for using voice-activity-detection in a voice channel.
     */
    public object UseVAD : Permission(33_554_432L)

    /**
     * Allows for modification of own nickname.
     */
    public object ChangeNickname : Permission(67_108_864L)

    /**
     * Allows for modification of other users nicknames.
     */
    public object ManageNicknames : Permission(134_217_728L)

    /**
     * Allows management and editing of roles.
     */
    public object ManageRoles : Permission(268_435_456L)

    /**
     * Allows management and editing of webhooks.
     */
    public object ManageWebhooks : Permission(536_870_912L)

    /**
     * Allows management and editing of emojis and stickers.
     */
    @Deprecated(
        level = DeprecationLevel.ERROR,
        message = "Renamed by discord",
        replaceWith = ReplaceWith(expression = "ManageGuildExpressions", imports = arrayOf()),
    )
    public object ManageEmojisAndStickers : Permission(1_073_741_824L)

    /**
     * Allows management and editing of emojis, stickers and soundboard sounds.
     */
    public object ManageGuildExpressions : Permission(1_073_741_824L)

    /**
     * Allows members to use application commands, including slash commands and context menu
     * commands.
     */
    public object UseApplicationCommands : Permission(2_147_483_648L)

    /**
     * Allows for requesting to speak in stage channels.
     *
     * is permission is under active development and may be changed or removed._
     */
    public object RequestToSpeak : Permission(4_294_967_296L)

    /**
     * Allows for creating, editing, and deleting scheduled events.
     */
    public object ManageEvents : Permission(8_589_934_592L)

    /**
     * Allows for deleting and archiving threads, and viewing all private threads.
     */
    public object ManageThreads : Permission(17_179_869_184L)

    /**
     * Allows for creating public and announcement threads.
     */
    public object CreatePublicThreads : Permission(34_359_738_368L)

    /**
     * Allows for creating private threads.
     */
    public object CreatePrivateThreads : Permission(68_719_476_736L)

    /**
     * Allows the usage of custom stickers from other servers.
     */
    public object UseExternalStickers : Permission(137_438_953_472L)

    /**
     * Allows for sending messages in threads.
     */
    public object SendMessagesInThreads : Permission(274_877_906_944L)

    /**
     * Allows for using Activities (applications with the [Embedded][ApplicationFlag.Embedded] flag)
     * in a voice channel.
     */
    public object UseEmbeddedActivities : Permission(549_755_813_888L)

    /**
     * Allows for timing out users to prevent them from sending or reacting to messages in chat and
     * threads, and from
     * speaking in voice and stage channels.
     */
    public object ModerateMembers : Permission(1_099_511_627_776L)

    /**
     * Allows for viewing role subscription insights.
     */
    public object ViewCreatorMonetizationAnalytics : Permission(2_199_023_255_552L)

    /**
     * Allows for using soundboard in a voice channel.
     */
    public object UseSoundboard : Permission(4_398_046_511_104L)

    /**
     * Allows the usage of custom soundboard sounds from other servers.
     */
    public object UseExternalSounds : Permission(35_184_372_088_832L)

    /**
     * Allows sending voice messages.
     */
    public object SendVoiceMessages : Permission(70_368_744_177_664L)

    /**
     * A combination of all [Permission]s
     */
    public object All : Permission(buildAll())

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
                UseExternalSounds,
                SendVoiceMessages,
            )
        }


        private fun buildAll(): DiscordBitSet {
            // We cannot inline this into the "All" object, because that causes a weird compiler bug
            return entries.fold(EmptyBitSet()) { acc, value ->
                acc + value.code
            }
        }
    }
}
