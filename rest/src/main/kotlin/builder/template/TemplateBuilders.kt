package dev.kord.rest.builder.template

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map
import dev.kord.rest.Image
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.GuildFromTemplateCreateRequest
import dev.kord.rest.json.request.GuildTemplateCreateRequest
import dev.kord.rest.json.request.GuildTemplateModifyRequest

class GuildFromTemplateCreateBuilder(var name: String) : RequestBuilder<GuildFromTemplateCreateRequest> {

    private var _image: Optional<Image> = Optional.Missing()
    var image: Image? by ::_image.delegate()


    override fun toRequest(): GuildFromTemplateCreateRequest = GuildFromTemplateCreateRequest(
        name, _image.map { it.dataUri }
    )
}

class GuildTemplateCreateBuilder(var name: String) : RequestBuilder<GuildTemplateCreateRequest> {
    private var _description: Optional<String> = Optional.Missing()
    var description: String? by ::_description.delegate()

    override fun toRequest(): GuildTemplateCreateRequest = GuildTemplateCreateRequest(name, _description)
}


class GuildTemplateModifyBuilder : RequestBuilder<GuildTemplateModifyRequest> {

    private var _name: Optional<String> = Optional.Missing()
    var name: String? by ::_name.delegate()

    private var _description: Optional<String> = Optional.Missing()
    var description: String? by ::_description.delegate()

    override fun toRequest(): GuildTemplateModifyRequest = GuildTemplateModifyRequest(_name, _description)
}
