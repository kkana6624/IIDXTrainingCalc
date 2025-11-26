package com.example.iidxtrainingcalc.domain

import com.example.iidxtrainingcalc.domain.model.Bpm
import com.example.iidxtrainingcalc.domain.model.PlaybackRate
import com.example.iidxtrainingcalc.domain.model.TargetGreenNumber
import com.example.iidxtrainingcalc.domain.model.WhiteNumber
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class GreenNumberCalculatorTest {

    private val calculator = GreenNumberCalculator()

    @Test
    @DisplayName("標準的なケースでのHiSpeed計算")
    fun calculate_standardCase_returnsCorrectHiSpeed() {
        // Given
        val baseBpm = Bpm.create(150).getOrThrow()
        val whiteNumber = WhiteNumber.create(300).getOrThrow()
        val playbackRate = PlaybackRate.create(1.0f).getOrThrow()
        val targetGreenNumber = TargetGreenNumber.create(290).getOrThrow()

        // Formula: (174 * (1000 - 300)) / ((150 * 1.0) * 290)
        // = (174 * 700) / (150 * 290)
        // = 121800 / 43500
        // = 2.8
        val expectedHiSpeed = 2.8f

        // When
        val result = calculator.calculate(baseBpm, whiteNumber, playbackRate, targetGreenNumber)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.value).isWithin(0.01f).of(expectedHiSpeed)
    }

    @Test
    @DisplayName("再生速度を0.5倍にした場合、HiSpeedは2倍になる")
    fun calculate_halfPlaybackRate_returnsDoubleHiSpeed() {
        // Given
        val baseBpm = Bpm.create(150).getOrThrow()
        val whiteNumber = WhiteNumber.create(300).getOrThrow()
        val playbackRate = PlaybackRate.create(0.5f).getOrThrow()
        val targetGreenNumber = TargetGreenNumber.create(290).getOrThrow()

        // Expected: 2.8 * 2 = 5.6
        val expectedHiSpeed = 5.6f

        // When
        val result = calculator.calculate(baseBpm, whiteNumber, playbackRate, targetGreenNumber)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.value).isWithin(0.01f).of(expectedHiSpeed)
    }
    
    @Test
    @DisplayName("GreenNumberが極端に小さい場合でも計算できる")
    fun calculate_smallGreenNumber_returnsLargeHiSpeed() {
        // Given
        val baseBpm = Bpm.create(150).getOrThrow()
        val whiteNumber = WhiteNumber.create(0).getOrThrow() // サドプラなし
        val playbackRate = PlaybackRate.create(1.0f).getOrThrow()
        val targetGreenNumber = TargetGreenNumber.create(10).getOrThrow() // 極端に速い設定

        // Formula: (174 * 1000) / (150 * 10)
        // = 174000 / 1500
        // = 116.0
        val expectedHiSpeed = 116.0f

        // When
        val result = calculator.calculate(baseBpm, whiteNumber, playbackRate, targetGreenNumber)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.value).isWithin(0.01f).of(expectedHiSpeed)
    }
}
