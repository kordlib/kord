package dev.kord.rest

import java.io.InputStream

class NamedFile(val name: String, val inputStream: InputStream) {

    val url: String get() = "attachment://$name"

    operator fun component1() = name
    operator fun component2() = inputStream
    operator fun component3() = url
}