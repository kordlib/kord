package com.gitlab.kordlib.rest.json.response

import com.gitlab.kordlib.common.entity.*
import kotlinx.serialization.*
import kotlinx.serialization.internal.ArrayListSerializer
import kotlinx.serialization.internal.IntDescriptor
import kotlinx.serialization.internal.SerialClassDescImpl
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonElementSerializer
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
        val targetId: String?,
        val changes: List<AuditLogChangeResponse<out @ContextualSerialization Any?>>? = null,
        @SerialName("user_id")
        val userId: String,
        val id: String,
        @SerialName("action_type")
        val actionType: AuditLogEventResponse,
        val options: AuditEntryInfoResponse? = null,
        val reason: String? = null
)

@Serializable(with = AuditLogChangeResponse.AuditLogChangeSerializer::class)
sealed class AuditLogChangeResponse<T> {
    abstract val old: T?
    abstract val new: T?


    @Serializer(forClass = AuditLogChangeResponse::class)
    companion object AuditLogChangeSerializer : KSerializer<AuditLogChangeResponse<*>> {
        override val descriptor: SerialDescriptor = object : SerialClassDescImpl("AuditLogChange") {
            init {
                addElement("key")
                addElement("old_value", true)
                addElement("new_value", true)
            }

        }

        @UnstableDefault
        override fun deserialize(decoder: Decoder): AuditLogChangeResponse<*> {
            lateinit var key: String
            var new: JsonElement? = null
            var old: JsonElement? = null
            with(decoder.beginStructure(descriptor)) {
                loop@ while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        CompositeDecoder.READ_DONE -> break@loop
                        0 -> key = decodeStringElement(descriptor, index)
                        1 -> old = decodeSerializableElement(descriptor, index, JsonElementSerializer)
                        2 -> new = decodeSerializableElement(descriptor, index, JsonElementSerializer)
                    }
                }
                endStructure(descriptor)
            }
            return when (key) {
                "name" -> NameLogChange(old?.primitive?.content, new?.primitive?.content)
                "icon_hash" -> IconHashLogChange(old?.primitive?.content, new?.primitive?.content)
                "splash_hash" -> SplashHashLogChange(old?.primitive?.content, new?.primitive?.content)
                "owner_id" -> OwnerLogChange(old?.primitive?.content, new?.primitive?.content)
                "region" -> RegionLogChange(old?.primitive?.content, new?.primitive?.content)
                "afk_channel_id" -> AFKChannelLogChange(old?.primitive?.content, new?.primitive?.content)
                "vanity_url_code" -> VanityUrlLogChange(old?.primitive?.content, new?.primitive?.content)
                "widget_channel_id" -> WidgetChannelLogChange(old?.primitive?.content, new?.primitive?.content)
                "application_id" -> ApplicationLogChange(old?.primitive?.content, new?.primitive?.content)
                "code" -> CodeLogChange(old?.primitive?.content, new?.primitive?.content)
                "channel_id" -> ChannelLogChange(old?.primitive?.content, new?.primitive?.content)
                "inviter_id" -> InviterLogChange(old?.primitive?.content, new?.primitive?.content)
                "nick" -> NickLogChange(old?.primitive?.content, new?.primitive?.content)
                "topic" -> TopicLogChange(old?.primitive?.content, new?.primitive?.content)
                "avatar_hash" -> AvatarHashLogChange(old?.primitive?.content, new?.primitive?.content)
                "id" -> IdLogChange(old?.primitive?.content, new?.primitive?.content)

                "widget_enabled" -> WidgetEnabledLogChange(old?.primitive?.boolean, new?.primitive?.boolean)
                "nsfw" -> NSFWLogChange(old?.primitive?.boolean, new?.primitive?.boolean)
                "hoist" -> HoistLogChange(old?.primitive?.boolean, new?.primitive?.boolean)
                "mentionable" -> MentionableLogChange(old?.primitive?.boolean, new?.primitive?.boolean)
                "temporary" -> TemporaryLogChange(old?.primitive?.boolean, new?.primitive?.boolean)
                "deaf" -> DeafLogChange(old?.primitive?.boolean, new?.primitive?.boolean)
                "mute" -> MuteLogChange(old?.primitive?.boolean, new?.primitive?.boolean)

                "position" -> PositionLogChange(old?.primitive?.int, new?.primitive?.int)
                "max_uses" -> MaxUsesLogChange(old?.primitive?.int, new?.primitive?.int)
                "uses" -> UsesLogChange(old?.primitive?.int, new?.primitive?.int)
                "max_age" -> MaxAgeLogChange(old?.primitive?.int, new?.primitive?.int)
                "color" -> ColorLogChange(old?.primitive?.int, new?.primitive?.int)
                "bitrate" -> BitrateLogChange(old?.primitive?.int, new?.primitive?.int)
                "prune_delete_days" -> PruneDeleteDaysLogChange(old?.primitive?.int, new?.primitive?.int)
                "afk_timeout" -> AFKTimeoutLogChange(old?.primitive?.int, new?.primitive?.int)
                "explicit_content_filter" -> ExplicitContentFilterLogChange(old?.primitive?.int, new?.primitive?.int)
                "default_message_notifications" -> DefaultMessageNotificationLevelLogChange(old?.primitive?.int, new?.primitive?.int)
                "mfa_level" -> MFALogChange(old?.primitive?.int, new?.primitive?.int)
                "permissions" -> PermissionsLogChange(old?.primitive?.int, new?.primitive?.int)
                "allow" -> AllowLogChange(old?.primitive?.int, new?.primitive?.int)
                "deny" -> DenyLogChange(old?.primitive?.int, new?.primitive?.int)
                "verification_level" -> VerificationLevelLogChange(old?.primitive?.int, new?.primitive?.int)

                "\$remove" -> AddLogChange(listFromJson(AuditLogRoleChange.serializer(), old), listFromJson(AuditLogRoleChange.serializer(), new))
                "\$add" -> RemoveLogChange(listFromJson(AuditLogRoleChange.serializer(), old), listFromJson(AuditLogRoleChange.serializer(), new))
                "permission_overwrites" -> PermissionOverwriteLogChange(listFromJson(Overwrite.serializer(), old), listFromJson(Overwrite.serializer(), new))

                else -> Unknown

            }

        }

        @UnstableDefault
        private fun <T> listFromJson(serializer: KSerializer<T>, element: JsonElement?): List<T>? {
            return if (element != null) {
                val asListSerializer = ArrayListSerializer(serializer)
                Json.nonstrict.fromJson(asListSerializer, element)
            } else null
        }
    }
}


data class HoistLogChange(override val old: Boolean?, override val new: Boolean?) : AuditLogChangeResponse<Boolean>()
data class NSFWLogChange(override val old: Boolean?, override val new: Boolean?) : AuditLogChangeResponse<Boolean>()
data class MentionableLogChange(override val old: Boolean?, override val new: Boolean?) : AuditLogChangeResponse<Boolean>()
data class TemporaryLogChange(override val old: Boolean?, override val new: Boolean?) : AuditLogChangeResponse<Boolean>()
data class DeafLogChange(override val old: Boolean?, override val new: Boolean?) : AuditLogChangeResponse<Boolean>()
data class MuteLogChange(override val old: Boolean?, override val new: Boolean?) : AuditLogChangeResponse<Boolean>()
data class WidgetEnabledLogChange(override val old: Boolean?, override val new: Boolean?) : AuditLogChangeResponse<Boolean>()
data class PermissionsLogChange(override val old: Permissions?, override val new: Permissions?) : AuditLogChangeResponse<Permissions>() {
    internal constructor(old: Int?, new: Int?) : this(old?.let { Permissions { +it } },
            new?.let { Permissions { +it } }
    )
}

data class AllowLogChange(override val old: Permissions?, override val new: Permissions?) : AuditLogChangeResponse<Permissions>() {
    internal constructor(old: Int?, new: Int?) : this(old?.let { Permissions { +it } },
            new?.let { Permissions { +it } }
    )
}


data class DenyLogChange(override val old: Permissions?, override val new: Permissions?) : AuditLogChangeResponse<Permissions>() {
    internal constructor(old: Int?, new: Int?) : this(old?.let { Permissions { +it } },
            new?.let { Permissions { +it } }
    )
}

data class NameLogChange(override val old: String?, override val new: String?) : AuditLogChangeResponse<String>()
data class VanityUrlLogChange(override val old: String?, override val new: String?) : AuditLogChangeResponse<String>()
data class SplashHashLogChange(override val old: String?, override val new: String?) : AuditLogChangeResponse<String>()
data class RegionLogChange(override val old: String?, override val new: String?) : AuditLogChangeResponse<String>()
data class AFKChannelLogChange(override val old: String?, override val new: String?) : AuditLogChangeResponse<String>()
data class WidgetChannelLogChange(override val old: String?, override val new: String?) : AuditLogChangeResponse<String>()
data class IconHashLogChange(override val old: String?, override val new: String?) : AuditLogChangeResponse<String>()
data class OwnerLogChange(override val old: String?, override val new: String?) : AuditLogChangeResponse<String>()
data class TopicLogChange(override val old: String?, override val new: String?) : AuditLogChangeResponse<String>()
data class ApplicationLogChange(override val old: String?, override val new: String?) : AuditLogChangeResponse<String>()
data class CodeLogChange(override val old: String?, override val new: String?) : AuditLogChangeResponse<String>()
data class ChannelLogChange(override val old: String?, override val new: String?) : AuditLogChangeResponse<String>()
data class InviterLogChange(override val old: String?, override val new: String?) : AuditLogChangeResponse<String>()
data class NickLogChange(override val old: String?, override val new: String?) : AuditLogChangeResponse<String>()
data class AvatarHashLogChange(override val old: String?, override val new: String?) : AuditLogChangeResponse<String>()
data class IdLogChange(override val old: String?, override val new: String?) : AuditLogChangeResponse<String>()
data class AFKTimeoutLogChange(override val old: Int?, override val new: Int?) : AuditLogChangeResponse<Int>()
data class PruneDeleteDaysLogChange(override val old: Int?, override val new: Int?) : AuditLogChangeResponse<Int>()
data class PositionLogChange(override val old: Int?, override val new: Int?) : AuditLogChangeResponse<Int>()
data class BitrateLogChange(override val old: Int?, override val new: Int?) : AuditLogChangeResponse<Int>()
data class MaxUsesLogChange(override val old: Int?, override val new: Int?) : AuditLogChangeResponse<Int>()
data class UsesLogChange(override val old: Int?, override val new: Int?) : AuditLogChangeResponse<Int>()
data class MaxAgeLogChange(override val old: Int?, override val new: Int?) : AuditLogChangeResponse<Int>()
data class ColorLogChange(override val old: Int?, override val new: Int?) : AuditLogChangeResponse<Int>()
data class AddLogChange(override val old: List<AuditLogRoleChange>?, override val new: List<AuditLogRoleChange>?) : AuditLogChangeResponse<List<AuditLogRoleChange>>()
data class RemoveLogChange(override val old: List<AuditLogRoleChange>?, override val new: List<AuditLogRoleChange>?) : AuditLogChangeResponse<List<AuditLogRoleChange>>()
data class PermissionOverwriteLogChange(override val old: List<Overwrite>?, override val new: List<Overwrite>?) : AuditLogChangeResponse<List<Overwrite>>()

data class MFALogChange(override val old: MFALevel?, override val new: MFALevel?) : AuditLogChangeResponse<MFALevel>() {
    internal constructor(old: Int?, new: Int?) : this(
            MFALevel.values().firstOrNull { it.code == new },
            MFALevel.values().firstOrNull { it.code == old }
    )
}

data class DefaultMessageNotificationLevelLogChange(override val old: DefaultMessageNotificationLevel?, override val new: DefaultMessageNotificationLevel?) : AuditLogChangeResponse<DefaultMessageNotificationLevel>() {
    internal constructor(old: Int?, new: Int?) : this(
            DefaultMessageNotificationLevel.values().firstOrNull { it.code == new },
            DefaultMessageNotificationLevel.values().firstOrNull { it.code == old }
    )
}

data class VerificationLevelLogChange(override val old: VerificationLevel?, override val new: VerificationLevel?) : AuditLogChangeResponse<VerificationLevel>() {
    internal constructor(old: Int?, new: Int?) : this(
            VerificationLevel.values().firstOrNull { it.code == new },
            VerificationLevel.values().firstOrNull { it.code == old }
    )
}

data class ExplicitContentFilterLogChange(override val old: ExplicitContentFilter?, override val new: ExplicitContentFilter?) : AuditLogChangeResponse<ExplicitContentFilter>() {
    internal constructor(old: Int?, new: Int?) : this(
            ExplicitContentFilter.values().firstOrNull { it.code == new },
            ExplicitContentFilter.values().firstOrNull { it.code == old }
    )
}

object Unknown : AuditLogChangeResponse<Nothing>() {
    override val old = null
    override val new = null
}

@Serializable(with = AuditLogEventResponse.AuditLogEventSerializer::class)
enum class AuditLogEventResponse(val code: Int) {
    /** The default code for unknown values. */
    Unknown(Int.MIN_VALUE),
    GuildUpdate(1),
    ChannelCreate(10),
    ChannelUpdate(11),
    ChannelDelete(12),
    ChannelOverwriteCreate(13),
    ChannelOverwriteUpdate(14),
    ChannelOverwriteDelete(15),

    MemberKick(20),
    MemberPrune(21),
    MemberBanAdd(22),
    MemberBanRemove(23),
    MemberUpdate(24),
    MemberRoleUpdate(25),
    MemberMove(26),
    MemberDisconnect(27),
    BotAdd(28),

    RoleCreate(30),
    RoleUpdate(31),
    RoleDelete(32),

    InviteCreate(40),
    InviteUpdate(41),
    InviteDelete(42),

    WebhookCreate(50),
    WebhookUpdate(51),
    WebhookDelete(52),


    EmojiCreate(60),
    EmojiUpdate(61),
    EmojiDelete(62),

    MessageDelete(72),
    MessageBulkDelete(73),
    MessagePin(74),
    MessageUnpin(75),

    IntegrationCreate(80),
    IntegrationUpdate(81),
    IntegrationDelete(82);

    @Serializer(forClass = AuditLogEventResponse::class)
    companion object AuditLogEventSerializer : KSerializer<AuditLogEventResponse> {
        override val descriptor: SerialDescriptor = IntDescriptor.withName("AuditLogEvent")

        override fun deserialize(decoder: Decoder): AuditLogEventResponse {
            val code = decoder.decodeInt()

            return values().firstOrNull { it.code == code } ?: Unknown
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
        @SerialName("message_id")
        val messageId: String? = null,
        val count: String? = null,
        val id: String? = null,
        val type: String? = null,
        @SerialName("role_name")
        val roleName: String? = null
)