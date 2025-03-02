package com.exsaw.make_guess_game.presentation.tool


fun logUnlimited(
    string: String,
    maxLogSize: Int = 4000,
) {
    if (string.isNotEmpty()) {
        println(
            string.replace("\n+".toRegex(), replacement = " ")
                .chunked(maxLogSize)
                .joinToString(separator = "\n${string.first()}${string.first()}${string.first()}>>")
                .trimIndent()
        )
    }
}