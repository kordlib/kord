package dev.kord.rest.builder.message

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.AttachmentRequest

@KordDsl
public class AttachmentBuilder(private val id: Snowflake) : RequestBuilder<AttachmentRequest> {

    private var _filename: Optional<String> = Optional.Missing()

    /** The name of the attached file. */
    public var filename: String? by ::_filename.delegate()

    private var _description: Optional<String> = Optional.Missing()

    /** The description for the file (max 1024 characters). */
    public var description: String? by ::_description.delegate()

    override fun toRequest(): AttachmentRequest = AttachmentRequest(
        id = id,
        filename = _filename,
        description = _description,
    )
}
