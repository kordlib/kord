package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.common.entity.ActivityType
import com.gitlab.kordlib.common.entity.DiscordPartialEmoji
import com.gitlab.kordlib.core.cache.data.ActivityData
import com.gitlab.kordlib.core.toInstant
import java.time.Instant

class Activity(val data: ActivityData) {

    val name: String get() = data.name
    val type: ActivityType get() = data.type
    val url: String? get() = data.url
    val start: Instant? get() = data.start?.toInstant()
    val stop: Instant? get() = data.stop?.toInstant()
    val applicationId: String? get() = data.applicationId
    val details: String? get() = data.details
    val emoji: DiscordPartialEmoji? get() = data.emoji
    val state: String? get() = data.state
    val party: Party? get() = data.partyId?.let { Party(it, data.partyCurrentSize!!, data.partyMaxSize!!) }
    val assets: Assets? get() = Assets(data.largeImage, data.largeText, data.smallImage, data.smallText)
    val secrets: Secrets? get() = Secrets(data.secretsJoin, data.secretsSpectate, data.secretsMatch)
    val isInstance: Boolean? get() = data.instance
    val flags: ActivityFlags
        get() = ActivityFlags(data.flags ?: 0)

    internal fun toGatewayActivity() =
            com.gitlab.kordlib.common.entity.DiscordActivity(name, type, url)

    override fun toString(): String {
        return "Activity(data=$data)"
    }

    data class Party(val id: String, val currentSize: Int, val maxSize: Int)
    data class Assets(val largeImage: String?, val largeText: String?, val smallImage: String?, val smallText: String?)
    data class Secrets(val join: String?, val spectate: String?, val match: String?)

}

inline class ActivityFlags(val code: Int) {
    operator fun contains(flag: ActivityFlag) = code.shr(flag.position).and(1) == 1
}

enum class ActivityFlag(val position: Int) {
    Instance(0),
    Join(1),
    Spectate(2),
    JoinRequest(3),
    Sync(4),
    Play(5)
}
