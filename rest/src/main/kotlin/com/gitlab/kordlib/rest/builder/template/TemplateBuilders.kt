package com.gitlab.kordlib.rest.builder.template

import com.gitlab.kordlib.common.entity.optional.Optional
import com.gitlab.kordlib.common.entity.optional.delegate.delegate
import com.gitlab.kordlib.common.entity.optional.map
import com.gitlab.kordlib.rest.Image
import com.gitlab.kordlib.rest.builder.RequestBuilder
import com.gitlab.kordlib.rest.json.request.GuildTemplateCreateRequest

class GuildFromTemplateCreateBuilder(var name: String) : RequestBuilder<GuildTemplateCreateRequest> {

    private var _image: Optional<Image> = Optional.Missing()
    var image: Image? by ::_image.delegate()


    override fun toRequest(): GuildTemplateCreateRequest = GuildTemplateCreateRequest(
            name, _image.map { it.dataUri }
    )
}