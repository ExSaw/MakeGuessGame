package com.exsaw.make_guess_game.presentation.core

import kotlinx.coroutines.CoroutineDispatcher

interface IDispatchersProvider {
    val main: CoroutineDispatcher
    val mainImmediate: CoroutineDispatcher
    val default: CoroutineDispatcher
    val io: CoroutineDispatcher
}