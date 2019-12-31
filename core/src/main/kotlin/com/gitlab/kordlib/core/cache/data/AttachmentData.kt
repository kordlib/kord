package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.common.entity.Attachment
import kotlinx.serialization.Serializable

@Serializable
data class AttachmentData(
        val id: Long,
        val filename: String,
        val size: Int,
        val url: String,
        val proxyUrl: String,
        val height: Int? = null,
        val width: Int? = null
) {
    companion object {
        fun from(entity: Attachment) = with(entity) {
            AttachmentData(id.toLong(), filename ?: "", size, url, proxyUrl, height, width)
        }
    }
}