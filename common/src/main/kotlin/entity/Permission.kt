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
     *  Returns this [Permissions] as a [Set] of [Permission]
     */
    val values: Set<Permission> = Permission.values.filter { it.code in code }.toSet()

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


public sealed class Permission(public val code: DiscordBitSet) {
    protected constructor(vararg values: Long) : this(DiscordBitSet(values))

    public object CreateInstantInvite : Permission(1L shl 0)
    public object KickMembers : Permission(1L shl 1)
    public object BanMembers : Permission(1L shl 2)
    public object Administrator : Permission(1L shl 3)
    public object ManageChannels : Permission(1L shl 4)
    public object ManageGuild : Permission(1L shl 5)
    public object AddReactions : Permission(1L shl 6)
    public object ViewAuditLog : Permission(1L shl 7)
    public object PrioritySpeaker : Permission(1L shl 8)
    public object Stream : Permission(1L shl 9)
    public object ViewChannel : Permission(1L shl 10)
    public object SendMessages : Permission(1L shl 11)
    public object SendTTSMessages : Permission(1L shl 12)
    public object ManageMessages : Permission(1L shl 13)
    public object EmbedLinks : Permission(1L shl 14)
    public object AttachFiles : Permission(1L shl 15)
    public object ReadMessageHistory : Permission(1L shl 16)
    public object MentionEveryone : Permission(1L shl 17)
    public object UseExternalEmojis : Permission(1L shl 18)
    public object ViewGuildInsights : Permission(1L shl 19)
    public object Connect : Permission(1L shl 20)
    public object Speak : Permission(1L shl 21)
    public object MuteMembers : Permission(1L shl 22)
    public object DeafenMembers : Permission(1L shl 23)
    public object MoveMembers : Permission(1L shl 24)
    public object UseVAD : Permission(1L shl 25)
    public object ChangeNickname : Permission(1L shl 26)
    public object ManageNicknames : Permission(1L shl 27)
    public object ManageRoles : Permission(1L shl 28)
    public object ManageWebhooks : Permission(1L shl 29)
    public object ManageEmojisAndStickers : Permission(1L shl 30)
    public object UseApplicationCommands : Permission(1L shl 31)
    public object RequestToSpeak : Permission(1L shl 32)
    public object ManageEvents : Permission(1L shl 33)
    public object ManageThreads : Permission(1L shl 34)
    public object CreatePublicThreads : Permission(1L shl 35)
    public object CreatePrivateThreads : Permission(1L shl 36)
    public object UseExternalStickers : Permission(1L shl 37)
    public object SendMessagesInThreads : Permission(1L shl 38)
    public object UseEmbeddedActivities : Permission(1L shl 39)
    public object ModerateMembers : Permission(1L shl 40)
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
