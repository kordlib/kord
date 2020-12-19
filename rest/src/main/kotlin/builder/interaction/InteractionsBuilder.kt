package dev.kord.rest.builder.interaction

import dev.kord.common.entity.ApplicationCommandOption
import dev.kord.common.entity.ApplicationCommandOptionType
import dev.kord.common.entity.DiscordApplicationCommandOptionChoice
import dev.kord.common.entity.InteractionResponseType
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map
import dev.kord.common.entity.optional.mapList
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.builder.message.AllowedMentionsBuilder
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.json.request.*

class GlobalApplicationCommandCreateBuilder(
    val name: String,
    val description: String
) : RequestBuilder<GlobalApplicationCommandCreateRequest> {

    private var _options: Optional<MutableList<ApplicationCommandOptionBuilder>> = Optional.Missing()
    private var options: MutableList<ApplicationCommandOptionBuilder>? by ::_options.delegate()

    //TODO("check if desc can be empty")
    fun option(
        type: ApplicationCommandOptionType,
        name: String,
        description: String,
        builder: ApplicationCommandOptionBuilder.() -> Unit
    ) {
        if (options == null) options = mutableListOf()
        val option = ApplicationCommandOptionBuilder(type, name, description).apply(builder)
        options!!.add(option)
    }

    override fun toRequest(): GlobalApplicationCommandCreateRequest {
        return GlobalApplicationCommandCreateRequest(name, description, _options.mapList { it.toRequest() })

    }

}


class GlobalApplicationCommandModifyBuilder(
    val name: String,
    val description: String
) : RequestBuilder<GlobalApplicationCommandModifyRequest> {

    private var _options: Optional<MutableList<ApplicationCommandOptionBuilder>> = Optional.Missing()
    private var options: MutableList<ApplicationCommandOptionBuilder>? by ::_options.delegate()

    //TODO("check if desc can be empty")
    fun option(
        type: ApplicationCommandOptionType,
        name: String,
        description: String,
        builder: ApplicationCommandOptionBuilder.() -> Unit
    ) {
        if (options == null) options = mutableListOf()
        val option = ApplicationCommandOptionBuilder(type, name, description).apply(builder)
        options!!.add(option)
    }

    override fun toRequest(): GlobalApplicationCommandModifyRequest {
        return GlobalApplicationCommandModifyRequest(name, description, _options.mapList { it.toRequest() })

    }

}

class GuildApplicationCommandCreateBuilder(
    val name: String,
    val description: String
) : RequestBuilder<GuildApplicationCommandCreateRequest> {

    private var _options: Optional<MutableList<ApplicationCommandOptionBuilder>> = Optional.Missing()
    private var options: MutableList<ApplicationCommandOptionBuilder>? by ::_options.delegate()

    //TODO("check if desc can be empty")
    fun option(
        type: ApplicationCommandOptionType,
        name: String,
        description: String,
        builder: ApplicationCommandOptionBuilder.() -> Unit
    ) {
        if (options == null) options = mutableListOf()
        val option = ApplicationCommandOptionBuilder(type, name, description).apply(builder)
        options!!.add(option)
    }

    override fun toRequest(): GuildApplicationCommandCreateRequest {
        return GuildApplicationCommandCreateRequest(name, description, _options.mapList { it.toRequest() })

    }

}


class GuildApplicationCommandModifyBuilder(
    val name: String,
    val description: String
) : RequestBuilder<GuildApplicationCommandModifyRequest> {

    private var _options: Optional<MutableList<ApplicationCommandOptionBuilder>> = Optional.Missing()
    private var options: MutableList<ApplicationCommandOptionBuilder>? by ::_options.delegate()

    //TODO("check if desc can be empty")
    fun option(
        type: ApplicationCommandOptionType,
        name: String,
        description: String,
        builder: ApplicationCommandOptionBuilder.() -> Unit
    ) {
        if (options == null) options = mutableListOf()
        val option = ApplicationCommandOptionBuilder(type, name, description).apply(builder)
        options!!.add(option)
    }

    override fun toRequest(): GuildApplicationCommandModifyRequest {
        return GuildApplicationCommandModifyRequest(name, description, _options.mapList { it.toRequest() })

    }

}

class ApplicationCommandOptionBuilder(
    val type: ApplicationCommandOptionType,
    val name: String,
    val description: String
) {
    private var _required: OptionalBoolean = OptionalBoolean.Missing
    var required: Boolean? by ::_required.delegate()

    private var _default: OptionalBoolean = OptionalBoolean.Missing
    var default: Boolean? by ::_default.delegate()

    private var _choices: Optional<MutableList<DiscordApplicationCommandOptionChoice>> = Optional.Missing()
    private var choices: MutableList<DiscordApplicationCommandOptionChoice>? by ::_choices.delegate()

    //TODO("Express types in a convenient way.")
    fun choice(name: String, value: () -> String) {
        if (choices == null) choices = mutableListOf()
        choices!!.add(DiscordApplicationCommandOptionChoice(name, value()))
    }

    fun toRequest() = ApplicationCommandOption(type, name, description, _default, _required, _choices)
}

class OriginalInteractionResponseModifyBuilder() :
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

class FollowupMessageModifyBuilder() :
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

class InteractionApplicationCommandCallbackDataBuilder(val content: String) {

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

    fun build(): DiscordInteractionApplicationCommandCallbackData {

        return DiscordInteractionApplicationCommandCallbackData(
            _tts,
            content,
            _embeds.mapList { it.toRequest() },
            _allowedMentions.map { it.build() })

    }
}

class InteractionResponseCreateBuilder(var type: InteractionResponseType) : RequestBuilder<DiscordInteractionResponse> {
    private var _data: Optional<InteractionApplicationCommandCallbackDataBuilder> = Optional.Missing()
    private var data: InteractionApplicationCommandCallbackDataBuilder? by ::_data.delegate()

    fun data(content: String, builder: InteractionApplicationCommandCallbackDataBuilder.() -> Unit) {
        val data = InteractionApplicationCommandCallbackDataBuilder(content).apply(builder)
    }

    override fun toRequest(): DiscordInteractionResponse {
        return DiscordInteractionResponse(type, _data.map { it.build() })
    }

}

