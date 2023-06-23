package dev.kord.common.exception

/**
 * Signals that some kind of exception occurred when attempting to interact with an entity.
 */
public abstract class RequestException : Exception {

    public constructor(message: String) : super(message)
    public constructor(cause: Throwable) : super(cause)
    public constructor(message: String, cause: Throwable) : super(message, cause)
}
