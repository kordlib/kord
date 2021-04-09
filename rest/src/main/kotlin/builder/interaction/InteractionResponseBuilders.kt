package dev.kord.rest.builder.interaction

import dev.kord.common.annotation.KordDsl
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.AllowedMentions
import dev.kord.common.entity.InteractionResponseType
import dev.kord.common.entity.MessageFlag
import dev.kord.common.entity.MessageFlags
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.mapList
import dev.kord.common.entity.optional.optional
import dev.kord.rest.builder.RequestBuilder
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
class AcknowledgementResponseBuilder(val ephemeral: Boolean = false) :
    RequestBuilder<InteractionResponseCreateRequest> {
    override fun toRequest(): InteractionResponseCreateRequest {
        val data = if (!ephemeral) Optional.Missing()
        else Optional.Value(
            InteractionApplicationCommandCallbackData(flags = Optional.Value(MessageFlags(MessageFlag.Ephemeral)))
        )
        return InteractionResponseCreateRequest(
            InteractionResponseType.DeferredChannelMessageWithSource,
            data
        )
    }

}

@KordPreview
@KordDsl
class PublicInteractionResponseCreateBuilder :
    RequestBuilder<MultipartInteractionResponseCreateRequest> {
    private var _content: Optional<String> = Optional.Missing()
    var content: String? by ::_content.delegate()

    private var _embeds: Optional<MutableList<EmbedBuilder>> = Optional.Missing()
    var embeds: MutableList<EmbedBuilder>? by ::_embeds.delegate()

    private var _allowedMentions: Optional<AllowedMentions> = Optional.Missing()
    var allowedMentions: AllowedMentions? by ::_allowedMentions.delegate()


    private var _tts: OptionalBoolean = OptionalBoolean.Missing
    var tts: Boolean? by ::_tts.delegate()

    val files: MutableList<Pair<String, InputStream>> = mutableListOf()


    @OptIn(ExperimentalContracts::class)
    inline fun allowedMentions(builder: AllowedMentionsBuilder.() -> Unit) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        allowedMentions = AllowedMentionsBuilder().apply(builder).build()
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

    suspend fun addFile(path: Path) = withContext(Dispatchers.IO) {
        addFile(path.fileName.toString(), Files.newInputStream(path))
    }

    override fun toRequest(): MultipartInteractionResponseCreateRequest {
        return MultipartInteractionResponseCreateRequest(
            InteractionResponseCreateRequest(
                InteractionResponseType.ChannelMessageWithSource,
                InteractionApplicationCommandCallbackData(
                    content = _content,
                    embeds = _embeds.mapList { it.toRequest() },
                    allowedMentions = _allowedMentions,
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

    private var _allowedMentions: Optional<AllowedMentions> = Optional.Missing()
    var allowedMentions: AllowedMentions? by ::_allowedMentions.delegate()


    val files: MutableList<Pair<String, InputStream>> = mutableListOf()


    @OptIn(ExperimentalContracts::class)
    inline fun allowedMentions(builder: AllowedMentionsBuilder.() -> Unit) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        allowedMentions = AllowedMentionsBuilder().apply(builder).build()
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

    suspend fun addFile(path: Path) = withContext(Dispatchers.IO) {
        addFile(path.fileName.toString(), Files.newInputStream(path))
    }

    override fun toRequest(): MultipartInteractionResponseModifyRequest {
        return MultipartInteractionResponseModifyRequest(
            InteractionResponseModifyRequest(
                content = _content,
                _embeds.mapList { it.toRequest() },
                _allowedMentions
            ),
            files
        )
    }
}


@KordPreview
@KordDsl
class EphemeralInteractionResponseModifyBuilder() :
    RequestBuilder<MultipartInteractionResponseModifyRequest> {
    private var _content: Optional<String> = Optional.Missing()
    var content: String? by ::_content.delegate()
    private var _allowedMentions: Optional<AllowedMentions> = Optional.Missing()
    var allowedMentions: AllowedMentions? by ::_allowedMentions.delegate()

    @OptIn(ExperimentalContracts::class)
    inline fun allowedMentions(builder: AllowedMentionsBuilder.() -> Unit) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        allowedMentions = AllowedMentionsBuilder().apply(builder).build()
    }


    override fun toRequest(): MultipartInteractionResponseModifyRequest {
        return MultipartInteractionResponseModifyRequest(
            InteractionResponseModifyRequest(
                content = _content,
                allowedMentions = _allowedMentions,
            ),
        )
    }
}


@KordPreview
@KordDsl
class EphemeralInteractionResponseCreateBuilder(val content: String) :
    RequestBuilder<InteractionResponseCreateRequest> {
    private var _allowedMentions: Optional<AllowedMentions> = Optional.Missing()
    var allowedMentions: AllowedMentions? by ::_allowedMentions.delegate()

    private var _tts: OptionalBoolean = OptionalBoolean.Missing
    var tts: Boolean? by ::_tts.delegate()

    @OptIn(ExperimentalContracts::class)
    fun allowedMentions(builder: AllowedMentionsBuilder.() -> Unit) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        allowedMentions = AllowedMentionsBuilder().apply(builder).build()
    }


    override fun toRequest(): InteractionResponseCreateRequest {
        return InteractionResponseCreateRequest(
            type = InteractionResponseType.ChannelMessageWithSource,
            InteractionApplicationCommandCallbackData(
                content = Optional.Value(content),
                allowedMentions = _allowedMentions,
                tts = _tts,
                flags = Optional.Value(MessageFlags(MessageFlag.Ephemeral))

            ).optional()
        )
    }
}

