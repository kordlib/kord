package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.orEmpty
import dev.kord.common.serialization.DurationInDaysSerializer
import dev.kord.common.serialization.DurationInSecondsSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.int
import kotlin.time.Duration
import dev.kord.common.Color as CommonColor
import dev.kord.common.entity.DefaultMessageNotificationLevel as CommonDefaultMessageNotificationLevel
import dev.kord.common.entity.ExplicitContentFilter as CommonExplicitContentFilter
import dev.kord.common.entity.MFALevel as CommonMFALevel
import dev.kord.common.entity.Permissions as CommonPermissions
import dev.kord.common.entity.VerificationLevel as CommonVerificationLevel

@Serializable
public data class DiscordAuditLog(
    val webhooks: List<DiscordWebhook>,
    val users: List<DiscordUser>,
    @SerialName("audit_log_entries")
    val auditLogEntries: List<DiscordAuditLogEntry>,
    val integrations: List<DiscordPartialIntegration>,
    val threads: List<DiscordChannel>
)

@Serializable
public data class DiscordAuditLogEntry(
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
    public operator fun <T> get(value: AuditLogChangeKey<T>): AuditLogChange<T>? =
        changes.orEmpty().firstOrNull { it.key == value } as? AuditLogChange<T>

}

@Serializable
public data class AuditLogEntryOptionalInfo(
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
public data class AuditLogChange<T>(
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

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
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
            AuditLogChange(
                new = newVal,
                old = oldVal,
                key = key as AuditLogChangeKey<Any?>
            ) as AuditLogChange<T>
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
public sealed class AuditLogChangeKey<T>(public val name: String, public val serializer: KSerializer<T>) {

    override fun toString(): String = "AuditLogChangeKey(name=$name)"

    public class Unknown(name: String) : AuditLogChangeKey<JsonElement>(name, JsonElement.serializer())

    @SerialName("name")
    public object Name : AuditLogChangeKey<String>("name", serializer())

    @SerialName("icon_hash")
    public object IconHash : AuditLogChangeKey<String>("icon_hash", serializer())

    @SerialName("image_hash")
    public object ImageHash : AuditLogChangeKey<String>("image_hash", serializer())

    @SerialName("splash_hash")
    public object SplashHash : AuditLogChangeKey<String>("splash_hash", serializer())

    @SerialName("owner_id")
    public object OwnerId : AuditLogChangeKey<Snowflake>("owner_id", serializer())

    @SerialName("region")
    public object Region : AuditLogChangeKey<String>("region", serializer())

    @SerialName("afk_channel_id")
    public object AfkChannelId : AuditLogChangeKey<Snowflake>("afk_channel_id", serializer())

    @SerialName("afk_timeout")
    public object AfkTimeout : AuditLogChangeKey<Duration>("afk_timeout", DurationInSecondsSerializer)

    @SerialName("mfa_level")
    public object MFALevel : AuditLogChangeKey<CommonMFALevel>("mfa_level", serializer())

    @SerialName("verification_level")
    public object VerificationLevel : AuditLogChangeKey<CommonVerificationLevel>("verification_level", serializer())

    @SerialName("explicit_content_filter")
    public object ExplicitContentFilter :
        AuditLogChangeKey<CommonExplicitContentFilter>("explicit_content_filter", serializer())

    @SerialName("default_message_notifications")
    public object DefaultMessageNotificationLevel :
        AuditLogChangeKey<CommonDefaultMessageNotificationLevel>("default_message_notifications", serializer())

    @SerialName("vanity_url_code")
    public object VanityUrlCode : AuditLogChangeKey<String>("vanity_url_code", serializer())

    @SerialName("\$add")
    public object Add : AuditLogChangeKey<List<DiscordPartialRole>>("\$add", serializer())

    @SerialName("\$remove")
    public object Remove : AuditLogChangeKey<List<DiscordPartialRole>>("\$remove", serializer())

    @SerialName("prune_delete_days")
    public object PruneDeleteDays : AuditLogChangeKey<Int>("prune_delete_days", serializer())

    @SerialName("widget_enabled")
    public object WidgetEnabled : AuditLogChangeKey<Boolean>("widget_enabled", serializer())

    @SerialName("widget_channel_id")
    public object WidgetChannelId : AuditLogChangeKey<Snowflake>("widget_channel_id", serializer())

    @SerialName("system_channel_id")
    public object SystemChannelId : AuditLogChangeKey<Snowflake>("system_channel_id", serializer())

    @SerialName("position")
    public object Position : AuditLogChangeKey<Int>("position", serializer())

    @SerialName("topic")
    public object Topic : AuditLogChangeKey<String>("topic", serializer())

    @SerialName("bitrate")
    public object Bitrate : AuditLogChangeKey<Int>("bitrate", serializer())

    @SerialName("permission_overwrites")
    public object PermissionOverwrites : AuditLogChangeKey<List<Overwrite>>("permission_overwrites", serializer())

    @SerialName("nsfw")
    public object Nsfw : AuditLogChangeKey<Boolean>("nsfw", serializer())

    @SerialName("application_id")
    public object ApplicationId : AuditLogChangeKey<Snowflake>("application_id", serializer())

    @SerialName("rate_limit_per_user")
    public object RateLimitPerUser : AuditLogChangeKey<Duration>("rate_limit_per_user", DurationInSecondsSerializer)

    @SerialName("permissions")
    public object Permissions : AuditLogChangeKey<CommonPermissions>("permissions", serializer())

    @SerialName("color")
    public object Color : AuditLogChangeKey<CommonColor>("color", serializer())

    @SerialName("command_id")
    public object CommandId : AuditLogChangeKey<Snowflake>("command_id", serializer())

    @SerialName("communication_disabled_until")
    public object CommunicationDisabledUntil : AuditLogChangeKey<Instant>("communication_disabled_until", serializer())

    @SerialName("hoist")
    public object Hoist : AuditLogChangeKey<Boolean>("hoist", serializer())

    @SerialName("mentionable")
    public object Mentionable : AuditLogChangeKey<Boolean>("mentionable", serializer())

    @SerialName("allow")
    public object Allow : AuditLogChangeKey<CommonPermissions>("allow", serializer())

    @SerialName("deny")
    public object Deny : AuditLogChangeKey<CommonPermissions>("deny", serializer())

    @SerialName("code")
    public object Code : AuditLogChangeKey<String>("code", serializer())

    @SerialName("channel_id")
    public object ChannelId : AuditLogChangeKey<Snowflake>("channel_id", serializer())

    @SerialName("inviter_id")
    public object InviterId : AuditLogChangeKey<Snowflake>("inviter_id", serializer())

    @SerialName("location")
    public object Location : AuditLogChangeKey<String>("location", serializer())

    @SerialName("max_uses")
    public object MaxUses : AuditLogChangeKey<Int>("max_uses", serializer())

    @SerialName("uses")
    public object Uses : AuditLogChangeKey<Int>("uses", serializer())

    @SerialName("max_age")
    public object MaxAges : AuditLogChangeKey<Duration>("max_age", DurationInSecondsSerializer)

    @SerialName("temporary")
    public object Temporary : AuditLogChangeKey<Boolean>("temporary", serializer())

    @SerialName("deaf")
    public object Deaf : AuditLogChangeKey<Boolean>("deaf", serializer())

    @SerialName("mute")
    public object Mute : AuditLogChangeKey<Boolean>("mute", serializer())

    @SerialName("nick")
    public object Nick : AuditLogChangeKey<String>("nick", serializer())

    @SerialName("avatar_hash")
    public object AvatarHash : AuditLogChangeKey<String>("avatar_hash", serializer())

    @SerialName("id")
    public object Id : AuditLogChangeKey<Snowflake>("id", serializer())

    /**
     * The actual supertype is [AuditLogChangeKey<Int | String>][AuditLogChangeKey] but Kotlin does not support union
     * types yet. [Int]s are instead converted to a [String].
     */
    @SerialName("type")
    public object Type : AuditLogChangeKey<String>("type", IntOrStringSerializer) {
        // TODO use union type `String | Int` if Kotlin ever introduces them

        // Audit Log Change Key "type" has integer or string values, so we need some sort of union serializer
        // (see https://discord.com/developers/docs/resources/audit-log#audit-log-entry-object-audit-log-change-key)
        private object IntOrStringSerializer : KSerializer<String> {
            private val backingSerializer = JsonPrimitive.serializer()

            /*
             * Delegating serializers should not reuse descriptors:
             * https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/serializers.md#delegating-serializers
             *
             * however `SerialDescriptor("...", backingSerializer.descriptor)` will throw since
             * `JsonPrimitive.serializer().kind` is `PrimitiveKind.STRING` (`SerialDescriptor()` does not allow
             * `PrimitiveKind`)
             * -> use `PrimitiveSerialDescriptor("...", PrimitiveKind.STRING)` instead
             */
            override val descriptor = PrimitiveSerialDescriptor(
                serialName = "dev.kord.common.entity.AuditLogChangeKey.Type.IntOrString",
                PrimitiveKind.STRING,
            )

            override fun serialize(encoder: Encoder, value: String) {
                val jsonPrimitive = value.toIntOrNull()?.let { JsonPrimitive(it) } ?: JsonPrimitive(value)
                encoder.encodeSerializableValue(backingSerializer, jsonPrimitive)
            }

            override fun deserialize(decoder: Decoder): String {
                val jsonPrimitive = decoder.decodeSerializableValue(backingSerializer)
                return if (jsonPrimitive.isString) jsonPrimitive.content else jsonPrimitive.int.toString()
            }
        }
    }

    @SerialName("enable_emoticons")
    public object EnableEmoticons : AuditLogChangeKey<Boolean>("enable_emoticons", serializer())

    @SerialName("expire_behavior")
    public object ExpireBehavior : AuditLogChangeKey<IntegrationExpireBehavior>("expire_behavior", serializer())

    @SerialName("expire_grace_period")
    public object ExpireGracePeriod : AuditLogChangeKey<Duration>("expire_grace_period", DurationInDaysSerializer)

    @SerialName("user_limit")
    public object UserLimit : AuditLogChangeKey<Int>("user_limit", serializer())

    @SerialName("archived")
    public object Archived : AuditLogChangeKey<Boolean>("archived", serializer())

    @SerialName("locked")
    public object Locked : AuditLogChangeKey<Boolean>("locked", serializer())

    @SerialName("auto_archive_duration")
    public object AutoArchiveDuration : AuditLogChangeKey<ArchiveDuration>("auto_archive_duration", serializer())

    @SerialName("default_auto_archive_duration")
    public object DefaultAutoArchiveDuration :
        AuditLogChangeKey<ArchiveDuration>("default_auto_archive_duration", serializer())

    @SerialName("entity_type")
    public object EntityType : AuditLogChangeKey<ScheduledEntityType>(
        "entity_type",
        serializer()
    )

    @SerialName("status")
    public object Status : AuditLogChangeKey<GuildScheduledEventStatus>(
        "status",
        serializer()
    )

    @SerialName("sku_ids")
    public object SkuIds : AuditLogChangeKey<List<Snowflake>>(
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
                "image_hash" -> ImageHash
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
                "command_id" -> CommandId
                "communication_disabled_until" -> CommunicationDisabledUntil
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
public sealed class AuditLogEvent(public val value: Int) {
    public class Unknown(value: Int) : AuditLogEvent(value)
    public object GuildUpdate : AuditLogEvent(1)
    public object ChannelCreate : AuditLogEvent(10)
    public object ChannelUpdate : AuditLogEvent(11)
    public object ChannelDelete : AuditLogEvent(12)
    public object ChannelOverwriteCreate : AuditLogEvent(13)
    public object ChannelOverwriteUpdate : AuditLogEvent(14)
    public object ChannelOverwriteDelete : AuditLogEvent(15)
    public object MemberKick : AuditLogEvent(20)
    public object MemberPrune : AuditLogEvent(21)
    public object MemberBanAdd : AuditLogEvent(22)
    public object MemberBanRemove : AuditLogEvent(23)
    public object MemberUpdate : AuditLogEvent(24)
    public object MemberRoleUpdate : AuditLogEvent(25)
    public object MemberMove : AuditLogEvent(26)
    public object MemberDisconnect : AuditLogEvent(27)
    public object BotAdd : AuditLogEvent(28)
    public object RoleCreate : AuditLogEvent(30)
    public object RoleUpdate : AuditLogEvent(31)
    public object RoleDelete : AuditLogEvent(32)
    public object InviteCreate : AuditLogEvent(40)
    public object InviteUpdate : AuditLogEvent(41)
    public object InviteDelete : AuditLogEvent(42)
    public object WebhookCreate : AuditLogEvent(50)
    public object WebhookUpdate : AuditLogEvent(51)
    public object WebhookDelete : AuditLogEvent(52)
    public object EmojiCreate : AuditLogEvent(60)
    public object EmojiUpdate : AuditLogEvent(61)
    public object EmojiDelete : AuditLogEvent(62)
    public object MessageDelete : AuditLogEvent(72)
    public object MessageBulkDelete : AuditLogEvent(73)
    public object MessagePin : AuditLogEvent(74)
    public object MessageUnpin : AuditLogEvent(75)
    public object IntegrationCreate : AuditLogEvent(80)
    public object IntegrationUpdate : AuditLogEvent(81)
    public object IntegrationDelete : AuditLogEvent(82)
    public object StageInstanceCreate : AuditLogEvent(83)
    public object StageInstanceUpdate : AuditLogEvent(84)
    public object StageInstanceDelete : AuditLogEvent(85)
    public object StickerCreate : AuditLogEvent(90)
    public object StickerUpdate : AuditLogEvent(91)
    public object StickerDelete : AuditLogEvent(92)
    public object GuildScheduledEventCreate : AuditLogEvent(100)
    public object GuildScheduledEventUpdate : AuditLogEvent(101)
    public object GuildScheduledEventDelete : AuditLogEvent(102)
    public object ThreadCreate : AuditLogEvent(110)
    public object ThreadUpdate : AuditLogEvent(111)
    public object ThreadDelete : AuditLogEvent(112)
    public object ApplicationCommandPermissionUpdate : AuditLogEvent(121)


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
            121 -> ApplicationCommandPermissionUpdate
            else -> Unknown(value)
        }
    }

}
