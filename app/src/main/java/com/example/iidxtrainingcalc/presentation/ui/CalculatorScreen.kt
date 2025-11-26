package com.example.iidxtrainingcalc.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.iidxtrainingcalc.presentation.viewmodel.CalculatorUiEvent
import com.example.iidxtrainingcalc.presentation.viewmodel.CalculatorViewModel
import kotlin.math.roundToInt

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header / Result Area
        ResultDisplay(hiSpeed = state.requiredHiSpeed)

        Spacer(modifier = Modifier.height(8.dp))

        // Input Fields
        NumberInputField(
            label = "Base BPM",
            value = state.baseBpm,
            onValueChange = { viewModel.onEvent(CalculatorUiEvent.OnBaseBpmChanged(it)) },
            error = state.baseBpmError,
            modifier = Modifier.fillMaxWidth()
        )

        NumberInputField(
            label = "Target Green Number",
            value = state.targetGreenNumber,
            onValueChange = { viewModel.onEvent(CalculatorUiEvent.OnTargetGreenNumberChanged(it)) },
            error = state.targetGreenNumberError,
            modifier = Modifier.fillMaxWidth()
        )

        NumberInputField(
            label = "White Number (SUD+)",
            value = state.whiteNumber,
            onValueChange = { viewModel.onEvent(CalculatorUiEvent.OnWhiteNumberChanged(it)) },
            error = state.whiteNumberError,
            modifier = Modifier.fillMaxWidth()
        )

        // Playback Rate Selector
        PlaybackRateSelector(
            currentRate = state.playbackRate,
            onRateSelected = { viewModel.onEvent(CalculatorUiEvent.OnPlaybackRateChanged(it)) }
        )

        // Error Display
        if (state.calculationError != null) {
            Text(
                text = state.calculationError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ResultDisplay(hiSpeed: Float?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Required Hi-Speed",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = if (hiSpeed != null) String.format("%.2f", hiSpeed) else "---",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun NumberInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = { 
            // Only allow numeric input
            if (it.all { char -> char.isDigit() }) {
                onValueChange(it) 
            }
        },
        label = { Text(label) },
        isError = error != null,
        supportingText = { if (error != null) Text(error) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = modifier,
        textStyle = MaterialTheme.typography.headlineSmall
    )
}

@Composable
fun PlaybackRateSelector(
    currentRate: Float,
    onRateSelected: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Playback Speed",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Common rates
            val rates = listOf(0.5f, 0.75f, 1.0f, 1.25f)
            rates.forEach { rate ->
                FilterChip(
                    selected = (currentRate == rate),
                    onClick = { onRateSelected(rate) },
                    label = { 
                        Text(
                            text = "x$rate",
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) 
                    },
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                )
            }
        }
        
        // Slider for fine tuning
        Slider(
            value = currentRate,
            onValueChange = { onRateSelected((it * 20).roundToInt() / 20f) }, // Snap to 0.05 steps
            valueRange = 0.5f..1.5f,
            steps = 19 // (1.5 - 0.5) / 0.05 - 1 = 19 steps
        )
        Text(
            text = "Current: x${String.format("%.2f", currentRate)}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.align(Alignment.End)
        )
    }
}
