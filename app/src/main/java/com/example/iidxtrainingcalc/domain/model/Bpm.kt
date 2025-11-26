package com.example.iidxtrainingcalc.domain.model

@JvmInline
value class Bpm private constructor(val value: Int) {

    companion object {
        // IIDXにおける一般的なBPM範囲と想定される制約
        // 0は計算不能になるため下限は1とする
        private const val MIN = 1
        // 技術的な上限として高めの値を設定（必要に応じて調整）
        private const val MAX = 1000 

        fun create(value: Int): Result<Bpm> {
            return if (value in MIN..MAX) {
                Result.success(Bpm(value))
            } else {
                Result.failure(IllegalArgumentException("BPM must be between $MIN and $MAX. Provided: $value"))
            }
        }
    }
}
