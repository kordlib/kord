package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordMessageSnapshot
import kotlinx.serialization.Serializable


@Serializable
public data class MessageSnapshotData(val message: MessageData) {
    public companion object {
        public fun from(snapshot: DiscordMessageSnapshot): MessageSnapshotData =
            MessageSnapshotData(MessageData.from(snapshot.message))
    }
}
