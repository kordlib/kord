package dev.kord.rest.builder.message

import dev.kord.rest.NamedFile
import io.ktor.client.request.forms.*
import io.ktor.util.cio.*
import java.nio.file.Path
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract

/** Adds a [file][NamedFile] with the given [path] to [files][MessageBuilder.files]. */
public fun MessageBuilder.addFile(path: Path): NamedFile =
    addFile(path.fileName.toString(), ChannelProvider { path.readChannel() })

/**
 * Adds a [file][NamedFile] with the given [path] to [files][MessageBuilder.files].
 *
 * The corresponding attachment object can be configured with [builder].
 */
public inline fun MessageBuilder.addFile(path: Path, builder: AttachmentBuilder.() -> Unit): NamedFile {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    return addFile(path.fileName.toString(), ChannelProvider { path.readChannel() }, builder)
}
