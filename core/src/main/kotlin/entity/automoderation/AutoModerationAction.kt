package dev.kord.core.entity.automoderation

import dev.kord.common.entity.AutoModerationActionType
import dev.kord.common.entity.AutoModerationActionType.*
import dev.kord.common.entity.Snowflake
import dev.kord.core.cache.data.AutoModerationActionData
import dev.kord.core.entity.channel.GuildChannel
import kotlin.time.Duration

/** An action which will execute whenever an [AutoModerationRule] is triggered. */
public sealed class AutoModerationAction(
    public val data: AutoModerationActionData,
    expectedActionType: AutoModerationActionType?,
) {
    init {
        if (expectedActionType == null) {
            require(data.type is Unknown) { "Expected unknown action type but got ${data.type}" }
        } else {
            require(data.type == expectedActionType) {
                "Wrong action type, expected $expectedActionType but got ${data.type}"
            }
        }
    }

    /** The type of action. */
    public abstract val type: AutoModerationActionType
}

internal fun AutoModerationAction(data: AutoModerationActionData) = when (data.type) {
    BlockMessage -> BlockMessageAutoModerationAction(data)
    SendAlertMessage -> SendAlertMessageAutoModerationAction(data)
    Timeout -> TimeoutAutoModerationAction(data)
    is Unknown -> UnknownAutoModerationAction(data)
}

/** An [AutoModerationAction] of type [BlockMessage]. */
public class BlockMessageAutoModerationAction(
    data: AutoModerationActionData,
) : AutoModerationAction(data, expectedActionType = BlockMessage) {

    override val type: BlockMessage get() = BlockMessage

    override fun toString(): String = "BlockMessageAutoModerationAction(data=$data)"
}

/** An [AutoModerationAction] of type [SendAlertMessage]. */
public class SendAlertMessageAutoModerationAction(
    data: AutoModerationActionData,
) : AutoModerationAction(data, expectedActionType = SendAlertMessage) {

    override val type: SendAlertMessage get() = SendAlertMessage

    /** The ID of the [GuildChannel] to which user content should be logged. */
    public val channelId: Snowflake get() = data.metadata.value!!.channelId.value!!

    override fun toString(): String = "SendAlertMessageAutoModerationAction(data=$data)"
}

/** An [AutoModerationAction] of type [Timeout]. */
public class TimeoutAutoModerationAction(
    data: AutoModerationActionData,
) : AutoModerationAction(data, expectedActionType = Timeout) {

    override val type: Timeout get() = Timeout

    /** The timeout duration. */
    public val duration: Duration get() = data.metadata.value!!.durationSeconds.value!!

    override fun toString(): String = "TimeoutAutoModerationAction(data=$data)"
}

/** An [AutoModerationAction] of type [Unknown]. */
public class UnknownAutoModerationAction(
    data: AutoModerationActionData,
) : AutoModerationAction(data, expectedActionType = null) {

    override val type: Unknown get() = data.type as Unknown

    override fun toString(): String = "UnknownAutoModerationAction(data=$data)"
}
