package com.gitlab.hopebaron.rest.route

sealed class Position(val key: String, val value: String) {
    class Before(id: String) : Position("before", id)
    class After(id: String) : Position("after", id)
    class Around(id: String) : Position("around", id)
}