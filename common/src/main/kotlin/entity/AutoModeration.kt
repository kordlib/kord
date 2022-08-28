package dev.kord.common.entity

import dev.kord.common.annotation.KordExperimental
import dev.kord.common.entity.AutoModerationRuleTriggerType.Keyword
import dev.kord.common.entity.AutoModerationRuleTriggerType.MentionSpam
import dev.kord.common.entity.Permission.ModerateMembers
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.serialization.DurationInSeconds
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
public data class DiscordAutoModerationRule(
    val id: Snowflake,
    @SerialName("guild_id")
    val guildId: Snowflake,
    val name: String,
    @SerialName("creator_id")
    val creatorId: Snowflake,
    @SerialName("event_type")
    val eventType: AutoModerationRuleEventType,
    @SerialName("trigger_type")
    val triggerType: AutoModerationRuleTriggerType,
    @SerialName("trigger_metadata")
    val triggerMetadata: DiscordAutoModerationRuleTriggerMetadata,
    val actions: List<DiscordAutoModerationAction>,
    val enabled: Boolean,
    @SerialName("exempt_roles")
    val exemptRoles: List<Snowflake>,
    @SerialName("exempt_channels")
    val exemptChannels: List<Snowflake>,
)

/** Characterizes the type of content which can trigger the rule. */
@Serializable(with = AutoModerationRuleTriggerType.Serializer::class)
public sealed class AutoModerationRuleTriggerType(public val value: Int) {

    final override fun equals(other: Any?): Boolean =
        this === other || (other is AutoModerationRuleTriggerType && this.value == other.value)

    final override fun hashCode(): Int = value


    /** An unknown [AutoModerationRuleTriggerType]. */
    public class Unknown(value: Int) : AutoModerationRuleTriggerType(value)

    /** Check if content contains words from a user defined list of keywords. */
    public object Keyword : AutoModerationRuleTriggerType(1)

    /**
     * Check if content represents generic spam.
     *
     * This [trigger type][AutoModerationRuleTriggerType] is not yet released, so it cannot be used in most servers.
     */
    @KordExperimental
    public object Spam : AutoModerationRuleTriggerType(3)

    /** Check if content contains words from internal pre-defined wordsets. */
    public object KeywordPreset : AutoModerationRuleTriggerType(4)

    /**
     * Check if content contains more mentions than allowed.
     *
     * This [trigger type][AutoModerationRuleTriggerType] is not yet released, so it cannot be used in most servers.
     */
    @KordExperimental
    public object MentionSpam : AutoModerationRuleTriggerType(5)


    internal object Serializer : KSerializer<AutoModerationRuleTriggerType> {

        override val descriptor =
            PrimitiveSerialDescriptor("dev.kord.common.entity.AutoModerationRuleTriggerType", PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, value: AutoModerationRuleTriggerType) = encoder.encodeInt(value.value)

        override fun deserialize(decoder: Decoder) = when (val value = decoder.decodeInt()) {
            1 -> Keyword
            3 -> Spam
            4 -> KeywordPreset
            5 -> MentionSpam
            else -> Unknown(value)
        }
    }
}

@Serializable
public data class DiscordAutoModerationRuleTriggerMetadata(
    @SerialName("keyword_filter")
    val keywordFilter: Optional<List<String>> = Optional.Missing(),
    val presets: Optional<List<AutoModerationRuleKeywordPresetType>> = Optional.Missing(),
    @SerialName("allow_list")
    val allowList: Optional<List<String>> = Optional.Missing(),
    @SerialName("mention_total_limit")
    val mentionTotalLimit: OptionalInt = OptionalInt.Missing,
)

/** An internally pre-defined wordset which will be searched for in content. */
@Serializable(with = AutoModerationRuleKeywordPresetType.Serializer::class)
public sealed class AutoModerationRuleKeywordPresetType(public val value: Int) {

    final override fun equals(other: Any?): Boolean =
        this === other || (other is AutoModerationRuleKeywordPresetType && this.value == other.value)

    final override fun hashCode(): Int = value


    /** An unknown [AutoModerationRuleKeywordPresetType]. */
    public class Unknown(value: Int) : AutoModerationRuleKeywordPresetType(value)

    /** Words that may be considered forms of swearing or cursing. */
    public object Profanity : AutoModerationRuleKeywordPresetType(1)

    /** Words that refer to sexually explicit behavior or activity. */
    public object SexualContent : AutoModerationRuleKeywordPresetType(2)

    /** Personal insults or words that may be considered hate speech. */
    public object Slurs : AutoModerationRuleKeywordPresetType(3)


    internal object Serializer : KSerializer<AutoModerationRuleKeywordPresetType> {

        override val descriptor =
            PrimitiveSerialDescriptor("dev.kord.common.entity.AutoModerationRuleKeywordPresetType", PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, value: AutoModerationRuleKeywordPresetType) =
            encoder.encodeInt(value.value)

        override fun deserialize(decoder: Decoder) = when (val value = decoder.decodeInt()) {
            1 -> Profanity
            2 -> SexualContent
            3 -> Slurs
            else -> Unknown(value)
        }
    }
}

/** Indicates in what event context a rule should be checked. */
@Serializable(with = AutoModerationRuleEventType.Serializer::class)
public sealed class AutoModerationRuleEventType(public val value: Int) {

    final override fun equals(other: Any?): Boolean =
        this === other || (other is AutoModerationRuleEventType && this.value == other.value)

    final override fun hashCode(): Int = value


    /** An unknown [AutoModerationRuleEventType]. */
    public class Unknown(value: Int) : AutoModerationRuleEventType(value)

    /** When a member sends or edits a message in the guild. */
    public object MessageSend : AutoModerationRuleEventType(1)


    internal object Serializer : KSerializer<AutoModerationRuleEventType> {

        override val descriptor =
            PrimitiveSerialDescriptor("dev.kord.common.entity.AutoModerationRuleEventType", PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, value: AutoModerationRuleEventType) = encoder.encodeInt(value.value)

        override fun deserialize(decoder: Decoder) = when (val value = decoder.decodeInt()) {
            1 -> MessageSend
            else -> Unknown(value)
        }
    }
}

@Serializable
public data class DiscordAutoModerationAction(
    val type: AutoModerationActionType,
    val metadata: Optional<DiscordAutoModerationActionMetadata> = Optional.Missing(),
)

/** The type of action. */
@Serializable(with = AutoModerationActionType.Serializer::class)
public sealed class AutoModerationActionType(public val value: Int) {

    final override fun equals(other: Any?): Boolean =
        this === other || (other is AutoModerationActionType && this.value == other.value)

    final override fun hashCode(): Int = value


    /** An unknown [AutoModerationActionType]. */
    public class Unknown(value: Int) : AutoModerationActionType(value)

    /** Blocks the content of a message according to the rule. */
    public object BlockMessage : AutoModerationActionType(1)

    /** Logs user content to a specified channel. */
    public object SendAlertMessage : AutoModerationActionType(2)

    /**
     * Timeout user for a specified duration.
     *
     * A [Timeout] action can only be set up for [Keyword] and [MentionSpam] rules. The [ModerateMembers] permission is
     * required to use the [Timeout] action type.
     */
    public object Timeout : AutoModerationActionType(3)


    internal object Serializer : KSerializer<AutoModerationActionType> {

        override val descriptor =
            PrimitiveSerialDescriptor("dev.kord.common.entity.AutoModerationActionType", PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, value: AutoModerationActionType) = encoder.encodeInt(value.value)

        override fun deserialize(decoder: Decoder) = when (val value = decoder.decodeInt()) {
            1 -> BlockMessage
            2 -> SendAlertMessage
            3 -> Timeout
            else -> Unknown(value)
        }
    }
}

@Serializable
public data class DiscordAutoModerationActionMetadata(
    @SerialName("channel_id")
    public val channelId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("duration_seconds")
    public val durationSeconds: Optional<DurationInSeconds> = Optional.Missing(),
)
