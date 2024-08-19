package dev.kord.rest.builder.automoderation

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.AutoModerationActionType
import dev.kord.common.entity.AutoModerationActionType.*
import dev.kord.common.entity.DiscordAutoModerationAction
import dev.kord.common.entity.DiscordAutoModerationActionMetadata
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.optional
import dev.kord.common.entity.optional.optionalSnowflake
import dev.kord.rest.builder.RequestBuilder
import kotlin.time.Duration

/**
 * A [RequestBuilder] for building [actions][DiscordAutoModerationAction] which will execute whenever a
 * [rule][AutoModerationRuleBuilder] is triggered.
 */
@KordDsl
public sealed class AutoModerationActionBuilder : RequestBuilder<DiscordAutoModerationAction> {

    /** The type of action. */
    public abstract val type: AutoModerationActionType

    protected open fun buildMetadata(): Optional<DiscordAutoModerationActionMetadata> = Optional.Missing()

    final override fun toRequest(): DiscordAutoModerationAction = DiscordAutoModerationAction(
        type = type,
        metadata = buildMetadata(),
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as AutoModerationActionBuilder

        return type == other.type
    }

    override fun hashCode(): Int {
        return type.hashCode()
    }

}

/** An [AutoModerationActionBuilder] for building actions with type [BlockMessage]. */
@KordDsl
public class BlockMessageAutoModerationActionBuilder : AutoModerationActionBuilder() {

    override val type: BlockMessage get() = BlockMessage

    private var _customMessage: Optional<String> = Optional.Missing()

    /**
     * Additional explanation that will be shown to members whenever their message is blocked (maximum of 150
     * characters).
     */
    public var customMessage: String? by ::_customMessage.delegate()

    override fun buildMetadata(): Optional<DiscordAutoModerationActionMetadata> {
        val customMessage = _customMessage
        return if (customMessage is Optional.Value) {
            DiscordAutoModerationActionMetadata(customMessage = customMessage).optional()
        } else {
            Optional.Missing()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        if (!super.equals(other)) return false

        other as BlockMessageAutoModerationActionBuilder

        return customMessage == other.customMessage
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (customMessage?.hashCode() ?: 0)
        return result
    }

}

/** An [AutoModerationActionBuilder] for building actions with type [SendAlertMessage]. */
@KordDsl
public class SendAlertMessageAutoModerationActionBuilder(
    /** The ID of the channel to which user content should be logged. */
    public var channelId: Snowflake,
) : AutoModerationActionBuilder() {

    override val type: SendAlertMessage get() = SendAlertMessage

    override fun buildMetadata(): Optional.Value<DiscordAutoModerationActionMetadata> =
        DiscordAutoModerationActionMetadata(channelId = channelId.optionalSnowflake()).optional()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        if (!super.equals(other)) return false

        other as SendAlertMessageAutoModerationActionBuilder

        return channelId == other.channelId
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + channelId.hashCode()
        return result
    }

}

/** An [AutoModerationActionBuilder] for building actions with type [Timeout]. */
@KordDsl
public class TimeoutAutoModerationActionBuilder(
    /** The timeout duration (maximum of 2419200 seconds (4 weeks)). */
    public var duration: Duration,
) : AutoModerationActionBuilder() {

    override val type: Timeout get() = Timeout

    override fun buildMetadata(): Optional.Value<DiscordAutoModerationActionMetadata> =
        DiscordAutoModerationActionMetadata(durationSeconds = duration.optional()).optional()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        if (!super.equals(other)) return false

        other as TimeoutAutoModerationActionBuilder

        return duration == other.duration
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + duration.hashCode()
        return result
    }

}
