package com.example.iidxtrainingcalc.domain.model

@JvmInline
value class HiSpeed private constructor(val value: Float) {

    companion object {
        private const val MIN = 0.0f
        
        fun create(value: Float): Result<HiSpeed> {
            return if (value >= MIN) {
                Result.success(HiSpeed(value))
            } else {
                Result.failure(IllegalArgumentException("HiSpeed must be greater than or equal to $MIN. Provided: $value"))
            }
        }
    }
}
