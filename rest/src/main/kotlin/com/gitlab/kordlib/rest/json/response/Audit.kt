package com.gitlab.kordlib.rest.json.response

import com.gitlab.kordlib.common.entity.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import mu.KotlinLogging

private val auditLogger = KotlinLogging.logger { }

@Serializable
data class AuditLogResponse(
        val webhooks: List<DiscordWebhook>,
        val users: List<DiscordUser>,
        @SerialName("audit_log_entries")
        val auditLogEntries: List<AuditLogEntryResponse>
)

@Serializable
data class AuditLogEntryResponse(
        @SerialName("target_id")
        val targetId: String?,
        val changes: List<AuditLogChangeResponse<out @Contextual Any?>>? = null,
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


    companion object AuditLogChangeSerializer : KSerializer<AuditLogChangeResponse<*>> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("AuditLogChange") {
            element("key", String.serializer().descriptor)
            element("old_value", JsonElement.serializer().descriptor, isOptional = true)
            element("new_value", JsonElement.serializer().descriptor, isOptional = true)
        }

        override fun serialize(encoder: Encoder, value: AuditLogChangeResponse<*>) {
            error("encoding for Audit logs is not implemented")
        }

        override fun deserialize(decoder: Decoder): AuditLogChangeResponse<*> {
            lateinit var key: String
            var new: JsonElement? = null
            var old: JsonElement? = null
            with(decoder.beginStructure(descriptor)) {
                loop@ while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        CompositeDecoder.DECODE_DONE -> break@loop
                        0 -> key = decodeStringElement(descriptor, index)
                        1 -> old = decodeSerializableElement(descriptor, index, JsonElement.serializer())
                        2 -> new = decodeSerializableElement(descriptor, index, JsonElement.serializer())
                    }
                }
                endStructure(descriptor)
            }
            return when (key) {
                "name" -> NameLogChange(old?.jsonPrimitive?.content, new?.jsonPrimitive?.content)
                "icon_hash" -> IconHashLogChange(old?.jsonPrimitive?.content, new?.jsonPrimitive?.content)
                "splash_hash" -> SplashHashLogChange(old?.jsonPrimitive?.content, new?.jsonPrimitive?.content)
                "owner_id" -> OwnerLogChange(old?.jsonPrimitive?.content, new?.jsonPrimitive?.content)
                "region" -> RegionLogChange(old?.jsonPrimitive?.content, new?.jsonPrimitive?.content)
                "afk_channel_id" -> AFKChannelLogChange(old?.jsonPrimitive?.content, new?.jsonPrimitive?.content)
                "vanity_url_code" -> VanityUrlLogChange(old?.jsonPrimitive?.content, new?.jsonPrimitive?.content)
                "widget_channel_id" -> WidgetChannelLogChange(old?.jsonPrimitive?.content, new?.jsonPrimitive?.content)
                "application_id" -> ApplicationLogChange(old?.jsonPrimitive?.content, new?.jsonPrimitive?.content)
                "code" -> CodeLogChange(old?.jsonPrimitive?.content, new?.jsonPrimitive?.content)
                "channel_id" -> ChannelLogChange(old?.jsonPrimitive?.content, new?.jsonPrimitive?.content)
                "inviter_id" -> InviterLogChange(old?.jsonPrimitive?.content, new?.jsonPrimitive?.content)
                "nick" -> NickLogChange(old?.jsonPrimitive?.content, new?.jsonPrimitive?.content)
                "topic" -> TopicLogChange(old?.jsonPrimitive?.content, new?.jsonPrimitive?.content)
                "avatar_hash" -> AvatarHashLogChange(old?.jsonPrimitive?.content, new?.jsonPrimitive?.content)
                "id" -> IdLogChange(old?.jsonPrimitive?.content, new?.jsonPrimitive?.content)

                "widget_enabled" -> WidgetEnabledLogChange(old?.jsonPrimitive?.boolean, new?.jsonPrimitive?.boolean)
                "nsfw" -> NSFWLogChange(old?.jsonPrimitive?.boolean, new?.jsonPrimitive?.boolean)
                "hoist" -> HoistLogChange(old?.jsonPrimitive?.boolean, new?.jsonPrimitive?.boolean)
                "mentionable" -> MentionableLogChange(old?.jsonPrimitive?.boolean, new?.jsonPrimitive?.boolean)
                "temporary" -> TemporaryLogChange(old?.jsonPrimitive?.boolean, new?.jsonPrimitive?.boolean)
                "deaf" -> DeafLogChange(old?.jsonPrimitive?.boolean, new?.jsonPrimitive?.boolean)
                "mute" -> MuteLogChange(old?.jsonPrimitive?.boolean, new?.jsonPrimitive?.boolean)

                "position" -> PositionLogChange(old?.jsonPrimitive?.int, new?.jsonPrimitive?.int)
                "max_uses" -> MaxUsesLogChange(old?.jsonPrimitive?.int, new?.jsonPrimitive?.int)
                "uses" -> UsesLogChange(old?.jsonPrimitive?.int, new?.jsonPrimitive?.int)
                "max_age" -> MaxAgeLogChange(old?.jsonPrimitive?.int, new?.jsonPrimitive?.int)
                "color" -> ColorLogChange(old?.jsonPrimitive?.int, new?.jsonPrimitive?.int)
                "bitrate" -> BitrateLogChange(old?.jsonPrimitive?.int, new?.jsonPrimitive?.int)
                "prune_delete_days" -> PruneDeleteDaysLogChange(old?.jsonPrimitive?.int, new?.jsonPrimitive?.int)
                "afk_timeout" -> AFKTimeoutLogChange(old?.jsonPrimitive?.int, new?.jsonPrimitive?.int)
                "explicit_content_filter" -> ExplicitContentFilterLogChange(old?.jsonPrimitive?.int, new?.jsonPrimitive?.int)
                "default_message_notifications" -> DefaultMessageNotificationLevelLogChange(old?.jsonPrimitive?.int, new?.jsonPrimitive?.int)
                "mfa_level" -> MFALogChange(old?.jsonPrimitive?.int, new?.jsonPrimitive?.int)
                "permissions" -> PermissionsLogChange(old?.jsonPrimitive?.content, new?.jsonPrimitive?.content)
                "allow" -> AllowLogChange(old?.jsonPrimitive?.content, new?.jsonPrimitive?.content)
                "deny" -> DenyLogChange(old?.jsonPrimitive?.content, new?.jsonPrimitive?.content)
                "verification_level" -> VerificationLevelLogChange(old?.jsonPrimitive?.int, new?.jsonPrimitive?.int)

                "\$remove" -> AddLogChange(listFromJson(DiscordAuditLogRoleChange.serializer(), old), listFromJson(DiscordAuditLogRoleChange.serializer(), new))
                "\$add" -> RemoveLogChange(listFromJson(DiscordAuditLogRoleChange.serializer(), old), listFromJson(DiscordAuditLogRoleChange.serializer(), new))
                "permission_overwrites" -> PermissionOverwriteLogChange(listFromJson(Overwrite.serializer(), old), listFromJson(Overwrite.serializer(), new))

                else -> Unknown

            }

        }

        private fun <T> listFromJson(serializer: KSerializer<T>, element: JsonElement?): List<T>? {
            return if (element != null) {
                val asListSerializer = ListSerializer(serializer)
                Json {
                    ignoreUnknownKeys = true
                    allowStructuredMapKeys = true
                    allowSpecialFloatingPointValues = true
                    isLenient = true
                }.decodeFromJsonElement(asListSerializer, element)
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
    internal constructor(old: String?, new: String?) : this(old?.let { Permissions(it) }, new?.let { Permissions(it) }
    )
}

data class AllowLogChange(override val old: Permissions?, override val new: Permissions?) : AuditLogChangeResponse<Permissions>() {
    internal constructor(old: String?, new: String?) : this(old?.let { Permissions(it) }, new?.let { Permissions(it) }
    )
}


data class DenyLogChange(override val old: Permissions?, override val new: Permissions?) : AuditLogChangeResponse<Permissions>() {
    internal constructor(old: String?, new: String?) : this(old?.let { Permissions(it) }, new?.let { Permissions(it) }
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
data class AddLogChange(override val old: List<DiscordAuditLogRoleChange>?, override val new: List<DiscordAuditLogRoleChange>?) : AuditLogChangeResponse<List<DiscordAuditLogRoleChange>>()
data class RemoveLogChange(override val old: List<DiscordAuditLogRoleChange>?, override val new: List<DiscordAuditLogRoleChange>?) : AuditLogChangeResponse<List<DiscordAuditLogRoleChange>>()
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

    companion object AuditLogEventSerializer : KSerializer<AuditLogEventResponse> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("AuditLogEvent", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): AuditLogEventResponse {
            val code = decoder.decodeInt()

            return values().firstOrNull { it.code == code } ?: Unknown
        }

        override fun serialize(encoder: Encoder, value: AuditLogEventResponse) {
            encoder.encodeInt(value.code)
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