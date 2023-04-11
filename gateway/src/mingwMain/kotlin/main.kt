package dev.kord.gateway

import io.ktor.client.*
import io.ktor.client.engine.winhttp.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.runBlocking

public fun main(): Unit = runBlocking {
    val client = HttpClient(WinHttp) {
        install(WebSockets)
    }

    val session = client.webSocketSession("wss://ws.postman-echo.com/raw")
    session.outgoing.send(Frame.Text("test"))
    println(session.incoming.receive().data.decodeToString())
}
