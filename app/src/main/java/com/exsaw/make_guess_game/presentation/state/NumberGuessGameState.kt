package com.exsaw.make_guess_game.presentation.state


data class NumberGuessGameState(
    val secretNumber: Int,
    val attempts: Int = 0,
    val enteredNumber: Int? = null,
    val isGuessCorrect: Boolean = false,
)