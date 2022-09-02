package dev.kord.rest

import io.ktor.utils.io.*

public class NamedFile(public val name: String, public val readChannel: ByteReadChannel) {

    public val url: String get() = "attachment://$name"

    public operator fun component1(): String = name
    public operator fun component2(): ByteReadChannel = readChannel
    public operator fun component3(): String = url
}
