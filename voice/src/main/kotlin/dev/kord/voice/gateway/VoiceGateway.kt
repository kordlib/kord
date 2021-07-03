package dev.kord.voice.gateway

interface VoiceGateway {
    fun connect()

    fun resume()

    fun send()

    fun disconnect()
}