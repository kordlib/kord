package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.AttachmentData
import java.util.*

/**
 * An instance of a [Discord Attachment](https://discord.com/developers/docs/resources/channel#attachment-object).
 *
 * A file attached to a [Message].
 */
data class Attachment(val data: AttachmentData, override val kord: Kord) : Entity {

    override val id: Snowflake
        get() = Snowflake(data.id)

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
    val height: Int? get() = data.height

    /**
     * The width of the file, if it is an image.
     */
    val width: Int? get() = data.width

    /**
     * If this file is displayed as a spoiler. Denoted by the `SPOILER_` prefix in the name.
     */
    val isSpoiler: Boolean get() = filename.startsWith("SPOILER_")

    /**
     * If this file is an image. Denoted by the presence of a [width] and [height].
     */
    val isImage: Boolean get() = height == width && height == null

    override fun hashCode(): Int = Objects.hash(id)

    override fun equals(other: Any?): Boolean = when(other) {
        is Attachment -> other.id == id
        else -> false
    }

}