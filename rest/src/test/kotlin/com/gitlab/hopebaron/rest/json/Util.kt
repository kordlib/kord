package com.gitlab.hopebaron.rest.json

internal fun file(name: String): String {
    val loader = Unit::class.java.classLoader
    return loader.getResource("json/$name.json").readText()
}