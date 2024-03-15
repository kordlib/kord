package dev.kord.gateway.json

fun jsonObjectPermutations(vararg keyValuePairs: Pair<String, String>): List<String> {
    fun <T> permutations(list: List<T>): List<List<T>> =
        if (list.isEmpty()) {
            listOf(emptyList())
        } else {
            val head = list.first()
            val tail = list.subList(1, list.size)
            permutations(tail).flatMap { perm ->
                val len = perm.size
                List(len + 1) { i -> perm.subList(0, i) + head + perm.subList(i, len) }
            }
        }
    return permutations(keyValuePairs.toList()).map { pairs ->
        pairs.joinToString(separator = ",", prefix = "{", postfix = "}") { (key, value) -> "\"$key\":$value" }
    }
}
