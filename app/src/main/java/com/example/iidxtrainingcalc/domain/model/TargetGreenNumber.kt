package com.example.iidxtrainingcalc.domain.model

@JvmInline
value class TargetGreenNumber private constructor(val value: Int) {

    companion object {
        private const val MIN = 1 // 分母になるため0は不可
        
        fun create(value: Int): Result<TargetGreenNumber> {
            return if (value >= MIN) {
                Result.success(TargetGreenNumber(value))
            } else {
                Result.failure(IllegalArgumentException("TargetGreenNumber must be greater than or equal to $MIN. Provided: $value"))
            }
        }
    }
}
