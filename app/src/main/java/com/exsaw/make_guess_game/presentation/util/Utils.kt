package com.exsaw.make_guess_game.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp

@Composable
fun dpToSp(dp: Dp) = with(LocalDensity.current) { dp.toSp() }

val Int.nonScaledSp
    @Composable
    get() = (this / LocalDensity.current.fontScale).sp