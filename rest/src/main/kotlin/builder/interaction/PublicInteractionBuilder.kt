package dev.kord.rest.builder.interaction

import dev.kord.common.annotation.KordDsl
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.InteractionResponseType
import dev.kord.common.entity.optional.*
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.component.MessageComponentBuilder
import dev.kord.rest.builder.message.AllowedMentionsBuilder
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.json.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

@KordPreview
@KordDsl
class PublicInteractionResponseCreateBuilder :
    BaseInteractionResponseCreateBuilder {
    private var _content: Optional<String> = Optional.Missing()
    override var content: String? by ::_content.delegate()

    override var embeds: MutableList<EmbedBuilder> = mutableListOf()

    private var _allowedMentions: Optional<AllowedMentionsBuilder> = Optional.Missing()
    override var allowedMentions: AllowedMentionsBuilder? by ::_allowedMentions.delegate()


    private var _tts: OptionalBoolean = OptionalBoolean.Missing
    var tts: Boolean? by ::_tts.delegate()

    override val components: MutableList<MessageComponentBuilder> = mutableListOf()

    val files: MutableList<Pair<String, InputStream>> = mutableListOf()


    fun addFile(name: String, content: InputStream) {
        files += name to content
    }

    suspend fun addFile(path: Path) = withContext(Dispatchers.IO) {
        addFile(path.fileName.toString(), Files.newInputStream(path))
    }

    override fun toRequest(): MultipartInteractionResponseCreateRequest {
        val type =
            if (files.isEmpty() && content == null && embeds.isEmpty()) InteractionResponseType.DeferredChannelMessageWithSource
            else InteractionResponseType.ChannelMessageWithSource

        return MultipartInteractionResponseCreateRequest(
            InteractionResponseCreateRequest(
                    type,
                InteractionApplicationCommandCallbackData(
                    content = _content,
                    embeds = Optional.missingOnEmpty(embeds.map { it.toRequest() }),
                    allowedMentions = _allowedMentions.map { it.build() },
                    tts = _tts,
                    components = Optional.missingOnEmpty(components.map { it.build() })
                ).optional()
            ),
            files
        )

    }
}

@KordPreview
@KordDsl
class PublicInteractionResponseModifyBuilder :
    BaseInteractionResponseModifyBuilder {
    private var _content: Optional<String?> = Optional.Missing()
    override var content: String? by ::_content.delegate()

    private var _embeds: Optional<MutableList<EmbedBuilder>> = Optional.Missing()
    override var embeds: MutableList<EmbedBuilder>? by ::_embeds.delegate()

    private var _allowedMentions: Optional<AllowedMentionsBuilder?> = Optional.Missing()
    override var allowedMentions: AllowedMentionsBuilder? by ::_allowedMentions.delegate()

    val files: MutableList<Pair<String, InputStream>> = mutableListOf()

    private var _components: Optional<MutableList<MessageComponentBuilder>> = Optional.Missing()
    override var components: MutableList<MessageComponentBuilder>? by ::_components.delegate()

    fun addFile(name: String, content: InputStream) {
        files += name to content
    }

    suspend fun addFile(path: Path) = withContext(Dispatchers.IO) {
        addFile(path.fileName.toString(), Files.newInputStream(path))
    }

    override fun toRequest(): MultipartInteractionResponseModifyRequest {
        return MultipartInteractionResponseModifyRequest(
            InteractionResponseModifyRequest(
                content = _content,
                embeds = Optional(embeds).coerceToMissing().mapList { it.toRequest() },
                allowedMentions = _allowedMentions.map { it.build() },
                components = Optional(components).coerceToMissing().mapList { it.build() },
            ),
            files
        )

    }
}
