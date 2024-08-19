package dev.kord.rest.builder.guild

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.optional.Optional
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.GuildStickerModifyRequest
import dev.kord.common.entity.optional.delegate.delegate

@KordDsl
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as StickerModifyBuilder

        if (name != other.name) return false
        if (description != other.description) return false
        if (tags != other.tags) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (tags?.hashCode() ?: 0)
        return result
    }

}
