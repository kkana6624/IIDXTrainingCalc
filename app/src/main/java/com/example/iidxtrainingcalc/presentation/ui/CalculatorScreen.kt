package com.example.iidxtrainingcalc.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.iidxtrainingcalc.presentation.viewmodel.CalculatorUiEvent
import com.example.iidxtrainingcalc.presentation.viewmodel.CalculatorViewModel
import java.util.Locale
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

        // Base BPM
        LabeledWheelPicker(
            label = "Base BPM",
            value = state.baseBpm.toIntOrNull() ?: 150,
            onValueChange = { viewModel.onEvent(CalculatorUiEvent.OnBaseBpmChanged(it.toString())) },
            range = 1..999
        )

        // Target Green Number
        LabeledWheelPicker(
            label = "Target Green Number",
            value = state.targetGreenNumber.toIntOrNull() ?: 300,
            onValueChange = { viewModel.onEvent(CalculatorUiEvent.OnTargetGreenNumberChanged(it.toString())) },
            range = 1..999
        )

        // White Number (SUD+)
        LabeledWheelPicker(
            label = "White Number (SUD+)",
            value = state.whiteNumber.toIntOrNull() ?: 200,
            onValueChange = { viewModel.onEvent(CalculatorUiEvent.OnWhiteNumberChanged(it.toString())) },
            range = 0..1000
        )

        // Playback Rate Selector
        PlaybackRateSelector(
            currentRate = state.playbackRate,
            onRateSelected = { viewModel.onEvent(CalculatorUiEvent.OnPlaybackRateChanged(it)) }
        )

        // Error Display (Global calculation errors)
        if (state.calculationError != null) {
            Text(
                text = state.calculationError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun LabeledWheelPicker(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange
) {
    // PagerState to control the drum roll
    val pagerState = rememberPagerState(initialPage = value - range.first) {
        range.last - range.first + 1
    }

    // Sync external value changes to the picker (e.g. reset)
    LaunchedEffect(value) {
        val targetPage = value - range.first
        if (pagerState.currentPage != targetPage && !pagerState.isScrollInProgress) {
            pagerState.animateScrollToPage(targetPage)
        }
    }

    // Sync picker changes to the callback
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            val selectedValue = range.first + page
            onValueChange(selectedValue)
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .height(120.dp)
                .width(120.dp),
            contentAlignment = Alignment.Center
        ) {
            // Selection Indicators (Lines)
            HorizontalDivider(
                modifier = Modifier.align(Alignment.TopCenter).offset(y = 40.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                thickness = 2.dp
            )
            HorizontalDivider(
                modifier = Modifier.align(Alignment.BottomCenter).offset(y = (-40).dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                thickness = 2.dp
            )

            // The Wheel
            VerticalPager(
                state = pagerState,
                contentPadding = PaddingValues(vertical = 40.dp), // Centers the selected item
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val num = range.first + page
                val isSelected = (pagerState.currentPage == page)

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = num.toString(),
                        style = if (isSelected) MaterialTheme.typography.headlineLarge else MaterialTheme.typography.titleMedium,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }
            }
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
                text = if (hiSpeed != null) String.format(Locale.US, "%.2f", hiSpeed) else "---",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
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
            text = "Current: x${String.format(Locale.US, "%.2f", currentRate)}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.align(Alignment.End)
        )
    }
}
