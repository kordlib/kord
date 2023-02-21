package dev.kord.core.exception

/**
 * Thrown when Kord cannot successfully initialize.
 */
public class KordInitializationException : Exception {
    public constructor(message: String) : super(message)
    public constructor(cause: Throwable) : super(cause)
    public constructor(message: String, cause: Throwable) : super(message, cause)
}
