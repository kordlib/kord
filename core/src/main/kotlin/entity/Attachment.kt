package dev.kord.core.entity

import dev.kord.common.entity.DiscordAttachment
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.value
import dev.kord.core.Kord
import dev.kord.core.cache.data.AttachmentData
import dev.kord.rest.Image
import java.util.*

/**
 * An instance of a [Discord Attachment](https://discord.com/developers/docs/resources/channel#attachment-object).
 *
 * A file attached to a [Message].
 */
public data class Attachment(val data: AttachmentData, override val kord: Kord) : KordEntity {

    override val id: Snowflake
        get() = data.id

    /**
     * The name of the file.
     */
    val filename: String get() = data.filename

    /**
     * The description for the file.
     */
    val description: String? get() = data.description.value

    /**
     * The attachment's [media type](https://en.wikipedia.org/wiki/Media_type).
     */
    val contentType: String? get() = data.contentType.value

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

    val isEphemeral: Boolean  get() = data.ephemeral.discordBoolean
    /**
     * If this file is an image. Denoted by the presence of a [width] and [height].
     */
    val isImage: Boolean get() = Image.Format.isSupported(filename)

    override fun hashCode(): Int = Objects.hash(id)

    override fun equals(other: Any?): Boolean = when (other) {
        is Attachment -> other.id == id
        else -> false
    }

    override fun toString(): String {
        return "Attachment(data=$data, kord=$kord)"
    }
}

public fun Attachment.toRawType(): DiscordAttachment {
    with(data) {
        return DiscordAttachment(id, filename, description, contentType, size, url, proxyUrl, height, width)
    }
}
