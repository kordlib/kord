package dev.kord.rest.builder.sticker

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.ModifyGuildStickerRequest

class ModifyStickerBuilder : RequestBuilder<ModifyGuildStickerRequest> {
    private var _name: Optional<String> = Optional.Missing()
    var name by ::_name.delegate()

    private var _description: Optional<String> = Optional.Missing()
    var description by ::_description.delegate()

    private var _tags: Optional<String> = Optional.Missing()
    var tags by ::_tags.delegate()

    fun tags(vararg tags: String) {
        this.tags = tags.joinToString(",")
    }

    override fun toRequest(): ModifyGuildStickerRequest = ModifyGuildStickerRequest(_name, _description, _tags)
}