package dev.kord.rest

import java.io.InputStream

public class NamedFile(public val name: String, public val inputStream: InputStream) {

    public val url: String get() = "attachment://$name"

    public operator fun component1(): String = name
    public operator fun component2(): InputStream = inputStream
    public operator fun component3(): String = url
}
