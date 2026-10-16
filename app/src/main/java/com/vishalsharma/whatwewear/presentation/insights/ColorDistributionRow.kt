
package com.vishalsharma.whatwewear.presentation.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ColorDistributionRow(
    colorName: String,
    count: Int,
    total: Int
) {
    val fraction = if (total > 0) {
        (count.toFloat() / total).coerceIn(0f, 1f)
    } else {
        0f
    }

    val percentage = (fraction * 100).toInt()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFF7F2EC)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(
                                color = colorForName(colorName),
                                shape = CircleShape
                            )
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = colorName,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF211B18)
                        )

                        Text(
                            text = if (count == 1) "1 item" else "$count items",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF89827B)
                        )
                    }
                }

                Text(
                    text = "$percentage%",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFFB8906D)
                )
            }

            LinearProgressIndicator(
                progress = { fraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = Color(0xFFB8906D),
                trackColor = Color(0xFFE7DDD3),
                gapSize = 0.dp,
                drawStopIndicator = {}
            )
        }
    }
}

private fun colorForName(name: String): Color {
    return when (name.trim().lowercase()) {
        "black" -> Color(0xFF222222)
        "white" -> Color(0xFFFFFFFF)
        "red" -> Color(0xFFC62828)
        "blue", "navy" -> Color(0xFF315A85)
        "green" -> Color(0xFF638568)
        "yellow" -> Color(0xFFE5C64B)
        "pink" -> Color(0xFFE7A5B5)
        "purple" -> Color(0xFF8C70A8)
        "brown" -> Color(0xFF8B5E3C)
        "beige", "cream" -> Color(0xFFE5D5B8)
        "grey", "gray" -> Color(0xFF9E9E9E)
        "orange" -> Color(0xFFE58A36)
        "unspecified" -> Color(0xFFBDB5AB)
        else -> Color(0xFFB8906D)
    }
}
