package dev.kord.rest.builder.guild

import dev.kord.common.entity.optional.Optional
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.GuildStickerModifyRequest
import dev.kord.common.entity.optional.delegate.delegate

public class StickerModifyBuilder : RequestBuilder<GuildStickerModifyRequest> {
    private var _name: Optional<String> = Optional.Missing()
    private var _description: Optional<String> = Optional.Missing()
    private var _tags: Optional<String> = Optional.Missing()
    public var name: String? by ::_name.delegate()
    public var description: String? by ::_description.delegate()
    public var tags: String? by ::_tags.delegate()


    override fun toRequest(): GuildStickerModifyRequest {
        return GuildStickerModifyRequest(_name, _description, _tags)
    }
}
