package com.gitlab.kordlib.core.exception

class KordInitializationException : Exception {
    constructor(message: String) : super(message)
    constructor(cause: Throwable) : super(cause)
    constructor(message: String, cause: Throwable) : super(message, cause)
}
