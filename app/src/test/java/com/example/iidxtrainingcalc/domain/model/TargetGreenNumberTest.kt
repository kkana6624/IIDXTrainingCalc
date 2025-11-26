package com.example.iidxtrainingcalc.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class TargetGreenNumberTest {

    @Test
    @DisplayName("有効な値(300)の場合、インスタンス生成に成功する")
    fun create_validValue_returnsSuccess() {
        val value = 300
        val result = TargetGreenNumber.create(value)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.value).isEqualTo(value)
    }

    @Test
    @DisplayName("境界値(1)の場合、インスタンス生成に成功する")
    fun create_lowerBound_returnsSuccess() {
        val value = 1
        val result = TargetGreenNumber.create(value)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.value).isEqualTo(value)
    }
    
    @Test
    @DisplayName("無効な値(0)の場合、インスタンス生成に失敗する")
    fun create_zeroValue_returnsFailure() {
        val value = 0
        val result = TargetGreenNumber.create(value)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    @DisplayName("負の値(-1)の場合、インスタンス生成に失敗する")
    fun create_negativeValue_returnsFailure() {
        val value = -1
        val result = TargetGreenNumber.create(value)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    @DisplayName("同じ値を持つオブジェクトは等価である")
    fun equals_sameValue_isTrue() {
        val num1 = TargetGreenNumber.create(290).getOrThrow()
        val num2 = TargetGreenNumber.create(290).getOrThrow()

        assertThat(num1).isEqualTo(num2)
    }
}
