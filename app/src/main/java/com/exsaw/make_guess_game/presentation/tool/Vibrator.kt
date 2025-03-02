package com.exsaw.make_guess_game.presentation.tool

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class Vibrator(context: Context) {

    private companion object {
        const val DEFAULT_DURATION = 70L
    }

    // Context.VIBRATOR_SERVICE for >= API_31 is deprecated
    private val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager =
            context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    fun vibrate(
        duration: Long = DEFAULT_DURATION,
        pattern: Int = 0,
        isCutItself: Boolean = false,
    ) {
        if (isCutItself) {
            vibrator.cancel()
        }
        when (pattern) {
            1 -> {
                val patternError = longArrayOf(0, duration, 100, duration)
                vibrator.vibrate(
                    VibrationEffect.createWaveform(
                        patternError,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            }

            else -> {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        duration,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            }
        }
    }
}