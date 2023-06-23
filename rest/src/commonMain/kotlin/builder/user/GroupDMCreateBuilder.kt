package dev.kord.rest.builder.user

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.Snowflake
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.GroupDMCreateRequest

@KordDsl
public class GroupDMCreateBuilder : RequestBuilder<GroupDMCreateRequest> {

    public val tokens: MutableList<String> = mutableListOf()
    public val nicknames: MutableMap<Snowflake, String> = mutableMapOf()

    override fun toRequest(): GroupDMCreateRequest = GroupDMCreateRequest(
        tokens.toList(),
        nicknames.mapKeys { it.value }
    )
}
