package dev.kord.rest.builder.message.modify

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.DiscordAttachment
import dev.kord.common.entity.MessageFlags
import dev.kord.common.entity.optional.Optional
import dev.kord.rest.NamedFile
import dev.kord.rest.builder.component.MessageComponentBuilder
import dev.kord.rest.builder.message.AllowedMentionsBuilder
import dev.kord.rest.builder.message.EmbedBuilder
import java.io.InputStream

/**
 * Utility container for message modify builder. This class contains
 * all possible fields as optionals.
 */
internal class MessageModifyStateHolder {

    var files: Optional<MutableList<NamedFile>> = Optional.Missing()

    var content: Optional<String?> = Optional.Missing()

    var embeds: Optional<MutableList<EmbedBuilder>> = Optional.Missing()

    var flags: Optional<MessageFlags?> = Optional.Missing()

    var allowedMentions: Optional<AllowedMentionsBuilder> = Optional.Missing()

    var attachments: Optional<MutableList<DiscordAttachment>> = Optional.Missing()

    var components: Optional<MutableList<MessageComponentBuilder>> = Optional.Missing()

}
