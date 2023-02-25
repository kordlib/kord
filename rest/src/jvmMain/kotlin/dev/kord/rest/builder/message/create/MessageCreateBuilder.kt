@file:JvmName("MessageCreateBuilderJvm")
package dev.kord.rest.builder.message.create

import dev.kord.rest.NamedFile
import io.ktor.client.request.forms.*
import io.ktor.util.cio.*
import java.nio.file.Path


/**
 * Adds a file with the given [path] to the attachments.
 */
public fun MessageCreateBuilder.addFile(path: Path): NamedFile =
    addFile(path.fileName.toString(), ChannelProvider { path.readChannel() })
