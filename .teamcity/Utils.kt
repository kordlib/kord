operator fun String?.plus(args: Iterable<String>) =
    (this?.plus(' ') ?: "").plus(args.joinToString(" "))
