package dev.kord.rest

import io.ktor.utils.io.*
import io.ktor.utils.io.jvm.javaio.*
import java.io.InputStream

public class NamedFile(public val name: String, public val readChannel: ByteReadChannel) {
    public constructor(name: String, inputStream: InputStream) : this(name, inputStream.toByteReadChannel())

    public val url: String get() = "attachment://$name"
    public val inputStream: InputStream get() = readChannel.toInputStream()

    public operator fun component1(): String = name
    public operator fun component2(): ByteReadChannel = readChannel
    @Deprecated("Binary compatibility", level = DeprecationLevel.HIDDEN)
    @JvmName("component2")
    public fun _component2(): InputStream = inputStream
    public operator fun component3(): String = url
}
