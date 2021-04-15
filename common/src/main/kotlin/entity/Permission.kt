package dev.kord.common.entity

import dev.kord.common.DiscordBitSet
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


@Serializable(with = Permissions.Companion::class)
class Permissions constructor(val code: DiscordBitSet) {

    operator fun plus(permission: Permission): Permissions = Permissions(code + permission.code)


    operator fun minus(permission: Permission): Permissions = Permissions(code - permission.code)


    operator fun contains(permission: Permission): Boolean {
        return permission.code in code
    }

    operator fun plus(permission: Permissions): Permissions = Permissions(code + permission.code)


    operator fun minus(permission: Permissions): Permissions = Permissions(code - permission.code)


    operator fun contains(permission: Permissions): Boolean {
        return permission.code in code
    }

    @OptIn(ExperimentalContracts::class)
    inline fun copy(block: PermissionsBuilder.() -> Unit): Permissions {
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

    companion object : KSerializer<Permissions> {

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

    class PermissionsBuilder(internal val code: DiscordBitSet) {
        operator fun Permissions.unaryPlus() {
            this@PermissionsBuilder.code.add(code)
        }

        operator fun Permissions.unaryMinus() {
            this@PermissionsBuilder.code.remove(code)
        }

        operator fun Permission.unaryPlus() {
            this@PermissionsBuilder.code.add(code)
        }

        operator fun Permission.unaryMinus() {
            this@PermissionsBuilder.code.remove(code)
        }

        fun permissions() = Permissions(code)
    }
}

fun Permissions(value: String) = Permissions(DiscordBitSet(value))

inline fun Permissions(block: Permissions.PermissionsBuilder.() -> Unit = {}): Permissions {
    val builder = Permissions.PermissionsBuilder(DiscordBitSet(0))
    builder.apply(block)
    return builder.permissions()
}

fun Permissions(vararg permissions: Permission) = Permissions {
    permissions.forEach { +it }
}

fun Permissions(vararg permissions: Permissions) = Permissions {
    permissions.forEach { +it }
}

fun Permissions(permissions: Iterable<Permission>) = Permissions {
    permissions.forEach { +it }
}


@JvmName("PermissionWithIterable")
fun Permissions(permissions: Iterable<Permissions>) = Permissions {
    permissions.forEach { +it }
}


sealed class Permission(val code: DiscordBitSet) {
    constructor(vararg values: Long) : this(DiscordBitSet(values))

    object CreateInstantInvite : Permission(0x00000001)
    object KickMembers : Permission(0x00000002)
    object BanMembers : Permission(0x00000004)
    object Administrator : Permission(0x00000008)
    object ManageChannels : Permission(0x00000010)
    object ManageGuild : Permission(0x00000020)
    object AddReactions : Permission(0x00000040)
    object ViewAuditLog : Permission(0x00000080)
    object ViewChannel : Permission(0x00000400)
    object SendMessages : Permission(0x00000800)
    object SendTTSMessages : Permission(0x00001000)
    object ManageMessages : Permission(0x00002000)
    object EmbedLinks : Permission(0x00004000)
    object AttachFiles : Permission(0x00008000)
    object ReadMessageHistory : Permission(0x00010000)
    object MentionEveryone : Permission(0x00020000)
    object UseExternalEmojis : Permission(0x00040000)
    object ViewGuildInsights : Permission(0x00080000)
    object Connect : Permission(0x00100000)
    object Speak : Permission(0x00200000)
    object MuteMembers : Permission(0x00400000)
    object DeafenMembers : Permission(0x00800000)
    object MoveMembers : Permission(0x01000000)
    object UseVAD : Permission(0x02000000)
    object PrioritySpeaker : Permission(0x00000100)
    object ChangeNickname : Permission(0x04000000)
    object ManageNicknames : Permission(0x08000000)
    object ManageRoles : Permission(0x10000000)
    object ManageWebhooks : Permission(0x20000000)
    object ManageEmojis : Permission(0x40000000)
    object UseSlashCommands : Permission(0x80000000)
    object All : Permission(0x7FFFFDFF)
}
