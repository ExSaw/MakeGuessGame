package com.exsaw.make_guess_game.presentation.di

import com.exsaw.make_guess_game.presentation.tool.Vibrator
import com.exsaw.make_guess_game.presentation.core.IDispatchersProvider
import com.exsaw.make_guess_game.presentation.core.StandardDispatchers
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val mainModule = module {
    single(CoreQualifiers.SCOPE_FOR_WRITE_OPERATIONS.qualifier) {
        CoroutineScope(SupervisorJob() + Dispatchers.IO + CoroutineName(CoreQualifiers.SCOPE_FOR_WRITE_OPERATIONS.name))
    }
    single(CoreQualifiers.APP_SCOPE.qualifier) {
        CoroutineScope(Dispatchers.Default + CoroutineName(CoreQualifiers.APP_SCOPE.name))
    }
    singleOf<IDispatchersProvider>(::StandardDispatchers)
    singleOf(::Vibrator)
}