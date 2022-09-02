@file:GenerateKordEnum(
    name = "StageInstancePrivacyLevel", valueType = INT,
    entries = [
        Entry("GuildOnly", intValue = 2, kDoc = "The Stage instance is visible to only guild members."),
    ],
    deprecatedEntries = [
        Entry(
            "Public", intValue = 1, kDoc = "The Stage instance is visible publicly.",
            deprecationMessage = "Stages are no longer discoverable", deprecationLevel = WARNING,
        ),
    ],
)

package dev.kord.common.entity

import dev.kord.ksp.GenerateKordEnum
import dev.kord.ksp.GenerateKordEnum.Entry
import dev.kord.ksp.GenerateKordEnum.ValueType.INT
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.DeprecationLevel.WARNING


/**
 * A [_Stage Instance_](https://discord.com/developers/docs/resources/stage-instance) holds information about a live
 * stage.
 *
 * @property id The id of this Stage instance.
 * @property guildId The guild id of the associated Stage channel.
 * @property channelId The id of the associated Stage channel.
 * @property topic The topic of the Stage instance.
 * @property privacyLevel The [privacy level][StageInstancePrivacyLevel] of the Stage instance.
 * @property discoverableDisabled Whether or not Stage Discovery is disabled.
 * @property guildScheduledEventId The id of the scheduled event for this Stage instance.
 */
@Serializable
public data class DiscordStageInstance(
    val id: Snowflake,
    @SerialName("guild_id")
    val guildId: Snowflake,
    @SerialName("channel_id")
    val channelId: Snowflake,
    val topic: String,
    @SerialName("privacy_level")
    val privacyLevel: StageInstancePrivacyLevel,
    @Deprecated("Stages are no longer discoverable")
    @SerialName("discoverable_disabled")
    val discoverableDisabled: Boolean,
    @SerialName("guild_scheduled_event_id")
    val guildScheduledEventId: Snowflake?,
)
