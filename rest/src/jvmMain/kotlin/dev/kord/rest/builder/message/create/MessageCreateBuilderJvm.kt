package dev.kord.rest.builder.message.create

import dev.kord.rest.NamedFile
import java.nio.file.Path
import dev.kord.rest.builder.message.addFile as addFileExtensionOnNewSupertype

/**
 * Adds a file with the given [path] to the attachments.
 */
@Deprecated(
    "Replaced by extension on 'MessageBuilder'. Change import to 'dev.kord.rest.builder.message.addFile'.",
    ReplaceWith("this.addFile(path)", imports = ["dev.kord.rest.builder.message.addFile"]),
    DeprecationLevel.HIDDEN,
)
public fun MessageCreateBuilder.addFile(path: Path): NamedFile =
    addFileExtensionOnNewSupertype(path)
