package com.exsaw.make_guess_game.presentation.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Если ради юнит тестов вам пришло в голову сделать этот класс публичным,
 * то стоит подумать ещё раз, а класс не трогать!
 */
internal class StandardDispatchers : IDispatchersProvider {
    override val main: CoroutineDispatcher
        get() = Dispatchers.Main
    override val mainImmediate: CoroutineDispatcher
        get() = Dispatchers.Main.immediate
    override val default: CoroutineDispatcher
        get() = Dispatchers.Default
    override val io: CoroutineDispatcher
        get() = Dispatchers.IO
}