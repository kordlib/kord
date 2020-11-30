package dev.kord.common.exception

/**
 * Signals that some kind of exception occurred when attempting to interact with an entity.
 */
abstract class RequestException : Exception {

    constructor(message: String) : super(message)
    constructor(cause: Throwable) : super(cause)
    constructor(message: String, cause: Throwable) : super(message, cause)

}