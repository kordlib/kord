@file:Generate(
    INT_KORD_ENUM, name = "StageInstancePrivacyLevel",
    docUrl = "https://discord.com/developers/docs/resources/stage-instance#stage-instance-object-privacy-level",
    entries = [
        Entry(
            "Public", intValue = 1, kDoc = "The Stage instance is visible publicly.",
            deprecated = Deprecated(
                "Stages are no longer discoverable. The deprecation level will be raised" +
                    " to HIDDEN in 0.19.0 and this declaration will be removed in 0.20.0",
                level = DeprecationLevel.ERROR
            ),
        ),
        Entry("GuildOnly", intValue = 2, kDoc = "The Stage instance is visible to only guild members."),
    ],
)

package dev.kord.common.entity

import dev.kord.ksp.Generate
import dev.kord.ksp.Generate.EntityType.INT_KORD_ENUM
import dev.kord.ksp.Generate.Entry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


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
    @Deprecated(
        "Stages are no longer discoverable. The deprecation level will be raised to HIDDEN in 0.19.0" +
            " and this declaration will be removed in 0.20.0", level = DeprecationLevel.ERROR
    )
    @SerialName("discoverable_disabled")
    val discoverableDisabled: Boolean,
    @SerialName("guild_scheduled_event_id")
    val guildScheduledEventId: Snowflake?,
)
