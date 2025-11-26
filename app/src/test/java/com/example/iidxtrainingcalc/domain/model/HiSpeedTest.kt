package com.example.iidxtrainingcalc.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class HiSpeedTest {

    @Test
    @DisplayName("有効な値(2.5)の場合、インスタンス生成に成功する")
    fun create_validValue_returnsSuccess() {
        val value = 2.5f
        val result = HiSpeed.create(value)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.value).isEqualTo(value)
    }

    @Test
    @DisplayName("境界値(0.0)の場合、インスタンス生成に成功する")
    fun create_zeroValue_returnsSuccess() {
        val value = 0.0f
        val result = HiSpeed.create(value)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.value).isEqualTo(value)
    }

    @Test
    @DisplayName("負の値(-0.1)の場合、インスタンス生成に失敗する")
    fun create_negativeValue_returnsFailure() {
        val value = -0.1f
        val result = HiSpeed.create(value)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    @DisplayName("同じ値を持つオブジェクトは等価である")
    fun equals_sameValue_isTrue() {
        val hs1 = HiSpeed.create(3.0f).getOrThrow()
        val hs2 = HiSpeed.create(3.0f).getOrThrow()

        assertThat(hs1).isEqualTo(hs2)
    }
}
