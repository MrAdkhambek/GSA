package mr.adkhambek.gsa_processor.ktx


internal val camelRegex = "(?<=[a-zA-Z])[A-Z]".toRegex()
internal val snakeRegex = "_[a-zA-Z]".toRegex()

// String extensions
internal fun String.camelToSnakeCase(): String {
    return camelRegex.replace(this) {
        "_${it.value}"
    }.toLowerCase()
}

internal fun String.snakeToLowerCamelCase(): String {
    return snakeRegex.replace(this) {
        it.value.replace("_", "").toUpperCase()
    }
}

internal fun String.snakeToUpperCamelCase(): String {
    return this.snakeToLowerCamelCase().capitalize()
}

internal fun String.camelToUpperSnakeCase(): String {
    return this.camelToSnakeCase().toUpperCase()
}