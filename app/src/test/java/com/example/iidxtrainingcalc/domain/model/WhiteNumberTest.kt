package com.example.iidxtrainingcalc.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class WhiteNumberTest {

    @Test
    @DisplayName("有効な値(300)の場合、インスタンス生成に成功する")
    fun create_validValue_returnsSuccess() {
        val value = 300
        val result = WhiteNumber.create(value)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.value).isEqualTo(value)
    }

    @Test
    @DisplayName("境界値(0)の場合、インスタンス生成に成功する")
    fun create_lowerBound_returnsSuccess() {
        val value = 0
        val result = WhiteNumber.create(value)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.value).isEqualTo(value)
    }

    @Test
    @DisplayName("境界値(1000)の場合、インスタンス生成に成功する")
    fun create_upperBound_returnsSuccess() {
        val value = 1000
        val result = WhiteNumber.create(value)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.value).isEqualTo(value)
    }

    @Test
    @DisplayName("範囲外の値(-1)の場合、インスタンス生成に失敗する")
    fun create_negativeValue_returnsFailure() {
        val value = -1
        val result = WhiteNumber.create(value)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    @DisplayName("範囲外の値(1001)の場合、インスタンス生成に失敗する")
    fun create_tooLargeValue_returnsFailure() {
        val value = 1001
        val result = WhiteNumber.create(value)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    @DisplayName("同じ値を持つオブジェクトは等価である")
    fun equals_sameValue_isTrue() {
        val num1 = WhiteNumber.create(250).getOrThrow()
        val num2 = WhiteNumber.create(250).getOrThrow()

        assertThat(num1).isEqualTo(num2)
    }
}
