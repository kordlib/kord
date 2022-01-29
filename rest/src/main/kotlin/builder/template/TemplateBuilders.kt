package dev.kord.rest.builder.template

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map
import dev.kord.rest.Image
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.GuildFromTemplateCreateRequest
import dev.kord.rest.json.request.GuildTemplateCreateRequest
import dev.kord.rest.json.request.GuildTemplateModifyRequest

public class GuildFromTemplateCreateBuilder(public var name: String) : RequestBuilder<GuildFromTemplateCreateRequest> {

    private var _image: Optional<Image> = Optional.Missing()
    public var image: Image? by ::_image.delegate()


    override fun toRequest(): GuildFromTemplateCreateRequest = GuildFromTemplateCreateRequest(
        name, _image.map { it.dataUri }
    )
}

public class GuildTemplateCreateBuilder(public var name: String) : RequestBuilder<GuildTemplateCreateRequest> {
    private var _description: Optional<String> = Optional.Missing()
    public var description: String? by ::_description.delegate()

    override fun toRequest(): GuildTemplateCreateRequest = GuildTemplateCreateRequest(name, _description)
}


public class GuildTemplateModifyBuilder : RequestBuilder<GuildTemplateModifyRequest> {

    private var _name: Optional<String> = Optional.Missing()
    public var name: String? by ::_name.delegate()

    private var _description: Optional<String> = Optional.Missing()
    public var description: String? by ::_description.delegate()

    override fun toRequest(): GuildTemplateModifyRequest = GuildTemplateModifyRequest(_name, _description)
}
