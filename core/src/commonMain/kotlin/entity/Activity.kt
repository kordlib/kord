package dev.kord.core.entity

import dev.kord.common.entity.ActivityFlags
import dev.kord.common.entity.ActivityType
import dev.kord.common.entity.DiscordActivityEmoji
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.value
import dev.kord.core.cache.data.ActivityData
import kotlinx.datetime.Instant

public class Activity(public val data: ActivityData) {

    public val name: String get() = data.name
    public val type: ActivityType get() = data.type
    public val url: String? get() = data.url.value
    public val start: Instant? get() = data.timestamps.value?.start?.value

    public val end: Instant? get() = data.timestamps.value?.end?.value

    public val applicationId: Snowflake? get() = data.applicationId.value

    public val details: String? get() = data.details.value

    public val emoji: DiscordActivityEmoji? get() = data.emoji.value

    public val state: String? get() = data.state.value

    public val party: Party?
        get() = data.party.value?.let {
            Party(
                it.id.value,
                it.size.value?.current,
                it.size.value?.maximum
            )
        }

    public val assets: Assets
        get() = Assets(
            data.assets.value?.largeImage?.value,
            data.assets.value?.largeText?.value,
            data.assets.value?.smallImage?.value,
            data.assets.value?.smallText?.value
        )

    public val secrets: Secrets
        get() = Secrets(
            data.secrets.value?.join?.value,
            data.secrets.value?.join?.value,
            data.secrets.value?.join?.value
        )

    public val isInstance: Boolean? get() = data.instance.value

    public val flags: ActivityFlags?
        get() = data.flags.value

    public val buttons: List<String>?
        get() = data.buttons.value

    override fun toString(): String {
        return "Activity(data=$data)"
    }

    public data class Party(val id: String?, val currentSize: Int?, val maxSize: Int?)
    public data class Assets(val largeImage: String?, val largeText: String?, val smallImage: String?, val smallText: String?)
    public data class Secrets(val join: String?, val spectate: String?, val match: String?)

}
