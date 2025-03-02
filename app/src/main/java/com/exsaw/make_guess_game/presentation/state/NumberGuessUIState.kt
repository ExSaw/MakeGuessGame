package com.exsaw.make_guess_game.presentation.state


data class NumberGuessUIState(
    val numberText: String = "",
    val guessText: String,
    val guessButtonText: String,
    val isGuessCorrect: Boolean = false,
)