package com.gitlab.kordlib.core.`object`.builder.guild

import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.rest.json.request.EmojiModifyRequest

class UpdateGuildEmojiBuilder (        var name: String? = null,
        val roles: MutableSet<Snowflake> = mutableSetOf()
) {
    fun toRequest(): EmojiModifyRequest = EmojiModifyRequest(
            name = name,
            roles = roles.map { it.value }
    )
}