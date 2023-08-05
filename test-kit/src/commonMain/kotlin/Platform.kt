package dev.kord.test

import io.ktor.utils.io.*
import com.goncalossilva.resources.Resource

expect object Platform {
    val IS_JVM: Boolean
    val IS_NODE: Boolean
    val IS_BROWSER: Boolean
    val IS_MINGW: Boolean
    val IS_LINUX: Boolean
    val IS_DARWIN: Boolean
}

expect fun getEnv(name: String): String?
fun file(path: String): String = resource(path).readText()
fun readFile(path: String): ByteReadChannel = ByteReadChannel(resource(path).readBytes())

private fun resource(path: String) = Resource("src/commonTest/resources/$path")
