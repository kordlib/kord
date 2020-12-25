package dev.kord.rest.builder.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map
import dev.kord.common.entity.optional.mapList
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.builder.message.AllowedMentionsBuilder
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.json.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Files
import java.nio.file.Path
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
@KordPreview
class GlobalApplicationCommandCreateBuilder(
    val name: String,
    val description: String
) : RequestBuilder<GlobalApplicationCommandCreateRequest>, BaseApplicationBuilder() {

    private var _options: Optional<MutableList<OptionsBuilder>> = Optional.Missing()
    override var options: MutableList<OptionsBuilder>? by ::_options.delegate()

    override fun toRequest(): GlobalApplicationCommandCreateRequest {
        return GlobalApplicationCommandCreateRequest(name, description, _options.mapList { it.toRequest() })

    }

}

@KordPreview
class GlobalApplicationCommandModifyBuilder : RequestBuilder<GlobalApplicationCommandModifyRequest>,
    BaseApplicationBuilder() {
    private var _name: Optional<String> = Optional.Missing()
    var name: String? by ::_name.delegate()

    private var _description: Optional<String> = Optional.Missing()
    var description: String? by ::_name.delegate()

    private var _options: Optional<MutableList<OptionsBuilder>> = Optional.Missing()
    override var options: MutableList<OptionsBuilder>? by ::_options.delegate()
    override fun toRequest(): GlobalApplicationCommandModifyRequest {
        return GlobalApplicationCommandModifyRequest(_name, _description, _options.mapList { it.toRequest() })
    }


}
@KordPreview
sealed class BaseApplicationBuilder {
    protected abstract var options: MutableList<OptionsBuilder>?

    fun boolean(name: String, description: String) {
        if (options == null) options = mutableListOf()
        options!!.add(BooleanBuilder(name, description))
    }

    fun int(name: String, description: String, builder: IntChoiceBuilder.() -> Unit = {}) {
        if (options == null) options = mutableListOf()
        options!!.add(IntChoiceBuilder(name, description).apply(builder))
    }

    fun string(name: String, description: String, builder: StringChoiceBuilder.() -> Unit = {}) {
        if (options == null) options = mutableListOf()
        options!!.add(StringChoiceBuilder(name, description).apply(builder))
    }

    fun group(name: String, description: String, builder: GroupCommandBuilder.() -> Unit) {
        if (options == null) options = mutableListOf()
        options!!.add(GroupCommandBuilder(name, description).apply(builder))
    }

    fun subCommand(name: String, description: String, builder: SubCommandBuilder.() -> Unit = {}) {
        if (options == null) options = mutableListOf()
        options!!.add(SubCommandBuilder(name, description).apply(builder))
    }

    fun role(name: String, description: String) {
        if (options == null) options = mutableListOf()
        options!!.add(RoleBuilder(name, description))
    }

    fun user(name: String, description: String) {
        if (options == null) options = mutableListOf()
        options!!.add(UserBuilder(name, description))
    }

    fun channel(name: String, description: String) {
        if (options == null) options = mutableListOf()
        options!!.add(ChannelBuilder(name, description))
    }
}
@KordPreview
class GuildApplicationCommandCreateBuilder(
    val name: String,
    val description: String
) : RequestBuilder<GuildApplicationCommandCreateRequest>, BaseApplicationBuilder() {
    private var _options: Optional<MutableList<OptionsBuilder>> = Optional.Missing()
    override var options: MutableList<OptionsBuilder>? by ::_options.delegate()

    override fun toRequest(): GuildApplicationCommandCreateRequest {
        return GuildApplicationCommandCreateRequest(name, description, _options.mapList { it.toRequest() })

    }

}

@KordPreview
class GuildApplicationCommandModifyBuilder : BaseApplicationBuilder(),
    RequestBuilder<GuildApplicationCommandModifyRequest> {

    private var _name: Optional<String> = Optional.Missing()
    var name: String? by ::_name.delegate()


    private var _description: Optional<String> = Optional.Missing()
    var description: String? by ::_name.delegate()


    private var _options: Optional<MutableList<OptionsBuilder>> = Optional.Missing()
    override var options: MutableList<OptionsBuilder>? by ::_options.delegate()


    override fun toRequest(): GuildApplicationCommandModifyRequest {
        return GuildApplicationCommandModifyRequest(_name, _description, _options.mapList { it.toRequest() })

    }

}

@KordPreview
class OriginalInteractionResponseModifyBuilder :
    RequestBuilder<OriginalInteractionResponseModifyRequest> {
    private var _content: Optional<String> = Optional.Missing()
    var content: String? by ::_content.delegate()

    private var _embeds: Optional<MutableList<EmbedBuilder>> = Optional.Missing()
    var embeds: MutableList<EmbedBuilder>? by ::_embeds.delegate()

    private var _allowedMentions: Optional<AllowedMentionsBuilder> = Optional.Missing()
    var allowedMentions: AllowedMentionsBuilder? by ::_allowedMentions.delegate()

    fun embed(builder: EmbedBuilder.() -> Unit) {
        if (embeds == null) embeds = mutableListOf()
        embeds!! += EmbedBuilder().apply(builder)
    }


    override fun toRequest(): OriginalInteractionResponseModifyRequest {
        return OriginalInteractionResponseModifyRequest(
            _content,
            _embeds.mapList { it.toRequest() },
            _allowedMentions.map { it.build() })
    }
}
@KordPreview
class FollowupMessageModifyBuilder :
    RequestBuilder<FollowupMessageModifyRequest> {
    private var _content: Optional<String> = Optional.Missing()
    var content: String? by ::_content.delegate()

    private var _embeds: Optional<MutableList<EmbedBuilder>> = Optional.Missing()
    var embeds: MutableList<EmbedBuilder>? by ::_embeds.delegate()

    private var _allowedMentions: Optional<AllowedMentionsBuilder> = Optional.Missing()
    var allowedMentions: AllowedMentionsBuilder? by ::_allowedMentions.delegate()

    fun embed(builder: EmbedBuilder.() -> Unit) {
        if (embeds == null) embeds = mutableListOf()
        embeds!! += EmbedBuilder().apply(builder)
    }


    override fun toRequest(): FollowupMessageModifyRequest {
        return FollowupMessageModifyRequest(
            _content,
            _embeds.mapList { it.toRequest() },
            _allowedMentions.map { it.build() })
    }
}
@KordPreview
class InteractionApplicationCommandCallbackDataBuilder(var content: String) {

    private var _tts: OptionalBoolean = OptionalBoolean.Missing
    var tts: Boolean? by ::_tts.delegate()

    private var _embeds: Optional<MutableList<EmbedBuilder>> = Optional.Missing()
    var embeds: MutableList<EmbedBuilder>? by ::_embeds.delegate()

    private var _allowedMentions: Optional<AllowedMentionsBuilder> = Optional.Missing()
    var allowedMentions: AllowedMentionsBuilder? by ::_allowedMentions.delegate()

    fun embed(builder: EmbedBuilder.() -> Unit) {
        if (embeds == null) embeds = mutableListOf()
        embeds!! += EmbedBuilder().apply(builder)
    }

    fun build(): InteractionApplicationCommandCallbackData {

        return InteractionApplicationCommandCallbackData(
            _tts,
            content,
            _embeds.mapList { it.toRequest() },
            _allowedMentions.map { it.build() })

    }
}

@KordPreview
class FollowupMessageCreateBuilder : RequestBuilder<MultipartFollowupMessageCreateRequest> {

    private var _content: Optional<String> = Optional.Missing()
    var content: String? by ::_content.delegate()

    private var _username: Optional<String> = Optional.Missing()
    var username: String? by ::_username.delegate()

    private var _avatarUrl: Optional<String> = Optional.Missing()
    var avatarUrl: String? by ::_avatarUrl.delegate()

    private var _tts: OptionalBoolean = OptionalBoolean.Missing
    var tts: Boolean? by ::_tts.delegate()

    private var file: Pair<String, java.io.InputStream>? = null
    var embeds: MutableList<EmbedRequest> = mutableListOf()

    fun setFile(name: String, content: java.io.InputStream) {
        file = name to content
    }

    suspend fun setFile(path: Path) = withContext(Dispatchers.IO) {
        setFile(path.fileName.toString(), Files.newInputStream(path))
    }

    @OptIn(ExperimentalContracts::class)
    inline fun embed(builder: EmbedBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        embeds.add(EmbedBuilder().apply(builder).toRequest())
    }

    override fun toRequest(): MultipartFollowupMessageCreateRequest = MultipartFollowupMessageCreateRequest(
        FollowupMessageCreateRequest(_content, _username, _avatarUrl, _tts, Optional.missingOnEmpty(embeds)), file
    )


}

