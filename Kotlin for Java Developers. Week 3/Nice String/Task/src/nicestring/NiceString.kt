package nicestring

fun String.isNice(): Boolean {
    val criteria = listOf(
            ::criteria1,
            ::criteria2,
            ::criteria3
    )
    return criteria.count { run(it) } >= 2
}

fun String.criteria1(): Boolean {
    return !(this.contains("bu") ||
             this.contains("ba") ||
             this.contains("be"))
}

fun String.criteria2(): Boolean {
    val vowels = "aeiou"
    return this.count { vowels.contains(it)} >= 3
}

fun String.criteria3(): Boolean {
    for (i in 0 until this.length-1) {
        if (this[i] == this[i+1]) {
            return true
        }
    }
    return false
}