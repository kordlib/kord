package dev.kord.rest

import io.ktor.client.request.forms.*
import io.ktor.utils.io.*
import io.ktor.utils.io.jvm.javaio.*
import java.io.InputStream

public class NamedFile(public val name: String, public val channelProvider: ChannelProvider) {
    public constructor(name: String, inputStream: InputStream) : this(name, ChannelProvider { inputStream.toByteReadChannel() })
    public constructor(name: String, channel: ByteReadChannel) : this(name, ChannelProvider { channel })

    public val url: String get() = "attachment://$name"

    @Deprecated(
        "Use ByteReadChannel instead of InputStream",
        ReplaceWith("readChannel"),
        DeprecationLevel.WARNING,
    )
    public val inputStream: InputStream get() = channelProvider.block().toInputStream()

    public operator fun component1(): String = name
    public operator fun component2(): ChannelProvider = channelProvider
    public operator fun component3(): String = url

    @Deprecated("Binary compatibility", level = DeprecationLevel.HIDDEN)
    @JvmName("component2")
    @Suppress("DEPRECATION")
    public fun _component2(): InputStream = inputStream
}
