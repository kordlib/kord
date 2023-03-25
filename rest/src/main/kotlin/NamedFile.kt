package dev.kord.rest

import io.ktor.client.request.forms.*
import io.ktor.utils.io.jvm.javaio.*
import java.io.InputStream
import kotlin.DeprecationLevel.HIDDEN

public class NamedFile(public val name: String, public val contentProvider: ChannelProvider) {
    /** @suppress */
    @Deprecated(
        "Use lazy ChannelProvider instead of InputStream. You should also make sure that the stream/channel is only " +
                "opened inside the block of the ChannelProvider because it could otherwise be read multiple times " +
                "(which isn't allowed).",
        ReplaceWith(
            "NamedFile(name, ChannelProvider { inputStream.toByteReadChannel() })",
            "io.ktor.client.request.forms.ChannelProvider",
            "io.ktor.utils.io.jvm.javaio.toByteReadChannel",
        ),
        level = HIDDEN,
    )
    public constructor(name: String, inputStream: InputStream) : this(inputStream, name)

    // TODO remove when above constructor is removed
    internal constructor(inputStream: InputStream, name: String) : this(
        name,
        ChannelProvider { inputStream.toByteReadChannel() },
    )

    public val url: String get() = "attachment://$name"

    /** @suppress */
    @Deprecated(
        "Use ChannelProvider instead of InputStream",
        ReplaceWith(
            "contentProvider.block().toInputStream()",
            "io.ktor.utils.io.jvm.javaio.toInputStream",
        ),
        level = HIDDEN,
    )
    public val inputStream: InputStream get() = _inputStream
    private val _inputStream get() = contentProvider.block().toInputStream() // TODO remove with `inputStream`

    public operator fun component1(): String = name
    public operator fun component2(): ChannelProvider = contentProvider
    public operator fun component3(): String = url

    @Deprecated("Binary compatibility", level = DeprecationLevel.HIDDEN)
    @JvmName("component2")
    @Suppress("DEPRECATION_ERROR", "FunctionName")
    public fun _component2(): InputStream = _inputStream
}
