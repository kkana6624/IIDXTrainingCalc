package com.example.iidxtrainingcalc.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class PlaybackRateTest {

    @Test
    @DisplayName("有効な値(1.0)の場合、インスタンス生成に成功する")
    fun create_validValue_returnsSuccess() {
        val value = 1.0f
        val result = PlaybackRate.create(value)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.value).isEqualTo(value)
    }

    @Test
    @DisplayName("境界値(0.5)の場合、インスタンス生成に成功する")
    fun create_lowerBound_returnsSuccess() {
        val value = 0.5f
        val result = PlaybackRate.create(value)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.value).isEqualTo(value)
    }

    @Test
    @DisplayName("境界値(1.5)の場合、インスタンス生成に成功する")
    fun create_upperBound_returnsSuccess() {
        val value = 1.5f
        val result = PlaybackRate.create(value)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.value).isEqualTo(value)
    }

    @Test
    @DisplayName("範囲外の値(0.4)の場合、インスタンス生成に失敗する")
    fun create_tooSmallValue_returnsFailure() {
        val value = 0.4f
        val result = PlaybackRate.create(value)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    @DisplayName("範囲外の値(1.6)の場合、インスタンス生成に失敗する")
    fun create_tooLargeValue_returnsFailure() {
        val value = 1.6f
        val result = PlaybackRate.create(value)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    @DisplayName("同じ値を持つオブジェクトは等価である")
    fun equals_sameValue_isTrue() {
        val rate1 = PlaybackRate.create(1.25f).getOrThrow()
        val rate2 = PlaybackRate.create(1.25f).getOrThrow()

        assertThat(rate1).isEqualTo(rate2)
    }
}
