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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as GroupDMCreateBuilder

        if (tokens != other.tokens) return false
        if (nicknames != other.nicknames) return false

        return true
    }

    override fun hashCode(): Int {
        var result = tokens.hashCode()
        result = 31 * result + nicknames.hashCode()
        return result
    }

}
