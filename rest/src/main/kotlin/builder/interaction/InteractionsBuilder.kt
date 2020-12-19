package dev.kord.rest.builder.interaction

import dev.kord.common.entity.ApplicationCommandOption
import dev.kord.common.entity.DiscordApplicationCommandOptionChoice
import dev.kord.common.entity.ApplicationCommandOptionType
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.mapList
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.GlobalApplicationCommandCreateRequest
import dev.kord.rest.json.request.GlobalApplicationCommandModifyRequest
import dev.kord.rest.json.request.GuildApplicationCommandCreateRequest
import dev.kord.rest.json.request.GuildApplicationCommandModifyRequest

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
    fun  choice(name: String, value: () -> String) {
        if (choices == null) choices = mutableListOf()
        choices!!.add(DiscordApplicationCommandOptionChoice(name, value()))
    }

    fun toRequest() = ApplicationCommandOption(type, name, description, _default, _required, _choices)
}