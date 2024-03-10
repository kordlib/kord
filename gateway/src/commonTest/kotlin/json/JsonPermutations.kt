package dev.kord.gateway.json

fun jsonObjectPermutations(vararg keyValuePairs: Pair<String, String>): List<String> {
    fun <T> recurse(pairs: List<T>): List<List<T>> =
        if (pairs.isEmpty()) {
            listOf(emptyList())
        } else {
            val head = pairs.first()
            val tail = pairs.subList(1, pairs.size)
            recurse(tail).flatMap { perm ->
                val len = perm.size
                (0..len).map { i -> perm.subList(0, i) + head + perm.subList(i, len) }
            }
        }
    return recurse(keyValuePairs.toList()).map { pairs ->
        pairs.joinToString(separator = ",", prefix = "{", postfix = "}") { (key, value) -> "\"$key\":$value" }
    }
}
