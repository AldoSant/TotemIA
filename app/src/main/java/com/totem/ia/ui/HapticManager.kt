package com.totem.ia.ui

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HapticManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    fun triggerClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrate(VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            if (vibrator.hasVibrator()) vibrator.vibrate(10)
        }
    }

    fun triggerSuccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrate(VibrationEffect.createOneShot(20, 50))
        } else {
            @Suppress("DEPRECATION")
            if (vibrator.hasVibrator()) vibrator.vibrate(20)
        }
    }

    fun triggerError() {
        val pattern = longArrayOf(0, 50, 50, 50)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            @Suppress("DEPRECATION")
            if (vibrator.hasVibrator()) vibrator.vibrate(pattern, -1)
        }
    }

    fun triggerListening() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
        }
    }

    private fun vibrate(effect: VibrationEffect) {
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(effect)
        }
    }
}
