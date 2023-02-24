@file:GenerateKordEnum(
    name = "AuditLogEvent", valueType = INT,
    docUrl = "https://discord.com/developers/docs/resources/audit-log#audit-log-entry-object-audit-log-events",
    entries = [
        Entry("GuildUpdate", intValue = 1, kDoc = "Server settings were updated."),
        Entry("ChannelCreate", intValue = 10, kDoc = "Channel was created."),
        Entry("ChannelUpdate", intValue = 11, kDoc = "Channel settings were updated."),
        Entry("ChannelDelete", intValue = 12, kDoc = "Channel was deleted."),
        Entry("ChannelOverwriteCreate", intValue = 13, kDoc = "Permission overwrite was added to a channel."),
        Entry("ChannelOverwriteUpdate", intValue = 14, kDoc = "Permission overwrite was updated for a channel."),
        Entry("ChannelOverwriteDelete", intValue = 15, kDoc = "Permission overwrite was deleted from a channel."),
        Entry("MemberKick", intValue = 20, kDoc = "Member was removed from server."),
        Entry("MemberPrune", intValue = 21, kDoc = "Members were pruned from server."),
        Entry("MemberBanAdd", intValue = 22, kDoc = "Member was banned from server."),
        Entry("MemberBanRemove", intValue = 23, kDoc = "Server ban was lifted for a member."),
        Entry("MemberUpdate", intValue = 24, kDoc = "Member was updated in server."),
        Entry("MemberRoleUpdate", intValue = 25, kDoc = "Member was added or removed from a role."),
        Entry("MemberMove", intValue = 26, kDoc = "Member was moved to a different voice channel."),
        Entry("MemberDisconnect", intValue = 27, kDoc = "Member was disconnected from a voice channel."),
        Entry("BotAdd", intValue = 28, kDoc = "Bot user was added to server."),
        Entry("RoleCreate", intValue = 30, kDoc = "Role was created."),
        Entry("RoleUpdate", intValue = 31, kDoc = "Role was edited."),
        Entry("RoleDelete", intValue = 32, kDoc = "Role was deleted."),
        Entry("InviteCreate", intValue = 40, kDoc = "Server invite was created."),
        Entry("InviteUpdate", intValue = 41, kDoc = "Server invite was updated."),
        Entry("InviteDelete", intValue = 42, kDoc = "Server invite was deleted."),
        Entry("WebhookCreate", intValue = 50, kDoc = "Webhook was created."),
        Entry("WebhookUpdate", intValue = 51, kDoc = "Webhook properties or channel were updated."),
        Entry("WebhookDelete", intValue = 52, kDoc = "Webhook was deleted."),
        Entry("EmojiCreate", intValue = 60, kDoc = "Emoji was created."),
        Entry("EmojiUpdate", intValue = 61, kDoc = "Emoji name was updated."),
        Entry("EmojiDelete", intValue = 62, kDoc = "Emoji was deleted."),
        Entry("MessageDelete", intValue = 72, kDoc = "Single message was deleted."),
        Entry("MessageBulkDelete", intValue = 73, kDoc = "Multiple messages were deleted."),
        Entry("MessagePin", intValue = 74, kDoc = "Message was pinned to a channel."),
        Entry("MessageUnpin", intValue = 75, kDoc = "Message was unpinned from a channel."),
        Entry("IntegrationCreate", intValue = 80, kDoc = "App was added to server."),
        Entry("IntegrationUpdate", intValue = 81, kDoc = "App was updated (as an example, its scopes were updated)."),
        Entry("IntegrationDelete", intValue = 82, kDoc = "App was removed from server."),
        Entry("StageInstanceCreate", intValue = 83, kDoc = "Stage instance was created (stage channel becomes live)."),
        Entry("StageInstanceUpdate", intValue = 84, kDoc = "Stage instance details were updated."),
        Entry(
            "StageInstanceDelete",
            intValue = 85,
            kDoc = "Stage instance was deleted (stage channel no longer live)."
        ),
        Entry("StickerCreate", intValue = 90, kDoc = "Sticker was created."),
        Entry("StickerUpdate", intValue = 91, kDoc = "Sticker details were updated."),
        Entry("StickerDelete", intValue = 92, kDoc = "Sticker was deleted."),
        Entry("GuildScheduledEventCreate", intValue = 100, kDoc = "Event was created."),
        Entry("GuildScheduledEventUpdate", intValue = 101, kDoc = "Event was updated."),
        Entry("GuildScheduledEventDelete", intValue = 102, kDoc = "Event was cancelled."),
        Entry("ThreadCreate", intValue = 110, kDoc = "Thread was created in a channel."),
        Entry("ThreadUpdate", intValue = 111, kDoc = "Thread was updated."),
        Entry("ThreadDelete", intValue = 112, kDoc = "Thread was deleted."),
        Entry("ApplicationCommandPermissionUpdate", intValue = 121, kDoc = "Permissions were updated for a command."),
        Entry("AutoModerationRuleCreate", intValue = 140, kDoc = "Auto Moderation rule was created."),
        Entry("AutoModerationRuleUpdate", intValue = 141, kDoc = "Auto Moderation rule was updated."),
        Entry("AutoModerationRuleDelete", intValue = 142, kDoc = "Auto Moderation rule was deleted."),
        Entry("AutoModerationBlockMessage", intValue = 143, kDoc = "Message was blocked by Auto Moderation."),
        Entry("AutoModerationFlagToChannel", intValue = 144, kDoc = "Message was flagged by Auto Moderation."),
        Entry(
            "AutoModerationUserCommunicationDisabled",
            intValue = 145,
            kDoc = "Member was timed out by Auto Moderation."
        ),
    ],
)

package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.orEmpty
import dev.kord.common.serialization.DurationInDaysSerializer
import dev.kord.common.serialization.DurationInSecondsSerializer
import dev.kord.common.serialization.LongOrStringSerializer
import dev.kord.ksp.GenerateKordEnum
import dev.kord.ksp.GenerateKordEnum.Entry
import dev.kord.ksp.GenerateKordEnum.ValueType.INT
import kotlinx.datetime.Instant
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
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
    @SerialName("application_commands")
    val applicationCommands: List<DiscordApplicationCommand>,
    @SerialName("audit_log_entries")
    val auditLogEntries: List<DiscordAuditLogEntry>,
    @SerialName("auto_moderation_rules")
    val autoModerationRules: List<DiscordAutoModerationRule>,
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
    @SerialName("application_id")
    val applicationId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("auto_moderation_rule_name")
    val autoModerationRuleName: Optional<String> = Optional.Missing(),
    @SerialName("auto_moderation_rule_trigger_type")
    val autoModerationRuleTriggerType: Optional<String> = Optional.Missing(),
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
    public object Name : AuditLogChangeKey<String>("name", String.serializer())

    @SerialName("icon_hash")
    public object IconHash : AuditLogChangeKey<String>("icon_hash", String.serializer())

    @SerialName("image_hash")
    public object ImageHash : AuditLogChangeKey<String>("image_hash", String.serializer())

    @SerialName("splash_hash")
    public object SplashHash : AuditLogChangeKey<String>("splash_hash", String.serializer())

    @SerialName("owner_id")
    public object OwnerId : AuditLogChangeKey<Snowflake>("owner_id", Snowflake.serializer())

    @SerialName("region")
    public object Region : AuditLogChangeKey<String>("region", String.serializer())

    @SerialName("afk_channel_id")
    public object AfkChannelId : AuditLogChangeKey<Snowflake>("afk_channel_id", Snowflake.serializer())

    @SerialName("afk_timeout")
    public object AfkTimeout : AuditLogChangeKey<Duration>("afk_timeout", DurationInSecondsSerializer)

    @SerialName("mfa_level")
    public object MFALevel : AuditLogChangeKey<CommonMFALevel>("mfa_level", CommonMFALevel.serializer())

    @SerialName("verification_level")
    public object VerificationLevel :
        AuditLogChangeKey<CommonVerificationLevel>("verification_level", CommonVerificationLevel.serializer())

    @SerialName("explicit_content_filter")
    public object ExplicitContentFilter : AuditLogChangeKey<CommonExplicitContentFilter>(
        "explicit_content_filter",
        CommonExplicitContentFilter.serializer()
    )

    @SerialName("default_message_notifications")
    public object DefaultMessageNotificationLevel : AuditLogChangeKey<CommonDefaultMessageNotificationLevel>(
        "default_message_notifications",
        CommonDefaultMessageNotificationLevel.serializer()
    )

    @SerialName("vanity_url_code")
    public object VanityUrlCode : AuditLogChangeKey<String>("vanity_url_code", String.serializer())

    @SerialName("\$add")
    public object Add :
        AuditLogChangeKey<List<DiscordPartialRole>>("\$add", ListSerializer(DiscordPartialRole.serializer()))

    @SerialName("\$remove")
    public object Remove :
        AuditLogChangeKey<List<DiscordPartialRole>>("\$remove", ListSerializer(DiscordPartialRole.serializer()))

    @SerialName("prune_delete_days")
    public object PruneDeleteDays : AuditLogChangeKey<Int>("prune_delete_days", Int.serializer())

    @SerialName("widget_enabled")
    public object WidgetEnabled : AuditLogChangeKey<Boolean>("widget_enabled", Boolean.serializer())

    @SerialName("widget_channel_id")
    public object WidgetChannelId : AuditLogChangeKey<Snowflake>("widget_channel_id", Snowflake.serializer())

    @SerialName("system_channel_id")
    public object SystemChannelId : AuditLogChangeKey<Snowflake>("system_channel_id", Snowflake.serializer())

    @SerialName("position")
    public object Position : AuditLogChangeKey<Int>("position", Int.serializer())

    @SerialName("topic")
    public object Topic : AuditLogChangeKey<String>("topic", String.serializer())

    @SerialName("bitrate")
    public object Bitrate : AuditLogChangeKey<Int>("bitrate", Int.serializer())

    @SerialName("permission_overwrites")
    public object PermissionOverwrites :
        AuditLogChangeKey<List<Overwrite>>("permission_overwrites", ListSerializer(Overwrite.serializer()))

    @SerialName("nsfw")
    public object Nsfw : AuditLogChangeKey<Boolean>("nsfw", Boolean.serializer())

    @SerialName("application_id")
    public object ApplicationId : AuditLogChangeKey<Snowflake>("application_id", Snowflake.serializer())

    @SerialName("rate_limit_per_user")
    public object RateLimitPerUser : AuditLogChangeKey<Duration>("rate_limit_per_user", DurationInSecondsSerializer)

    @SerialName("permissions")
    public object Permissions : AuditLogChangeKey<CommonPermissions>("permissions", CommonPermissions.serializer())

    @SerialName("color")
    public object Color : AuditLogChangeKey<CommonColor>("color", CommonColor.serializer())

    @SerialName("command_id")
    public object CommandId : AuditLogChangeKey<Snowflake>("command_id", Snowflake.serializer())

    @SerialName("communication_disabled_until")
    public object CommunicationDisabledUntil :
        AuditLogChangeKey<Instant>("communication_disabled_until", Instant.serializer())

    @SerialName("hoist")
    public object Hoist : AuditLogChangeKey<Boolean>("hoist", Boolean.serializer())

    @SerialName("mentionable")
    public object Mentionable : AuditLogChangeKey<Boolean>("mentionable", Boolean.serializer())

    @SerialName("allow")
    public object Allow : AuditLogChangeKey<CommonPermissions>("allow", CommonPermissions.serializer())

    @SerialName("deny")
    public object Deny : AuditLogChangeKey<CommonPermissions>("deny", CommonPermissions.serializer())

    @SerialName("code")
    public object Code : AuditLogChangeKey<String>("code", String.serializer())

    @SerialName("channel_id")
    public object ChannelId : AuditLogChangeKey<Snowflake>("channel_id", Snowflake.serializer())

    @SerialName("inviter_id")
    public object InviterId : AuditLogChangeKey<Snowflake>("inviter_id", Snowflake.serializer())

    @SerialName("location")
    public object Location : AuditLogChangeKey<String>("location", String.serializer())

    @SerialName("max_uses")
    public object MaxUses : AuditLogChangeKey<Int>("max_uses", Int.serializer())

    @SerialName("uses")
    public object Uses : AuditLogChangeKey<Int>("uses", Int.serializer())

    @SerialName("max_age")
    public object MaxAges : AuditLogChangeKey<Duration>("max_age", DurationInSecondsSerializer)

    @SerialName("temporary")
    public object Temporary : AuditLogChangeKey<Boolean>("temporary", Boolean.serializer())

    @SerialName("deaf")
    public object Deaf : AuditLogChangeKey<Boolean>("deaf", Boolean.serializer())

    @SerialName("mute")
    public object Mute : AuditLogChangeKey<Boolean>("mute", Boolean.serializer())

    @SerialName("nick")
    public object Nick : AuditLogChangeKey<String>("nick", String.serializer())

    @SerialName("avatar_hash")
    public object AvatarHash : AuditLogChangeKey<String>("avatar_hash", String.serializer())

    @SerialName("id")
    public object Id : AuditLogChangeKey<Snowflake>("id", Snowflake.serializer())

    /**
     * The actual supertype is [AuditLogChangeKey<Long | String>][AuditLogChangeKey] but Kotlin does not support union
     * types yet. [Long]s are instead converted to a [String].
     */
    // Audit Log Change Key "type" has integer or string values, so we need some sort of union serializer
    // (see https://discord.com/developers/docs/resources/audit-log#audit-log-entry-object-audit-log-change-key)
    // TODO use union type `String | Int` if Kotlin ever introduces them
    @SerialName("type")
    public object Type : AuditLogChangeKey<String>("type", LongOrStringSerializer)

    @SerialName("enable_emoticons")
    public object EnableEmoticons : AuditLogChangeKey<Boolean>("enable_emoticons", Boolean.serializer())

    @SerialName("expire_behavior")
    public object ExpireBehavior :
        AuditLogChangeKey<IntegrationExpireBehavior>("expire_behavior", IntegrationExpireBehavior.serializer())

    @SerialName("expire_grace_period")
    public object ExpireGracePeriod : AuditLogChangeKey<Duration>("expire_grace_period", DurationInDaysSerializer)

    @SerialName("user_limit")
    public object UserLimit : AuditLogChangeKey<Int>("user_limit", Int.serializer())

    @SerialName("archived")
    public object Archived : AuditLogChangeKey<Boolean>("archived", Boolean.serializer())

    @SerialName("locked")
    public object Locked : AuditLogChangeKey<Boolean>("locked", Boolean.serializer())

    @SerialName("auto_archive_duration")
    public object AutoArchiveDuration :
        AuditLogChangeKey<ArchiveDuration>("auto_archive_duration", ArchiveDuration.serializer())

    @SerialName("default_auto_archive_duration")
    public object DefaultAutoArchiveDuration :
        AuditLogChangeKey<ArchiveDuration>("default_auto_archive_duration", ArchiveDuration.serializer())

    @SerialName("entity_type")
    public object EntityType : AuditLogChangeKey<ScheduledEntityType>("entity_type", ScheduledEntityType.serializer())

    @SerialName("status")
    public object Status :
        AuditLogChangeKey<GuildScheduledEventStatus>("status", GuildScheduledEventStatus.serializer())

    @SerialName("sku_ids")
    public object SkuIds : AuditLogChangeKey<List<Snowflake>>("sku_ids", ListSerializer(Snowflake.serializer()))

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
