package dev.kord.rest.builder.sticker

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.CreateStickerRequest
import io.ktor.client.request.forms.InputProvider

class CreateStickerBuilder(val name: String, val tags: String, val file: InputProvider) : RequestBuilder<CreateStickerRequest> {

    private var _description: Optional<String> = Optional.Missing()
    var description by ::_description.delegate()

    override fun toRequest(): CreateStickerRequest =
        CreateStickerRequest(name, _description, tags, file)
}
