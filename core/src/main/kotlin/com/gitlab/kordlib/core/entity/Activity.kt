package dev.kord.core.entity

import dev.kord.common.annotation.DeprecatedSinceKord
import dev.kord.common.entity.*
import dev.kord.common.entity.optional.value
import dev.kord.core.cache.data.ActivityData
import dev.kord.core.toInstant
import java.time.Instant

class Activity(val data: ActivityData) {

    val name: String get() = data.name
    val type: ActivityType get() = data.type
    val url: String? get() = data.url.value
    val start: Instant? get() = data.timestamps.value?.start.value?.toInstant()

    @DeprecatedSinceKord("0.7.0")
    @Deprecated("stop was renamed to end.", ReplaceWith("end"), DeprecationLevel.ERROR)
    val stop: Instant? by ::end

    val end: Instant? get() = data.timestamps.value?.end.value?.toInstant()

    val applicationId: Snowflake? get() = data.applicationId.value

    val details: String? get() = data.details.value

    val emoji: DiscordActivityEmoji? get() = data.emoji.value

    val state: String? get() = data.state.value

    val party: Party? get() = data.party.value?.let { Party(it.id.value, it.size.value?.current, it.size.value?.maximum) }

    val assets: Assets? get() = Assets(
            data.assets.value?.largeImage?.value,
            data.assets.value?.largeText?.value,
            data.assets.value?.smallImage?.value,
            data.assets.value?.smallText?.value
    )

    val secrets: Secrets? get() = Secrets(data.secrets.value?.join?.value, data.secrets.value?.join?.value, data.secrets.value?.join?.value)

    val isInstance: Boolean? get() = data.instance.value

    val flags: ActivityFlags?
        get() = data.flags.value

    override fun toString(): String {
        return "Activity(data=$data)"
    }

    data class Party(val id: String?, val currentSize: Int?, val maxSize: Int?)
    data class Assets(val largeImage: String?, val largeText: String?, val smallImage: String?, val smallText: String?)
    data class Secrets(val join: String?, val spectate: String?, val match: String?)

}
