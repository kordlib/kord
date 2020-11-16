package com.gitlab.kordlib.rest.builder.channel

import com.gitlab.kordlib.common.entity.Overwrite
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.rest.builder.AuditRequestBuilder
import com.gitlab.kordlib.common.annotation.KordDsl
import com.gitlab.kordlib.common.entity.ChannelType
import com.gitlab.kordlib.common.entity.optional.Optional
import com.gitlab.kordlib.common.entity.optional.OptionalBoolean
import com.gitlab.kordlib.common.entity.optional.OptionalInt
import com.gitlab.kordlib.common.entity.optional.OptionalSnowflake
import com.gitlab.kordlib.common.entity.optional.delegate.delegate
import com.gitlab.kordlib.rest.json.request.GuildChannelCreateRequest

@KordDsl
class NewsChannelCreateBuilder(var name: String): AuditRequestBuilder<GuildChannelCreateRequest> {
    override var reason: String? = null

    private var _topic: Optional<String> = Optional.Missing()
    var topic: String? by ::_topic.delegate()
    
    private var _nsfw: OptionalBoolean = OptionalBoolean.Missing
    var nsfw: Boolean? by ::_nsfw.delegate()
    
    private var _parentId: OptionalSnowflake = OptionalSnowflake.Missing
    var parentId: Snowflake? by ::_parentId.delegate()

    private var _position: OptionalInt = OptionalInt.Missing
    var position: Int? by ::_position.delegate()

    val permissionOverwrites: MutableList<Overwrite> = mutableListOf()

    override fun toRequest(): GuildChannelCreateRequest = GuildChannelCreateRequest(
            name = name,
            topic = _topic,
            nsfw = _nsfw,
            parentId = _parentId,
            position = _position,
            permissionOverwrite = Optional.missingOnEmpty(permissionOverwrites),
            type = ChannelType.GuildNews
    )
}