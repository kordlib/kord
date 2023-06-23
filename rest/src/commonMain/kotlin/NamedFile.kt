package dev.kord.rest

import io.ktor.client.request.forms.*

public class NamedFile(public val name: String, public val contentProvider: ChannelProvider) {

    public val url: String get() = "attachment://$name"

    public operator fun component1(): String = name
    public operator fun component2(): ChannelProvider = contentProvider
    public operator fun component3(): String = url
}
