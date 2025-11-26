package com.example.iidxtrainingcalc.domain

import com.example.iidxtrainingcalc.domain.model.Bpm
import com.example.iidxtrainingcalc.domain.model.HiSpeed
import com.example.iidxtrainingcalc.domain.model.PlaybackRate
import com.example.iidxtrainingcalc.domain.model.TargetGreenNumber
import com.example.iidxtrainingcalc.domain.model.WhiteNumber

class GreenNumberCalculator {

    /**
     * 目標の緑数字(Green Number)を維持するために必要なハイスピード設定値を計算します。
     *
     * 計算式:
     * RequiredHiSpeed = (174 * (1000 - WhiteNumber)) / ((BaseBPM * PlaybackRate) * TargetGreenNumber)
     * 
     * Note: 精度向上のため内部計算は Double (64bit) で行います。
     */
    fun calculate(
        baseBpm: Bpm,
        whiteNumber: WhiteNumber,
        playbackRate: PlaybackRate,
        targetGreenNumber: TargetGreenNumber
    ): Result<HiSpeed> {
        return try {
            // Use Double for calculation to minimize floating point errors
            val numerator = 174.0 * (1000 - whiteNumber.value)
            val denominator = (baseBpm.value.toDouble() * playbackRate.value.toDouble()) * targetGreenNumber.value.toDouble()
            
            if (denominator == 0.0) {
                return Result.failure(ArithmeticException("Denominator is zero, cannot calculate HiSpeed."))
            }

            val requiredHiSpeedValue = numerator / denominator
            
            // Convert back to Float for the Value Object
            HiSpeed.create(requiredHiSpeedValue.toFloat())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
