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
     */
    fun calculate(
        baseBpm: Bpm,
        whiteNumber: WhiteNumber,
        playbackRate: PlaybackRate,
        targetGreenNumber: TargetGreenNumber
    ): Result<HiSpeed> {
        return try {
            val numerator = 174.0f * (1000 - whiteNumber.value)
            val denominator = (baseBpm.value * playbackRate.value) * targetGreenNumber.value
            
            if (denominator == 0.0f) {
                return Result.failure(ArithmeticException("Denominator is zero, cannot calculate HiSpeed."))
            }

            val requiredHiSpeedValue = numerator / denominator
            HiSpeed.create(requiredHiSpeedValue)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
