package com.example.iidxtrainingcalc.presentation.ui

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
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

    // Stabilize callbacks to prevent unnecessary recomposition of all pickers when one changes
    val onBaseBpmChange = remember(viewModel) {
        { valStr: Int -> viewModel.onEvent(CalculatorUiEvent.OnBaseBpmChanged(valStr.toString())) }
    }
    val onGreenNumberChange = remember(viewModel) {
        { valStr: Int -> viewModel.onEvent(CalculatorUiEvent.OnTargetGreenNumberChanged(valStr.toString())) }
    }
    val onWhiteNumberChange = remember(viewModel) {
        { valStr: Int -> viewModel.onEvent(CalculatorUiEvent.OnWhiteNumberChanged(valStr.toString())) }
    }
    val onPlaybackRateChange = remember(viewModel) {
        { valFloat: Float -> viewModel.onEvent(CalculatorUiEvent.OnPlaybackRateChanged(valFloat)) }
    }

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
            onValueChange = onBaseBpmChange,
            range = 1..500
        )

        // Target Green Number
        LabeledWheelPicker(
            label = "Target Green Number",
            value = state.targetGreenNumber.toIntOrNull() ?: 300,
            onValueChange = onGreenNumberChange,
            range = 1..999
        )

        // White Number (SUD+)
        LabeledWheelPicker(
            label = "White Number (SUD+)",
            value = state.whiteNumber.toIntOrNull() ?: 200,
            onValueChange = onWhiteNumberChange,
            range = 0..1000
        )

        // Playback Rate Selector
        PlaybackRateSelector(
            currentRate = state.playbackRate,
            onRateSelected = onPlaybackRateChange
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
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = value - range.first)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    
    val updatedOnValueChange by rememberUpdatedState(onValueChange)
    val updatedValue by rememberUpdatedState(value)

    // Sync external value changes
    LaunchedEffect(value) {
        val targetIndex = value - range.first
        if (listState.firstVisibleItemIndex != targetIndex && !listState.isScrollInProgress) {
            listState.scrollToItem(targetIndex)
        }
    }

    // Sync internal scroll changes
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }.collect { index ->
            val selectedValue = range.first + index
            if (selectedValue in range && selectedValue != updatedValue) {
                updatedOnValueChange(selectedValue)
            }
        }
    }

    // Efficiently determine center index without triggering heavy recomposition logic
    val currentCenteredIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }

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
            LazyColumn(
                state = listState,
                flingBehavior = flingBehavior,
                contentPadding = PaddingValues(vertical = 40.dp),
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(
                    count = range.count(),
                    key = { index -> range.first + index }
                ) { index ->
                    val num = range.first + index
                    val isSelected = (currentCenteredIndex == index)

                    Box(
                        modifier = Modifier
                            .height(40.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = num.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            // Efficient Styling: Use graphicsLayer for Scale/Alpha (GPU) instead of changing FontSize (CPU Layout)
                            // This maintains high performance while providing visual feedback
                            modifier = Modifier.graphicsLayer {
                                val scale = if (isSelected) 1.15f else 0.8f
                                val alphaValue = if (isSelected) 1.0f else 0.3f
                                scaleX = scale
                                scaleY = scale
                                alpha = alphaValue
                            },
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Selection Indicators
            HorizontalDivider(
                modifier = Modifier.align(Alignment.TopCenter).offset(y = 40.dp),
                color = MaterialTheme.colorScheme.primary,
                thickness = 2.dp
            )
            HorizontalDivider(
                modifier = Modifier.align(Alignment.BottomCenter).offset(y = (-40).dp),
                color = MaterialTheme.colorScheme.primary,
                thickness = 2.dp
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
