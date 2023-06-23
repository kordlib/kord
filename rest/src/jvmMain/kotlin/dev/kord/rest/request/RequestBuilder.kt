package dev.kord.rest.request

import io.ktor.client.request.forms.*
import io.ktor.util.cio.*
import java.nio.file.Path

public fun <T> RequestBuilder<T>.file(path: Path): Unit =
    file(path.fileName.toString(), ChannelProvider { path.readChannel() })
