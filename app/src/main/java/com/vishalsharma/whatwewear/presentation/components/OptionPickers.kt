package com.vishalsharma.whatwewear.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vishalsharma.whatwewear.ui.theme.AppColors

private val Brown = AppColors.Ink
private val PillBackground = AppColors.PillFill
private val Ink = AppColors.Ink
private val Muted = AppColors.Muted

@Composable
fun FieldLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium.copy(
            letterSpacing = 1.5.sp,
            fontWeight = FontWeight.SemiBold
        ),
        color = Muted
    )
}

// Rounded pills: one tap to choose. allowClear lets a second tap un-select (for optional fields).
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipGroup(
    label: String,
    options: List<String>,
    selected: String,
    modifier: Modifier = Modifier,
    allowClear: Boolean = false,
    onSelect: (String) -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        FieldLabel(label)
        Spacer(modifier = Modifier.height(10.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                val isSelected = option.equals(selected, ignoreCase = true)
                val background by animateColorAsState(
                    targetValue = if (isSelected) Brown else PillBackground,
                    label = "chipBackground"
                )
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White else Brown,
                    label = "chipText"
                )
                Text(
                    text = option,
                    color = textColor,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(background)
                        .clickable { onSelect(if (isSelected && allowClear) "" else option) }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                )
            }
        }
    }
}

private fun swatchColor(name: String): Color = when (name.lowercase()) {
    "black" -> Color(0xFF1A1A1A)
    "white" -> Color(0xFFFFFFFF)
    "blue" -> Color(0xFF3F6CB5)
    "grey", "gray" -> Color(0xFF9A9A9A)
    "brown" -> Color(0xFF7A5539)
    "green" -> Color(0xFF4C7A52)
    "red" -> Color(0xFFB3392F)
    else -> Color(0xFFD9C9BC)
}

// Real color circles. The selected one gets a ring, and its name shows beside the label.
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColorSwatchPicker(
    label: String,
    options: List<String>,
    selected: String,
    modifier: Modifier = Modifier,
    onSelect: (String) -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            FieldLabel(label)
            if (selected.isNotBlank()) {
                Text(
                    text = "   ·   $selected",
                    color = Ink,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            options.forEach { option ->
                val isSelected = option.equals(selected, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .border(
                            width = if (isSelected) 2.dp else 0.dp,
                            color = if (isSelected) Brown else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable { onSelect(option) },
                    contentAlignment = Alignment.Center
                ) {
                    val circle = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                    if (option.equals("Other", ignoreCase = true)) {
                        Box(
                            modifier = circle.background(
                                Brush.sweepGradient(
                                    listOf(
                                        Color(0xFFE57373), Color(0xFFFFD54F), Color(0xFF81C784),
                                        Color(0xFF64B5F6), Color(0xFFBA68C8), Color(0xFFE57373)
                                    )
                                )
                            )
                        )
                    } else {
                        Box(
                            modifier = circle
                                .background(swatchColor(option))
                                .border(1.dp, Color(0xFFD9C9BC), CircleShape)
                        )
                    }
                }
            }
        }
    }
}

// Soft, rounded text field to match the pills
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Brown,
            unfocusedBorderColor = Color(0xFFD9C9BC),
            focusedLabelColor = Brown,
            cursorColor = Brown,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        modifier = modifier.fillMaxWidth()
    )
}