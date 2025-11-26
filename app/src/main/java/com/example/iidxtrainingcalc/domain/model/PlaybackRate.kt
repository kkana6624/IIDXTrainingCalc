package com.example.iidxtrainingcalc.domain.model

@JvmInline
value class PlaybackRate private constructor(val value: Float) {

    companion object {
        private const val MIN = 0.5f
        private const val MAX = 1.5f

        fun create(value: Float): Result<PlaybackRate> {
            return if (value in MIN..MAX) {
                Result.success(PlaybackRate(value))
            } else {
                Result.failure(IllegalArgumentException("PlaybackRate must be between $MIN and $MAX. Provided: $value"))
            }
        }
    }
}
