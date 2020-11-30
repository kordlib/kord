package dev.kord.rest.builder.template

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map
import dev.kord.rest.Image
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.GuildTemplateCreateRequest

class GuildFromTemplateCreateBuilder(var name: String) : RequestBuilder<GuildTemplateCreateRequest> {

    private var _image: Optional<Image> = Optional.Missing()
    var image: Image? by ::_image.delegate()


    override fun toRequest(): GuildTemplateCreateRequest = GuildTemplateCreateRequest(
            name, _image.map { it.dataUri }
    )
}