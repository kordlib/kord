package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.AttachmentData

data class Attachment(val data: AttachmentData, override val kord: Kord) : Entity {

    override val id: Snowflake
        get() = Snowflake(data.id)

    val filename: String get() = data.filename

    val size: Int get() = data.size

    val url: String get() = data.url

    val proxyUrl: String get() = data.proxyUrl

    val height: Int? get() = data.height

    val width: Int? get() = data.width

    val isSpoiler: Boolean get() = filename.startsWith("SPOILER_")

    val isImage: Boolean get() = height == width && height == null

}