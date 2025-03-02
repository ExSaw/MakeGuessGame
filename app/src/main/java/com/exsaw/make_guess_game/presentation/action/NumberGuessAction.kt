package com.exsaw.make_guess_game.presentation.action

sealed interface NumberGuessAction {

    data object OnGuessButtonClick : NumberGuessAction

    data class OnNumberTextChange(val numberText: String) : NumberGuessAction
}