package com.example.iidxtrainingcalc.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class BpmTest {

    @Test
    @DisplayName("有効な値(150)の場合、インスタンス生成に成功する")
    fun create_validValue_returnsSuccess() {
        val value = 150
        val result = Bpm.create(value)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.value).isEqualTo(value)
    }

    @Test
    @DisplayName("境界値(1)の場合、インスタンス生成に成功する")
    fun create_lowerBound_returnsSuccess() {
        val value = 1
        val result = Bpm.create(value)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.value).isEqualTo(value)
    }
    
    @Test
    @DisplayName("境界値(1000)の場合、インスタンス生成に成功する")
    fun create_upperBound_returnsSuccess() {
        val value = 1000
        val result = Bpm.create(value)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.value).isEqualTo(value)
    }

    @Test
    @DisplayName("無効な値(0)の場合、インスタンス生成に失敗する")
    fun create_zeroValue_returnsFailure() {
        val value = 0
        val result = Bpm.create(value)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    @DisplayName("負の値(-150)の場合、インスタンス生成に失敗する")
    fun create_negativeValue_returnsFailure() {
        val value = -150
        val result = Bpm.create(value)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    @DisplayName("同じ値を持つオブジェクトは等価である")
    fun equals_sameValue_isTrue() {
        val bpm1 = Bpm.create(180).getOrThrow()
        val bpm2 = Bpm.create(180).getOrThrow()

        assertThat(bpm1).isEqualTo(bpm2)
    }
}
