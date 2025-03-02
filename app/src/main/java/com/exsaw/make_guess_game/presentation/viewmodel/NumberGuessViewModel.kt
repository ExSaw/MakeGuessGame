package com.exsaw.make_guess_game.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exsaw.make_guess_game.presentation.action.NumberGuessAction
import com.exsaw.make_guess_game.presentation.core.IDispatchersProvider
import com.exsaw.make_guess_game.presentation.state.NumberGuessGameState
import com.exsaw.make_guess_game.presentation.state.NumberGuessUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlin.random.Random

class NumberGuessViewModel(
    private val dispatchers: IDispatchersProvider,
) : ViewModel() {

    private companion object {
        const val INITIAL_GUESS_TEXT = "Guess a number between [0,100]"
        const val INITIAL_GUESS_BUTTON_TEXT = "Make Guess"
    }

    private val numberTextState = MutableStateFlow<String?>(null)

    private val _gameState = MutableStateFlow<NumberGuessGameState>(getInitialGameStateData())
    private val gameState = _gameState.asStateFlow()

    private val _uiState = MutableStateFlow<NumberGuessUIState>(getInitialUIStateData())
    val uiState = _uiState.asStateFlow()

    init {
        combine(
            numberTextState,
            gameState
        ) { currentEnteredNumberText, gameState ->

            val updatedUiState = when {
                gameState.isGuessCorrect -> {
                    NumberGuessUIState(
                        numberText = currentEnteredNumberText ?: "",
                        guessText = "That was it! You needed ${gameState.attempts} attempts",
                        isGuessCorrect = true,
                        guessButtonText = "Start new game",
                    )
                }

                gameState.enteredNumber != null && gameState.enteredNumber < gameState.secretNumber -> {
                    NumberGuessUIState(
                        numberText = currentEnteredNumberText ?: "",
                        guessText = "Nope, my number is larger",
                        isGuessCorrect = false,
                        guessButtonText = INITIAL_GUESS_BUTTON_TEXT,
                    )
                }

                gameState.enteredNumber != null && gameState.enteredNumber > gameState.secretNumber -> {
                    NumberGuessUIState(
                        numberText = currentEnteredNumberText ?: "",
                        guessText = "Nope, my number is smaller",
                        isGuessCorrect = false,
                        guessButtonText = INITIAL_GUESS_BUTTON_TEXT,
                    )
                }

                else -> {
                    getInitialUIStateData(
                        numberText = currentEnteredNumberText ?: ""
                    )
                }
            }

            _uiState.update { updatedUiState }

        }.stateIn(
            scope = viewModelScope + dispatchers.default,
            started = SharingStarted.Eagerly,
            initialValue = null
        )
    }

    fun onAction(action: NumberGuessAction) {
        viewModelScope.launch(dispatchers.default) {
            when (action) {
                is NumberGuessAction.OnGuessButtonClick -> {
                    _gameState.update { gameState ->
                        if (gameState.isGuessCorrect) {
                            getInitialGameStateData()
                        } else {
                            val currentEnteredNumber = numberTextState.value?.toIntOrNull()
                            gameState.copy(
                                attempts = gameState.attempts + 1,
                                enteredNumber = currentEnteredNumber,
                                isGuessCorrect = gameState.secretNumber == currentEnteredNumber
                            )
                        }
                    }
                }

                is NumberGuessAction.OnNumberTextChange -> {
                    numberTextState.update { action.numberText }
                }
            }
        }
    }

    private fun getInitialGameStateData() =
        NumberGuessGameState(
            secretNumber = Random.nextInt(from = 0, until = 101),
            attempts = 0,
            enteredNumber = null,
            isGuessCorrect = false
        )

    private fun getInitialUIStateData(
        numberText: String = ""
    ) =
        NumberGuessUIState(
            numberText = numberText,
            guessText = INITIAL_GUESS_TEXT,
            isGuessCorrect = false,
            guessButtonText = INITIAL_GUESS_BUTTON_TEXT,
        )
}