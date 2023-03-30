package dev.kord.common.http

import dev.kord.common.annotation.KordInternal
import io.ktor.client.engine.js.*

@KordInternal
public actual typealias HttpEngine = Js
