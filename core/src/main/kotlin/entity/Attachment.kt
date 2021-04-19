package dev.kord.core.entity

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.value
import dev.kord.core.Kord
import dev.kord.core.cache.data.AttachmentData
import java.util.*

/**
 * An instance of a [Discord Attachment](https://discord.com/developers/docs/resources/channel#attachment-object).
 *
 * A file attached to a [Message].
 */
data class Attachment(val data: AttachmentData, override val kord: Kord) : KordEntity {

    override val id: Snowflake
        get() = data.id

    /**
     * The name of the file.
     */
    val filename: String get() = data.filename

    /**
     * The size of the file in bytes.
     */
    val size: Int get() = data.size

    /**
     * The url of the file.
     */
    val url: String get() = data.url

    /**
     * The proxied url of the file.
     */
    val proxyUrl: String get() = data.proxyUrl

    /**
     * The height of the file, if it is an image.
     */
    val height: Int? get() = data.height.value

    /**
     * The width of the file, if it is an image.
     */
    val width: Int? get() = data.width.value

    /**
     * If this file is displayed as a spoiler. Denoted by the `SPOILER_` prefix in the name.
     */
    val isSpoiler: Boolean get() = filename.startsWith("SPOILER_")

    /**
     * If this file is an image. Denoted by the presence of a [width] and [height].
     */
    val isImage: Boolean get() = height != null && width != null

    override fun hashCode(): Int = Objects.hash(id)

    override fun equals(other: Any?): Boolean = when (other) {
        is Attachment -> other.id == id
        else -> false
    }

    override fun toString(): String {
        return "Attachment(data=$data, kord=$kord)"
    }

}