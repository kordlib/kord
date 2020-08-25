package com.gitlab.kordlib.rest.builder.user

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.annotation.KordDsl
import com.gitlab.kordlib.common.annotation.KordUnstableApi
import com.gitlab.kordlib.rest.builder.RequestBuilder
import com.gitlab.kordlib.rest.json.request.GroupDMCreateRequest

@KordDsl
class GroupDMCreateBuilder : RequestBuilder<@OptIn(KordUnstableApi::class) GroupDMCreateRequest> {

    val tokens: MutableList<String> = mutableListOf()
    val nicknames: MutableMap<Snowflake, String> = mutableMapOf()

    @OptIn(KordUnstableApi::class)
    override fun toRequest(): GroupDMCreateRequest = GroupDMCreateRequest(
            tokens.toList(),
            nicknames.mapKeys { it.value }
    )

}