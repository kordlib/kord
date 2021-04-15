package dev.kord.rest.builder.user

import dev.kord.common.entity.Snowflake
import dev.kord.common.annotation.KordDsl
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.GroupDMCreateRequest

@KordDsl
class GroupDMCreateBuilder : RequestBuilder<GroupDMCreateRequest> {

    val tokens: MutableList<String> = mutableListOf()
    val nicknames: MutableMap<Snowflake, String> = mutableMapOf()

    override fun toRequest(): GroupDMCreateRequest = GroupDMCreateRequest(
        tokens.toList(),
        nicknames.mapKeys { it.value }
    )

}