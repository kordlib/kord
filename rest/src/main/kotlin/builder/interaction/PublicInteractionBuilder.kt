package dev.kord.rest.builder.interaction

import dev.kord.common.annotation.KordDsl
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.DiscordComponent
import dev.kord.common.entity.InteractionResponseType
import dev.kord.common.entity.optional.*
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.builder.components.ActionRowBuilder
import dev.kord.rest.builder.components.ActionRowContainerBuilder
import dev.kord.rest.builder.components.MessageComponentBuilder
import dev.kord.rest.builder.message.AllowedMentionsBuilder
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.json.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordPreview
@KordDsl
class PublicInteractionResponseCreateBuilder :
    RequestBuilder<MultipartInteractionResponseCreateRequest> {
    private var _content: Optional<String> = Optional.Missing()
    var content: String? by ::_content.delegate()

    private var _embeds: Optional<MutableList<EmbedBuilder>> = Optional.Missing()
    var embeds: MutableList<EmbedBuilder>? by ::_embeds.delegate()

    private var _allowedMentions: Optional<AllowedMentionsBuilder> = Optional.Missing()
    var allowedMentions: AllowedMentionsBuilder? by ::_allowedMentions.delegate()


    private var _tts: OptionalBoolean = OptionalBoolean.Missing
    var tts: Boolean? by ::_tts.delegate()

    @KordPreview
    val components: MutableList<MessageComponentBuilder> = mutableListOf()

    val files: MutableList<Pair<String, InputStream>> = mutableListOf()



    /**
     * Configures the mentions that should trigger a mention (aka ping). Not calling this function will result in the default behavior
     * (ping everything), calling this function but not configuring it before the request is build will result in all
     * pings being ignored.
     */
    @OptIn(ExperimentalContracts::class)
    inline fun allowedMentions(block: AllowedMentionsBuilder.() -> Unit = {}) {
        contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
        allowedMentions = (allowedMentions ?: AllowedMentionsBuilder()).apply(block)
    }

    @OptIn(ExperimentalContracts::class)
    inline fun embed(builder: EmbedBuilder.() -> Unit) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        if (embeds == null) embeds = mutableListOf()
        embeds!! += EmbedBuilder().apply(builder)
    }

    @OptIn(ExperimentalContracts::class)
    @KordPreview
    inline fun actionRow(builder: ActionRowBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        components.add(ActionRowBuilder().apply(builder))
    }

    fun addFile(name: String, content: InputStream) {
        files += name to content
    }

    suspend fun addFile(path: Path) = withContext(Dispatchers.IO) {
        addFile(path.fileName.toString(), Files.newInputStream(path))
    }

    override fun toRequest(): MultipartInteractionResponseCreateRequest {
        val type =
            if (files.isEmpty() && content == null && embeds == null) InteractionResponseType.DeferredChannelMessageWithSource
            else InteractionResponseType.ChannelMessageWithSource

        return MultipartInteractionResponseCreateRequest(
            InteractionResponseCreateRequest(
                type,
                InteractionApplicationCommandCallbackData(
                    content = _content,
                    embeds = _embeds.mapList { it.toRequest() },
                    allowedMentions = _allowedMentions.map { it.build() },
                    tts = _tts
                ).optional()
            ),
            files
        )

    }
}

@KordPreview
@KordDsl
class PublicInteractionResponseModifyBuilder :
    RequestBuilder<MultipartInteractionResponseModifyRequest> {
    private var _content: Optional<String> = Optional.Missing()
    var content: String? by ::_content.delegate()

    private var _embeds: Optional<MutableList<EmbedBuilder>> = Optional.Missing()
    var embeds: MutableList<EmbedBuilder>? by ::_embeds.delegate()

    private var _allowedMentions: Optional<AllowedMentionsBuilder> = Optional.Missing()
    var allowedMentions: AllowedMentionsBuilder? by ::_allowedMentions.delegate()

    val files: MutableList<Pair<String, InputStream>> = mutableListOf()

    val components: MutableList<MessageComponentBuilder> = mutableListOf()

    /**
     * Configures the mentions that should trigger a mention (aka ping). Not calling this function will result in the default behavior
     * (ping everything), calling this function but not configuring it before the request is build will result in all
     * pings being ignored.
     */
    @OptIn(ExperimentalContracts::class)
    inline fun allowedMentions(block: AllowedMentionsBuilder.() -> Unit = {}) {
        contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
        allowedMentions = (allowedMentions ?: AllowedMentionsBuilder()).apply(block)
    }



    @OptIn(ExperimentalContracts::class)
    inline fun embed(builder: EmbedBuilder.() -> Unit) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        if (embeds == null) embeds = mutableListOf()
        embeds!! += EmbedBuilder().apply(builder)
    }

    fun addFile(name: String, content: InputStream) {
        files += name to content
    }

    @OptIn(ExperimentalContracts::class)
    @KordPreview
    inline fun actionRow(builder: ActionRowBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        components.add(ActionRowBuilder().apply(builder))
    }

    suspend fun addFile(path: Path) = withContext(Dispatchers.IO) {
        addFile(path.fileName.toString(), Files.newInputStream(path))
    }

    override fun toRequest(): MultipartInteractionResponseModifyRequest {
        return MultipartInteractionResponseModifyRequest(
            InteractionResponseModifyRequest(
                content = _content,
                embeds = _embeds.mapList { it.toRequest() },
                allowedMentions = _allowedMentions.map { it.build() },
                components = Optional.missingOnEmpty(components.map(MessageComponentBuilder::build))
            ),
            files
        )

    }
}
