package com.gitlab.kordlib.rest.builder.user

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.rest.builder.KordDsl
import com.gitlab.kordlib.rest.builder.RequestBuilder
import com.gitlab.kordlib.rest.json.request.GroupDMCreateRequest

@KordDsl
class GroupDMCreateBuilder : RequestBuilder<GroupDMCreateRequest> {

    val tokens: MutableList<String> = mutableListOf()
    val nicknames: MutableMap<Snowflake, String> = mutableMapOf()

    override fun toRequest(): GroupDMCreateRequest = GroupDMCreateRequest(
            tokens.toList(),
            nicknames.mapKeys { it.value }
    )

}