package dev.kord.rest.builder.message.create

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.DiscordMessageReference
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.*
import dev.kord.rest.NamedFile
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.builder.component.MessageComponentBuilder
import dev.kord.rest.builder.message.AllowedMentionsBuilder
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.json.request.MessageCreateRequest
import dev.kord.rest.json.request.MultipartMessageCreateRequest

/**
 * Message builder for creating messages as a bot user.
 */
public class UserMessageCreateBuilder
    : MessageCreateBuilder,
    RequestBuilder<MultipartMessageCreateRequest> {

    override var content: String? = null

    /**
     * An identifier that can be used to validate the message was sent.
     */
    public var nonce: String? = null

    override var tts: Boolean? = null

    override val embeds: MutableList<EmbedBuilder> = mutableListOf()

    override var allowedMentions: AllowedMentionsBuilder? = null


    override val components: MutableList<MessageComponentBuilder> = mutableListOf()

    /**
     * The id of the message being replied to.
     * Requires the [ReadMessageHistory][dev.kord.common.entity.Permission.ReadMessageHistory] permission.
     *
     * Replying will not mention the author by default,
     * set [AllowedMentionsBuilder.repliedUser] to `true` via [allowedMentions]  to mention the author.
     */
    public var messageReference: Snowflake? = null

    /**
     * whether to error if the referenced message doesn't exist instead of sending as a normal (non-reply) message,
     * defaults to true.
     */
    public var failIfNotExists: Boolean? = null

    override val files: MutableList<NamedFile> = mutableListOf()

    @OptIn(KordPreview::class)
    override fun toRequest(): MultipartMessageCreateRequest {
        return MultipartMessageCreateRequest(
            MessageCreateRequest(
                content = Optional(content).coerceToMissing(),
                nonce = Optional(nonce).coerceToMissing(),
                tts = Optional(tts).coerceToMissing().toPrimitive(),
                embeds = Optional(embeds).mapList { it.toRequest() },
                allowedMentions = Optional(allowedMentions).coerceToMissing().map { it.build() },
                messageReference = messageReference?.let {
                    Optional(
                        DiscordMessageReference(
                            OptionalSnowflake.Value(it),
                            failIfNotExists = Optional(failIfNotExists).coerceToMissing().toPrimitive()
                        )
                    )
                } ?: Optional.Missing(),
                components = Optional(components).coerceToMissing().mapList { it.build() }
            ),
            files
        )
    }

}
