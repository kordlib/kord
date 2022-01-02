package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.orEmpty
import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.serializer
import dev.kord.common.Color as CommonColor
import dev.kord.common.entity.DefaultMessageNotificationLevel as CommonDefaultMessageNotificationLevel
import dev.kord.common.entity.ExplicitContentFilter as CommonExplicitContentFilter
import dev.kord.common.entity.MFALevel as CommonMFALevel
import dev.kord.common.entity.Permissions as CommonPermissions
import dev.kord.common.entity.VerificationLevel as CommonVerificationLevel

@Serializable
data class DiscordAuditLog(
    val webhooks: List<DiscordWebhook>,
    val users: List<DiscordUser>,
    @SerialName("audit_log_entries")
    val auditLogEntries: List<DiscordAuditLogEntry>,
    val integrations: List<DiscordPartialIntegration>,
    val threads: List<DiscordChannel>
)

@Serializable
data class DiscordAuditLogEntry(
    @SerialName("target_id")
    val targetId: Snowflake?,
    val changes: Optional<List<AuditLogChange<in @Contextual Any?>>> = Optional.Missing(),
    @SerialName("user_id")
    val userId: Snowflake,
    val id: Snowflake,
    @SerialName("action_type")
    val actionType: AuditLogEvent,
    val options: Optional<AuditLogEntryOptionalInfo> = Optional.Missing(),
    val reason: Optional<String> = Optional.Missing(),
) {

    @Suppress("UNCHECKED_CAST")
    operator fun <T> get(value: AuditLogChangeKey<T>): AuditLogChange<T>? =
        changes.orEmpty().firstOrNull { it.key == value } as? AuditLogChange<T>

}

@Serializable
data class AuditLogEntryOptionalInfo(
    /*
    Do not trust the docs:
    2020-11-12 field is described as present but is in fact optional
     */
    @SerialName("delete_member_days")
    val deleteMemberDays: Optional<String> = Optional.Missing(),
    /*
    Do not trust the docs:
    2020-11-12 field is described as present but is in fact optional
     */
    @SerialName("members_removed")
    val membersRemoved: Optional<String> = Optional.Missing(),
    /*
    Do not trust the docs:
    2020-11-12 field is described as present but is in fact optional
     */
    @SerialName("channel_id")
    val channelId: OptionalSnowflake = OptionalSnowflake.Missing,
    /*
    Do not trust the docs:
    2020-11-12 field is described as present but is in fact optional
     */
    @SerialName("message_id")
    val messageId: OptionalSnowflake = OptionalSnowflake.Missing,
    /*
    Do not trust the docs:
    2020-11-12 field is described as present but is in fact optional
     */
    val count: Optional<String> = Optional.Missing(),
    /*
    Do not trust the docs:
    2020-11-12 field is described as present but is in fact optional
     */
    val id: OptionalSnowflake = OptionalSnowflake.Missing,
    /*
    Do not trust the docs:
    2020-11-12 field is described as present but is in fact optional
     */
    val type: Optional<OverwriteType> = Optional.Missing(),
    /*
    Do not trust the docs:
    2020-11-12 field is described as present but is in fact optional
     */
    @SerialName("role_name")
    val roleName: Optional<String> = Optional.Missing()
)

@Serializable(with = AuditLogChange.Serializer::class)
data class AuditLogChange<T>(
    val new: T?,
    val old: T?,
    val key: AuditLogChangeKey<T>,
) {

    internal class Serializer<T>(val ser: KSerializer<T>) : KSerializer<AuditLogChange<T>> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Kord.AuditLogChange", ser.descriptor) {
            element<JsonElement>("new_value")
            element<JsonElement>("old_value")
            element("key", ser.descriptor)
        }

        override fun deserialize(decoder: Decoder): AuditLogChange<T> {
            decoder.decodeStructure(descriptor) {
                var new: JsonElement? = null
                var old: JsonElement? = null
                lateinit var key: AuditLogChangeKey<*>
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> new = decodeSerializableElement(descriptor, index, JsonElement.serializer())
                        1 -> old = decodeSerializableElement(descriptor, index, JsonElement.serializer())
                        2 -> key = decodeSerializableElement(
                            descriptor,
                            index,
                            AuditLogChangeKey.Serializer(Unit.serializer())
                        )
                        CompositeDecoder.DECODE_DONE -> break
                        else -> throw SerializationException("unknown index: $index")
                    }
                }

                val newVal = new?.let { Json.decodeFromJsonElement(key.serializer, new) }
                val oldVal = old?.let { Json.decodeFromJsonElement(key.serializer, old) }

                @Suppress("UNCHECKED_CAST")
                return AuditLogChange(
                    new = newVal,
                    old = oldVal,
                    key = key as AuditLogChangeKey<Any?>
                ) as AuditLogChange<T>
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun serialize(encoder: Encoder, value: AuditLogChange<T>) {
            val logChange = value as AuditLogChange<Unit>
            encoder.encodeStructure(descriptor) {
                encodeSerializableElement(descriptor, 0, logChange.key.serializer, logChange.new as Unit)
                encodeSerializableElement(descriptor, 0, logChange.key.serializer, logChange.old as Unit)
                encodeSerializableElement(descriptor, 0, AuditLogChangeKey.serializer(Unit.serializer()), logChange.key)
            }
        }
    }
}

@Serializable(with = AuditLogChangeKey.Serializer::class)
sealed class AuditLogChangeKey<T>(val name: String, val serializer: KSerializer<T>) {

    override fun toString(): String = "AuditLogChangeKey(name=$name)"

    class Unknown(name: String) : AuditLogChangeKey<JsonElement>(name, JsonElement.serializer())

    @SerialName("name")
    object Name : AuditLogChangeKey<String>("name", serializer())

    @SerialName("icon_hash")
    object IconHash : AuditLogChangeKey<String>("icon_hash", serializer())

    @SerialName("splash_hash")
    object SplashHash : AuditLogChangeKey<String>("splash_hash", serializer())

    @SerialName("owner_id")
    object OwnerId : AuditLogChangeKey<Snowflake>("owner_id", serializer())

    @SerialName("region")
    object Region : AuditLogChangeKey<String>("region", serializer())

    @SerialName("afk_channel_id")
    object AfkChannelId : AuditLogChangeKey<Snowflake>("afk_channel_id", serializer())

    @SerialName("afk_timeout")
    object AfkTimeout : AuditLogChangeKey<Int>("afk_timeout", serializer())

    @SerialName("mfa_level")
    object MFALevel : AuditLogChangeKey<CommonMFALevel>("mfa_level", serializer())

    @SerialName("verification_level")
    object VerificationLevel : AuditLogChangeKey<CommonVerificationLevel>("verification_level", serializer())

    @SerialName("explicit_content_filter")
    object ExplicitContentFilter :
        AuditLogChangeKey<CommonExplicitContentFilter>("explicit_content_filter", serializer())

    @SerialName("default_message_notifications")
    object DefaultMessageNotificationLevel :
        AuditLogChangeKey<CommonDefaultMessageNotificationLevel>("default_message_notifications", serializer())

    @SerialName("vanity_url_code")
    object VanityUrlCode : AuditLogChangeKey<String>("vanity_url_code", serializer())

    @SerialName("\$add")
    object Add : AuditLogChangeKey<List<DiscordPartialRole>>("\$add", serializer())

    @SerialName("\$remove")
    object Remove : AuditLogChangeKey<List<DiscordPartialRole>>("\$remove", serializer())

    @SerialName("prune_delete_days")
    object PruneDeleteDays : AuditLogChangeKey<Int>("prune_delete_days", serializer())

    @SerialName("widget_enabled")
    object WidgetEnabled : AuditLogChangeKey<Boolean>("widget_enabled", serializer())

    @SerialName("widget_channel_id")
    object WidgetChannelId : AuditLogChangeKey<Snowflake>("widget_channel_id", serializer())

    @SerialName("system_channel_id")
    object SystemChannelId : AuditLogChangeKey<Snowflake>("system_channel_id", serializer())

    @SerialName("position")
    object Position : AuditLogChangeKey<Int>("position", serializer())

    @SerialName("topic")
    object Topic : AuditLogChangeKey<String>("topic", serializer())

    @SerialName("bitrate")
    object Bitrate : AuditLogChangeKey<Int>("bitrate", serializer())

    @SerialName("permission_overwrites")
    object PermissionOverwrites : AuditLogChangeKey<List<Overwrite>>("permission_overwrites", serializer())

    @SerialName("nsfw")
    object Nsfw : AuditLogChangeKey<Boolean>("nsfw", serializer())

    @SerialName("application_id")
    object ApplicationId : AuditLogChangeKey<Snowflake>("application_id", serializer())

    @SerialName("rate_limit_per_user")
    object RateLimitPerUser : AuditLogChangeKey<Int>("rate_limit_per_user", serializer())

    @SerialName("permissions")
    object Permissions : AuditLogChangeKey<CommonPermissions>("permissions", serializer())

    @SerialName("color")
    object Color : AuditLogChangeKey<CommonColor>("color", serializer())

    @SerialName("hoist")
    object Hoist : AuditLogChangeKey<Boolean>("hoist", serializer())

    @SerialName("mentionable")
    object Mentionable : AuditLogChangeKey<Boolean>("mentionable", serializer())

    @SerialName("allow")
    object Allow : AuditLogChangeKey<CommonPermissions>("allow", serializer())

    @SerialName("deny")
    object Deny : AuditLogChangeKey<CommonPermissions>("deny", serializer())

    @SerialName("code")
    object Code : AuditLogChangeKey<String>("code", serializer())

    @SerialName("channel_id")
    object ChannelId : AuditLogChangeKey<Snowflake>("channel_id", serializer())

    @SerialName("inviter_id")
    object InviterId : AuditLogChangeKey<Snowflake>("inviter_id", serializer())

    @SerialName("location")
    object Location : AuditLogChangeKey<String>("location", serializer())

    @SerialName("max_uses")
    object MaxUses : AuditLogChangeKey<Int>("max_uses", serializer())

    @SerialName("uses")
    object Uses : AuditLogChangeKey<Int>("uses", serializer())

    @SerialName("max_age")
    object MaxAges : AuditLogChangeKey<Int>("max_age", serializer())

    @SerialName("temporary")
    object Temporary : AuditLogChangeKey<Boolean>("temporary", serializer())

    @SerialName("deaf")
    object Deaf : AuditLogChangeKey<Boolean>("deaf", serializer())

    @SerialName("mute")
    object Mute : AuditLogChangeKey<Boolean>("mute", serializer())

    @SerialName("nick")
    object Nick : AuditLogChangeKey<String>("nick", serializer())

    @SerialName("avatar_hash")
    object AvatarHash : AuditLogChangeKey<String>("avatar_hash", serializer())

    @SerialName("id")
    object Id : AuditLogChangeKey<Snowflake>("id", serializer())

    @SerialName("type")
    object Type : AuditLogChangeKey<ChannelType>("type", serializer())

    @SerialName("enable_emoticons")
    object EnableEmoticons : AuditLogChangeKey<Boolean>("enable_emoticons", serializer())

    @SerialName("expire_behavior")
    object ExpireBehavior : AuditLogChangeKey<IntegrationExpireBehavior>("expire_behavior", serializer())

    @SerialName("expire_grace_period")
    object ExpireGracePeriod : AuditLogChangeKey<Int>("expire_grace_period", serializer())

    @SerialName("user_limit")
    object UserLimit : AuditLogChangeKey<Int>("user_limit", serializer())

    @SerialName("archived")
    object Archived : AuditLogChangeKey<Boolean>("archived", serializer())

    @SerialName("locked")
    object Locked : AuditLogChangeKey<Boolean>("locked", serializer())

    @SerialName("auto_archive_duration")
    object AutoArchiveDuration : AuditLogChangeKey<ArchiveDuration>("auto_archive_duration", serializer())

    @SerialName("default_auto_archive_duration")
    object DefaultAutoArchiveDuration :
        AuditLogChangeKey<ArchiveDuration>("default_auto_archive_duration", serializer())

    @SerialName("entity_type")
    object EntityType : AuditLogChangeKey<ScheduledEntityType>(
        "entity_type",
        serializer()
    )

    @SerialName("status")
    object Status : AuditLogChangeKey<GuildScheduledEventStatus>(
        "status",
        serializer()
    )

    @SerialName("sku_ids")
    object SkuIds : AuditLogChangeKey<List<Snowflake>>(
        "sku_ids",
        serializer()
    )

    internal class Serializer<T>(val type: KSerializer<T>) : KSerializer<AuditLogChangeKey<T>> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("Kord.AuditLogKey", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: AuditLogChangeKey<T>) {
            encoder.encodeString(value.name)
        }

        @Suppress("UNCHECKED_CAST")
        override fun deserialize(decoder: Decoder): AuditLogChangeKey<T> {
            val name = decoder.decodeString()
            return when (name) {
                "name" -> Name
                "icon_hash" -> IconHash
                "splash_hash" -> SplashHash
                "owner_id" -> OwnerId
                "region" -> Region
                "afk_channel_id" -> AfkChannelId
                "afk_timeout" -> AfkTimeout
                "mfa_level" -> MFALevel
                "verification_level" -> VerificationLevel
                "explicit_content_filter" -> ExplicitContentFilter
                "default_message_notifications" -> DefaultMessageNotificationLevel
                "vanity_url_code" -> VanityUrlCode
                "\$add" -> Add
                "\$remove" -> Remove
                "prune_delete_days" -> PruneDeleteDays
                "widget_enabled" -> WidgetEnabled
                "widget_channel_id" -> WidgetChannelId
                "system_channel_id" -> SystemChannelId
                "position" -> Position
                "topic" -> Topic
                "bitrate" -> Bitrate
                "permission_overwrites" -> PermissionOverwrites
                "nsfw" -> Nsfw
                "application_id" -> ApplicationId
                "rate_limit_per_user" -> RateLimitPerUser
                "permissions" -> Permissions
                "color" -> Color
                "hoist" -> Hoist
                "mentionable" -> Mentionable
                "allow" -> Allow
                "deny" -> Deny
                "code" -> Code
                "channel_id" -> ChannelId
                "inviter_id" -> InviterId
                "location" -> Location
                "max_uses" -> MaxUses
                "uses" -> Uses
                "max_age" -> MaxAges
                "temporary" -> Temporary
                "deaf" -> Deaf
                "mute" -> Mute
                "nick" -> Nick
                "avatar_hash" -> AvatarHash
                "id" -> Id
                "type" -> Type
                "enable_emoticons" -> EnableEmoticons
                "expire_behavior" -> ExpireBehavior
                "expire_grace_period" -> ExpireGracePeriod
                "user_limit" -> UserLimit
                "locked" -> Locked
                "archived" -> Archived
                "auto_archive_duration" -> AutoArchiveDuration
                "default_auto_archive_duration" -> DefaultAutoArchiveDuration
                "entity_type" -> EntityType
                "status" -> Status
                "sku_ids" -> SkuIds
                else -> Unknown(name)
            } as AuditLogChangeKey<T>
        }
    }
}

@Serializable(with = AuditLogEvent.Serializer::class)
sealed class AuditLogEvent(val value: Int) {
    class Unknown(value: Int) : AuditLogEvent(value)
    object GuildUpdate : AuditLogEvent(1)
    object ChannelCreate : AuditLogEvent(10)
    object ChannelUpdate : AuditLogEvent(11)
    object ChannelDelete : AuditLogEvent(12)
    object ChannelOverwriteCreate : AuditLogEvent(13)
    object ChannelOverwriteUpdate : AuditLogEvent(14)
    object ChannelOverwriteDelete : AuditLogEvent(15)
    object MemberKick : AuditLogEvent(20)
    object MemberPrune : AuditLogEvent(21)
    object MemberBanAdd : AuditLogEvent(22)
    object MemberBanRemove : AuditLogEvent(23)
    object MemberUpdate : AuditLogEvent(24)
    object MemberRoleUpdate : AuditLogEvent(25)
    object MemberMove : AuditLogEvent(26)
    object MemberDisconnect : AuditLogEvent(27)
    object BotAdd : AuditLogEvent(28)
    object RoleCreate : AuditLogEvent(30)
    object RoleUpdate : AuditLogEvent(31)
    object RoleDelete : AuditLogEvent(32)
    object InviteCreate : AuditLogEvent(40)
    object InviteUpdate : AuditLogEvent(41)
    object InviteDelete : AuditLogEvent(42)
    object WebhookCreate : AuditLogEvent(50)
    object WebhookUpdate : AuditLogEvent(51)
    object WebhookDelete : AuditLogEvent(52)
    object EmojiCreate : AuditLogEvent(60)
    object EmojiUpdate : AuditLogEvent(61)
    object EmojiDelete : AuditLogEvent(62)
    object MessageDelete : AuditLogEvent(72)
    object MessageBulkDelete : AuditLogEvent(73)
    object MessagePin : AuditLogEvent(74)
    object MessageUnpin : AuditLogEvent(75)
    object IntegrationCreate : AuditLogEvent(80)
    object IntegrationUpdate : AuditLogEvent(81)
    object IntegrationDelete : AuditLogEvent(82)
    object StageInstanceCreate : AuditLogEvent(83)
    object StageInstanceUpdate : AuditLogEvent(84)
    object StageInstanceDelete : AuditLogEvent(85)
    object StickerCreate : AuditLogEvent(90)
    object StickerUpdate : AuditLogEvent(91)
    object StickerDelete : AuditLogEvent(92)
    object GuildScheduledEventCreate : AuditLogEvent(100)
    object GuildScheduledEventUpdate : AuditLogEvent(101)
    object GuildScheduledEventDelete : AuditLogEvent(102)
    object ThreadCreate : AuditLogEvent(110)
    object ThreadUpdate : AuditLogEvent(111)
    object ThreadDelete : AuditLogEvent(112)


    internal object Serializer : KSerializer<AuditLogEvent> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("Kord.AuditLogEvent", PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, value: AuditLogEvent) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): AuditLogEvent = when (val value = decoder.decodeInt()) {
            1 -> GuildUpdate
            10 -> ChannelCreate
            11 -> ChannelUpdate
            12 -> ChannelDelete
            13 -> ChannelOverwriteCreate
            14 -> ChannelOverwriteUpdate
            15 -> ChannelOverwriteDelete
            20 -> MemberKick
            21 -> MemberPrune
            22 -> MemberBanAdd
            23 -> MemberBanRemove
            24 -> MemberUpdate
            25 -> MemberRoleUpdate
            26 -> MemberMove
            27 -> MemberDisconnect
            28 -> BotAdd
            30 -> RoleCreate
            31 -> RoleUpdate
            32 -> RoleDelete
            40 -> InviteCreate
            41 -> InviteUpdate
            42 -> InviteDelete
            50 -> WebhookCreate
            51 -> WebhookUpdate
            52 -> WebhookDelete
            60 -> EmojiCreate
            61 -> EmojiUpdate
            62 -> EmojiDelete
            72 -> MessageDelete
            73 -> MessageBulkDelete
            74 -> MessagePin
            75 -> MessageUnpin
            80 -> IntegrationCreate
            81 -> IntegrationUpdate
            82 -> IntegrationDelete
            83 -> StageInstanceCreate
            84 -> StageInstanceUpdate
            85 -> StageInstanceDelete
            90 -> StickerCreate
            91 -> StickerUpdate
            92 -> StickerDelete
            100 -> GuildScheduledEventCreate
            101 -> GuildScheduledEventUpdate
            102 -> GuildScheduledEventDelete
            110 -> ThreadCreate
            111 -> ThreadUpdate
            112 -> ThreadDelete
            else -> Unknown(value)
        }
    }

}
