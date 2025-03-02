package com.exsaw.make_guess_game.presentation.di

import com.exsaw.make_guess_game.presentation.viewmodel.NumberGuessViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val numberGuessScreenModule = module {
    viewModel {
        NumberGuessViewModel(
            dispatchers = get()
        )
    }
}