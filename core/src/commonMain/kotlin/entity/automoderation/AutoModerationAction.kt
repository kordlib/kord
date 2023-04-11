package dev.kord.core.entity.automoderation

import dev.kord.common.entity.AutoModerationActionType
import dev.kord.common.entity.AutoModerationActionType.*
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.cache.data.AutoModerationActionData
import dev.kord.core.entity.channel.GuildMessageChannel
import kotlin.time.Duration

/** An action which will execute whenever an [AutoModerationRule] is triggered. */
public sealed class AutoModerationAction(
    public val data: AutoModerationActionData,
    final override val kord: Kord,
    expectedActionType: AutoModerationActionType?,
) : KordObject {
    init {
        val type = data.type
        if (expectedActionType == null) {
            require(type is Unknown) { "Expected unknown action type but got $type" }
        } else {
            require(type == expectedActionType) { "Wrong action type, expected $expectedActionType but got $type" }
        }
    }

    /** The type of action. */
    public abstract val type: AutoModerationActionType

    abstract override fun toString(): String
}

internal fun AutoModerationAction(data: AutoModerationActionData, kord: Kord) = when (data.type) {
    BlockMessage -> BlockMessageAutoModerationAction(data, kord)
    SendAlertMessage -> SendAlertMessageAutoModerationAction(data, kord)
    Timeout -> TimeoutAutoModerationAction(data, kord)
    is Unknown -> UnknownAutoModerationAction(data, kord)
}

/** An [AutoModerationAction] of type [BlockMessage]. */
public class BlockMessageAutoModerationAction(
    data: AutoModerationActionData,
    kord: Kord,
) : AutoModerationAction(data, kord, expectedActionType = BlockMessage) {

    override val type: BlockMessage get() = BlockMessage

    /** Additional explanation that will be shown to members whenever their message is blocked. */
    public val customMessage: String? get() = data.metadata.value?.customMessage?.value

    override fun toString(): String = "BlockMessageAutoModerationAction(data=$data)"
}

/** An [AutoModerationAction] of type [SendAlertMessage]. */
public class SendAlertMessageAutoModerationAction(
    data: AutoModerationActionData,
    kord: Kord,
) : AutoModerationAction(data, kord, expectedActionType = SendAlertMessage) {

    override val type: SendAlertMessage get() = SendAlertMessage

    /** The ID of the [GuildMessageChannel] to which user content should be logged. */
    public val channelId: Snowflake get() = data.metadata.value!!.channelId.value!!

    override fun toString(): String = "SendAlertMessageAutoModerationAction(data=$data)"
}

/** An [AutoModerationAction] of type [Timeout]. */
public class TimeoutAutoModerationAction(
    data: AutoModerationActionData,
    kord: Kord,
) : AutoModerationAction(data, kord, expectedActionType = Timeout) {

    override val type: Timeout get() = Timeout

    /** The timeout duration. */
    public val duration: Duration get() = data.metadata.value!!.durationSeconds.value!!

    override fun toString(): String = "TimeoutAutoModerationAction(data=$data)"
}

/** An [AutoModerationAction] of type [Unknown]. */
public class UnknownAutoModerationAction(
    data: AutoModerationActionData,
    kord: Kord,
) : AutoModerationAction(data, kord, expectedActionType = null) {

    override val type: Unknown get() = data.type as Unknown

    override fun toString(): String = "UnknownAutoModerationAction(data=$data)"
}
