package com.example.iidxtrainingcalc.presentation.viewmodel

import com.example.iidxtrainingcalc.domain.model.PlaybackRate

/**
 * UI State for the Calculator Screen
 */
data class CalculatorUiState(
    val baseBpm: String = "",
    val whiteNumber: String = "",
    val targetGreenNumber: String = "",
    val playbackRate: Float = 1.0f,
    
    // Calculated Result
    val requiredHiSpeed: Float? = null,
    
    // Error States (if any specific field has error)
    val baseBpmError: String? = null,
    val whiteNumberError: String? = null,
    val targetGreenNumberError: String? = null,
    
    // Global calculation error
    val calculationError: String? = null
) {
    val isInputValid: Boolean
        get() = baseBpmError == null && 
                whiteNumberError == null && 
                targetGreenNumberError == null &&
                baseBpm.isNotEmpty() &&
                whiteNumber.isNotEmpty() &&
                targetGreenNumber.isNotEmpty()
}

/**
 * UI Events (User Actions)
 */
sealed interface CalculatorUiEvent {
    data class OnBaseBpmChanged(val value: String) : CalculatorUiEvent
    data class OnWhiteNumberChanged(val value: String) : CalculatorUiEvent
    data class OnTargetGreenNumberChanged(val value: String) : CalculatorUiEvent
    data class OnPlaybackRateChanged(val value: Float) : CalculatorUiEvent
}
