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

    public object CreateInstantInvite : Permission(0x00000001)
    public object KickMembers : Permission(0x00000002)
    public object BanMembers : Permission(0x00000004)
    public object Administrator : Permission(0x00000008)
    public object ManageChannels : Permission(0x00000010)
    public object ManageGuild : Permission(0x00000020)
    public object AddReactions : Permission(0x00000040)
    public object ViewAuditLog : Permission(0x00000080)
    public object Stream : Permission(0x00000200)
    public object ViewChannel : Permission(0x00000400)
    public object SendMessages : Permission(0x00000800)
    public object SendTTSMessages : Permission(0x00001000)
    public object ManageMessages : Permission(0x00002000)
    public object EmbedLinks : Permission(0x00004000)
    public object AttachFiles : Permission(0x00008000)
    public object ReadMessageHistory : Permission(0x00010000)
    public object MentionEveryone : Permission(0x00020000)
    public object UseExternalEmojis : Permission(0x00040000)
    public object ViewGuildInsights : Permission(0x00080000)
    public object Connect : Permission(0x00100000)
    public object Speak : Permission(0x00200000)
    public object MuteMembers : Permission(0x00400000)
    public object DeafenMembers : Permission(0x00800000)
    public object MoveMembers : Permission(0x01000000)
    public object UseVAD : Permission(0x02000000)
    public object PrioritySpeaker : Permission(0x00000100)
    public object ChangeNickname : Permission(0x04000000)
    public object ManageNicknames : Permission(0x08000000)
    public object ManageRoles : Permission(0x10000000)
    public object ManageWebhooks : Permission(0x20000000)
    public object ManageEmojis : Permission(0x40000000)
    public object UseSlashCommands : Permission(0x80000000)
    public object RequestToSpeak : Permission(0x100000000)
    public object ManageEvents : Permission(0x0200000000)
    public object ManageThreads : Permission(0x0400000000)
    public object CreatePublicThreads : Permission(0x0800000000)
    public object CreatePrivateThreads : Permission(0x1000000000)
    public object SendMessagesInThreads : Permission(0x4000000000)
    public object ModerateMembers : Permission(0x0000010000000000)
    public object All : Permission(buildAll())

    public companion object {
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
                PrioritySpeaker,
                ChangeNickname,
                ManageNicknames,
                ManageRoles,
                ManageWebhooks,
                ManageEmojis,
                UseSlashCommands,
                RequestToSpeak,
                ManageEvents,
                ManageThreads,
                CreatePublicThreads,
                CreatePrivateThreads,
                SendMessagesInThreads,
                ModerateMembers
            )
    }
}
