package com.example.iidxtrainingcalc.domain.model

@JvmInline
value class WhiteNumber private constructor(val value: Int) {

    companion object {
        private const val MIN = 0
        private const val MAX = 1000

        fun create(value: Int): Result<WhiteNumber> {
            return if (value in MIN..MAX) {
                Result.success(WhiteNumber(value))
            } else {
                Result.failure(IllegalArgumentException("WhiteNumber must be between $MIN and $MAX. Provided: $value"))
            }
        }
    }
}
