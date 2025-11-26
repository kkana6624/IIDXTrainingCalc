package com.example.iidxtrainingcalc.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iidxtrainingcalc.domain.GreenNumberCalculator
import com.example.iidxtrainingcalc.domain.model.Bpm
import com.example.iidxtrainingcalc.domain.model.PlaybackRate
import com.example.iidxtrainingcalc.domain.model.TargetGreenNumber
import com.example.iidxtrainingcalc.domain.model.WhiteNumber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val calculator: GreenNumberCalculator
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    fun onEvent(event: CalculatorUiEvent) {
        when (event) {
            is CalculatorUiEvent.OnBaseBpmChanged -> {
                _uiState.update { it.copy(baseBpm = event.value) }
                calculate()
            }
            is CalculatorUiEvent.OnWhiteNumberChanged -> {
                _uiState.update { it.copy(whiteNumber = event.value) }
                calculate()
            }
            is CalculatorUiEvent.OnTargetGreenNumberChanged -> {
                _uiState.update { it.copy(targetGreenNumber = event.value) }
                calculate()
            }
            is CalculatorUiEvent.OnPlaybackRateChanged -> {
                _uiState.update { it.copy(playbackRate = event.value) }
                calculate()
            }
        }
    }

    private fun calculate() {
        viewModelScope.launch {
            val state = _uiState.value
            
            // Validate Inputs
            // Parse integers safely
            val bpmVal = state.baseBpm.toIntOrNull()
            val whiteVal = state.whiteNumber.toIntOrNull()
            val greenVal = state.targetGreenNumber.toIntOrNull()
            val rateVal = state.playbackRate

            // Reset errors initially
            _uiState.update { 
                it.copy(
                    baseBpmError = if (state.baseBpm.isNotEmpty() && bpmVal == null) "Invalid number" else null,
                    whiteNumberError = if (state.whiteNumber.isNotEmpty() && whiteVal == null) "Invalid number" else null,
                    targetGreenNumberError = if (state.targetGreenNumber.isNotEmpty() && greenVal == null) "Invalid number" else null,
                    requiredHiSpeed = null,
                    calculationError = null
                )
            }

            // If any parsing failed or fields are empty, stop
            if (bpmVal == null || whiteVal == null || greenVal == null) {
                return@launch
            }

            // Domain Object Creation & Validation
            val bpmResult = Bpm.create(bpmVal)
            val whiteResult = WhiteNumber.create(whiteVal)
            val greenResult = TargetGreenNumber.create(greenVal)
            val rateResult = PlaybackRate.create(rateVal)

            // Update UI with specific domain validation errors if any
            _uiState.update {
                it.copy(
                    baseBpmError = bpmResult.exceptionOrNull()?.message,
                    whiteNumberError = whiteResult.exceptionOrNull()?.message,
                    targetGreenNumberError = greenResult.exceptionOrNull()?.message
                )
            }

            // If all domain objects are valid, proceed to calculation
            if (bpmResult.isSuccess && whiteResult.isSuccess && greenResult.isSuccess && rateResult.isSuccess) {
                val result = calculator.calculate(
                    baseBpm = bpmResult.getOrThrow(),
                    whiteNumber = whiteResult.getOrThrow(),
                    playbackRate = rateResult.getOrThrow(),
                    targetGreenNumber = greenResult.getOrThrow()
                )

                if (result.isSuccess) {
                    _uiState.update { it.copy(requiredHiSpeed = result.getOrThrow().value) }
                } else {
                    _uiState.update { it.copy(calculationError = result.exceptionOrNull()?.message) }
                }
            }
        }
    }
}
