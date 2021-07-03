package dev.kord.rest.response

import dev.kord.rest.route.Route
import dev.kord.rest.route.RouteParameters

class Response<R>(val result: R, val route: Route<R>, val routeParameters: RouteParameters) {

    operator fun component1(): R = result

    operator fun component2(): Route<R> = route

    operator fun component3(): RouteParameters = routeParameters


}