package com.gitlab.hopebaron.rest.json.response

import com.gitlab.hopebaron.common.entity.*
import kotlinx.serialization.*
import kotlinx.serialization.internal.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import mu.KotlinLogging

private val auditLogger = KotlinLogging.logger { }

@Serializable
data class AuditLogResponse(
        val webhooks: List<Webhook>,
        val users: List<User>,
        @SerialName("audit_log_entries")
        val auditLogEntries: List<AuditLogEntryResponse>
)

@Serializable
data class AuditLogEntryResponse(
        @SerialName("target_id")
        val targetId: String? = null,
        val changes: List<AuditLogChangeResponse<*>>? = null,
        @SerialName("user_id")
        val userId: String,
        val id: String,
        @SerialName("action_type")
        val actionType: AuditLogEventResponse,
        val options: AuditEntryInfoResponse? = null,
        val reason: String? = null
)

@Serializable(with = AuditLogChangeResponse.AuditLogChangeSerializer::class)
data class AuditLogChangeResponse<T>(
        val newValue: T?,
        val oldValue: T?,
        val key: String
) {

    @Serializer(forClass = AuditLogChangeResponse::class)
    companion object AuditLogChangeSerializer : KSerializer<AuditLogChangeResponse<*>> {
        override val descriptor: SerialDescriptor = object : SerialClassDescImpl("AuditLogChange") {
            init {
                addElement("new_value", true)
                addElement("old_value", true)
                addElement("key", true)
            }
        }

        @UnstableDefault
        override fun deserialize(decoder: Decoder): AuditLogChangeResponse<*> {
            var newValue: JsonElement? = null
            var oldValue: JsonElement? = null
            lateinit var key: String

            with(decoder.beginStructure(descriptor)) {
                loop@ while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        CompositeDecoder.READ_DONE -> break@loop
                        0 -> newValue = decodeSerializableElement(descriptor, index, JsonElement.serializer())
                        1 -> oldValue = decodeSerializableElement(descriptor, index, JsonElement.serializer())
                        3 -> key = decodeStringElement(descriptor, index)
                    }
                }
                val serializer = key.asSerializer()

                val actualNewValue: Any? = serializer?.let {
                    if (newValue != null) Json.nonstrict.fromJson(NullableSerializer(it), newValue as JsonElement)
                } ?: newValue

                val actualOldValue: Any? = serializer?.let {
                    if (oldValue != null) Json.nonstrict.fromJson(NullableSerializer(it), oldValue as JsonElement)

                } ?: oldValue

                endStructure(descriptor)

                return AuditLogChangeResponse(actualNewValue, actualOldValue, key)
            }
        }

        override fun serialize(encoder: Encoder, obj: AuditLogChangeResponse<*>) {
            TODO("not implemented")
        }

        private fun String.asSerializer(): KSerializer<out Any>? = when (this) {
            "name", "icon_hash", "splash_hash" -> StringSerializer
            "owner_id" -> StringSerializer
            "region" -> StringSerializer
            "afk_channel_id" -> StringSerializer
            "afk_timeout" -> Int.serializer()
            "mfa_level" -> MFALevel.MFALevelSerializer
            "verification_level" -> VerificationLevel.VerificationLevelSerializer
            "explicit_content_filter" -> ExplicitContentFilter.ExplicitContentFilterSerializer
            "default_message_notifications" -> DefaultMessageNotificationLevel.DefaultMessageNotificationLevelSerializer
            "vanity_url_code" -> StringSerializer
            "\$add", "\$remove" -> ArrayListSerializer(Role.serializer())
            "prune_delete_days" -> Int.serializer()
            "widget_enabled" -> Boolean.serializer()
            "widget_channel_id" -> StringSerializer
            "position" -> Int.serializer()
            "topic" -> StringSerializer
            "bitrate" -> Int.serializer()
            "permission_overwrites" -> ArrayListSerializer(Overwrite.serializer())
            "nsfw" -> Boolean.serializer()
            "application_id" -> StringSerializer
            "permissions" -> Permissions.serializer()
            "color" -> Int.serializer()
            "hoist" -> Boolean.serializer()
            "mentionable" -> Boolean.serializer()
            "allow" -> Permissions.serializer()
            "deny" -> Permissions.serializer()
            "code" -> StringSerializer
            "channel_id", "inviter_id" -> StringSerializer
            "max_uses", "uses", "max_age" -> Int.serializer()
            "temporary", "deaf", "mute" -> Boolean.serializer()
            "nick" -> StringSerializer
            "avatar_hash" -> StringSerializer
            "id" -> StringSerializer
            "type" -> null // TODO fix mixed type int|string

            else -> {
                auditLogger.warn { "unknown audit log key $this" }
                null
            }
        }

    }

}

@Serializable(with = AuditLogEventResponse.AuditLogEventSerializer::class)
enum class AuditLogEventResponse(val code: Int) {
    GuildUpdate(1),
    ChannelCreate(10),
    ChannelUpdate(11),
    ChannelDelete(12),
    ChannelOverwriteCreate(13),
    ChannelOverwriteUpdate(14),
    ChannelOverwriteDelete(15),

    MemberKick(15),
    MemberPrune(21),
    MemberBanAdd(22),
    MemberBanRemove(23),
    MemberUpdate(24),
    MemberRoleUpdate(25),

    RoleCreate(30),
    RoleUpdate(31),
    RoleDelete(32),

    InviteCreate(40),
    InviteUpdate(41),
    InviteDelete(42),

    WebhookCreate(50),
    WebhookDelete(51),

    EmojiCreate(60),
    EmojiUpdate(61),
    EmojiDelete(62),

    MessageDelete(72);

    @Serializer(forClass = AuditLogEventResponse::class)
    companion object AuditLogEventSerializer : KSerializer<AuditLogEventResponse> {
        override val descriptor: SerialDescriptor = IntDescriptor.withName("AuditLogEvent")

        override fun deserialize(decoder: Decoder): AuditLogEventResponse {
            val code = decoder.decodeInt()

            return values().first { it.code == code }
        }

        override fun serialize(encoder: Encoder, obj: AuditLogEventResponse) {
            encoder.encodeInt(obj.code)
        }

    }
}

@Serializable
data class AuditEntryInfoResponse(
        @SerialName("delete_member_days")
        val deleteMemberDays: String? = null,
        @SerialName("members_removed")
        val membersRemoved: String? = null,
        @SerialName("channel_id")
        val channelId: String? = null,
        val count: String? = null,
        val id: String? = null,
        val type: String? = null,
        @SerialName("role_name")
        val roleName: String? = null
)