package com.exsaw.make_guess_game.presentation.di

import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named

enum class CoreQualifiers(val qualifier: Qualifier) {
    SCOPE_FOR_WRITE_OPERATIONS(named("SCOPE_FOR_WRITE_OPERATIONS")),
    APP_SCOPE(named("APP_SCOPE"))
}