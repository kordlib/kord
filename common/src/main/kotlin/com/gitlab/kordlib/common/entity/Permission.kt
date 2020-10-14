package com.gitlab.kordlib.common.entity

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Serializable(with = Permissions.Companion::class)
class Permissions constructor(val code: Int) {

    val values: Set<Permission> get() = Permission.values().filter { it.code and code == it.code }.toSet()

    operator fun plus(permission: Permission): Permissions = Permissions(this.code or permission.code)


    operator fun minus(permission: Permission): Permissions = Permissions(code xor (code and permission.code))


    operator fun contains(permission: Permission): Boolean {
        return this.code and permission.code == permission.code
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
        return "Permissions(values=${values.joinToString(", ")})"
    }

    @Serializer(forClass = Permissions::class)
    companion object : KSerializer<Permissions> {

        inline operator fun invoke(block: PermissionsBuilder.() -> Unit = {}): Permissions {
            val builder = PermissionsBuilder()
            builder.apply(block)
            return builder.permissions()
        }

        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("permission", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): Permissions {
            return Permissions(decoder.decodeInt())
        }

        override fun serialize(encoder: Encoder, value: Permissions) {
            encoder.encodeInt(value.code)
        }
    }

    class PermissionsBuilder(internal var code: Int = 0) {
        operator fun Permissions.unaryPlus() {
            this@PermissionsBuilder.code = this@PermissionsBuilder.code or code
        }

        operator fun Permissions.unaryMinus() {
            this@PermissionsBuilder.code = this@PermissionsBuilder.code xor (this@PermissionsBuilder.code and code)
        }

        operator fun Permission.unaryPlus() {
            this@PermissionsBuilder.code = this@PermissionsBuilder.code or code
        }

        operator fun Permission.unaryMinus() {
            this@PermissionsBuilder.code = this@PermissionsBuilder.code xor (this@PermissionsBuilder.code and code)
        }

        fun permissions() = Permissions(code)
    }

}

enum class Permission(val code: Int = 0) {
    CreateInstantInvite(0x00000001),
    KickMembers(0x00000002),
    BanMembers(0x00000004),
    Administrator(0x00000008),
    ManageChannels(0x00000010),
    ManageGuild(0x00000020),
    AddReactions(0x00000040),
    ViewAuditLog(0x00000080),
    ViewChannel(0x00000400),
    SendMessages(0x00000800),
    SendTTSMessages(0x00001000),
    ManageMessages(0x00002000),
    EmbedLinks(0x00004000),
    AttachFiles(0x00008000),
    ReadMessageHistory(0x00010000),
    MentionEveryone(0x00020000),
    UseExternalEmojis(0x00040000),
    ViewGuildInsights(0x00080000),
    Connect(0x00100000),
    Speak(0x00200000),
    MuteMembers(0x00400000),
    DeafenMembers(0x00800000),
    MoveMembers(0x01000000),
    UseVAD(0x02000000),
    PrioritySpeaker(0x00000100),
    ChangeNickname(0x04000000),
    ManageNicknames(0x08000000),
    ManageRoles(0x10000000),
    ManageWebhooks(0x20000000),
    ManageEmojis(0x40000000),
    All(0x7FFFFDFF)
}