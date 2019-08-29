package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.common.entity.Attachment
import kotlinx.serialization.Serializable

@Serializable
data class AttachmentData(
        val id: String,
        val fileName: String,
        val size: Int,
        val url: String,
        val proxyUrl: String,
        val height: Int? = null,
        val width: Int? = null
) {
    companion object {
        fun from(entity: Attachment) = with(entity) {
            AttachmentData(id, fileName, size, url, proxyUrl, height, width)
        }
    }
}