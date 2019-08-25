package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.`object`.Image
import com.gitlab.kordlib.core.`object`.data.AttachmentData

data class Attachment(val data: AttachmentData, override val kord: Kord /*TODO remove kord? Might want to keep it for io stuff*/) : Entity {

    override val id: Snowflake
        get() = Snowflake(data.id)

    val fileName: String get() = data.fileName

    val size: Int get() = data.size

    val url: String get() = data.url

    val proxyUrl: String get() = data.proxyUrl

    val height: Int? get() = data.height

    val width: Int? get() = data.width

    val isSpoiler: Boolean get() = fileName.startsWith("SPOILER_")

    val isImage: Boolean get() = height == width && height == null

}