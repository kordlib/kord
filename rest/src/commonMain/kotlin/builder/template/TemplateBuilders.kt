package dev.kord.rest.builder.template

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map
import dev.kord.rest.Image
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.GuildFromTemplateCreateRequest
import dev.kord.rest.json.request.GuildTemplateCreateRequest
import dev.kord.rest.json.request.GuildTemplateModifyRequest

@KordDsl
public class GuildFromTemplateCreateBuilder(public var name: String) : RequestBuilder<GuildFromTemplateCreateRequest> {

    private var _image: Optional<Image> = Optional.Missing()
    public var image: Image? by ::_image.delegate()


    override fun toRequest(): GuildFromTemplateCreateRequest = GuildFromTemplateCreateRequest(
        name, _image.map { it.dataUri }
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as GuildFromTemplateCreateBuilder

        if (name != other.name) return false
        if (image != other.image) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (image?.hashCode() ?: 0)
        return result
    }

}

@KordDsl
public class GuildTemplateCreateBuilder(public var name: String) : RequestBuilder<GuildTemplateCreateRequest> {
    private var _description: Optional<String> = Optional.Missing()
    public var description: String? by ::_description.delegate()

    override fun toRequest(): GuildTemplateCreateRequest = GuildTemplateCreateRequest(name, _description)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as GuildTemplateCreateBuilder

        if (name != other.name) return false
        if (description != other.description) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        return result
    }

}

@KordDsl
public class GuildTemplateModifyBuilder : RequestBuilder<GuildTemplateModifyRequest> {

    private var _name: Optional<String> = Optional.Missing()
    public var name: String? by ::_name.delegate()

    private var _description: Optional<String> = Optional.Missing()
    public var description: String? by ::_description.delegate()

    override fun toRequest(): GuildTemplateModifyRequest = GuildTemplateModifyRequest(_name, _description)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as GuildTemplateModifyBuilder

        if (name != other.name) return false
        if (description != other.description) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + (description?.hashCode() ?: 0)
        return result
    }

}
