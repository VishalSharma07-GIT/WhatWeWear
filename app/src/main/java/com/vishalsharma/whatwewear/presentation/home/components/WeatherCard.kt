
package com.vishalsharma.whatwewear.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val WeatherBackground = Color(0xFFFFFAF7)
private val WeatherText = Color(0xFF211B18)
private val WeatherMuted = Color(0xFF77736F)
private val WeatherAccent = Color(0xFF706168)

@Composable
fun WeatherCard(
    temperature: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = WeatherBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "JAIPUR",
                    style = MaterialTheme.typography.labelLarge.copy(
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = WeatherMuted
                )

                Text(
                    text = temperature,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp
                    ),
                    color = WeatherText,
                    maxLines = 1
                )
            }

            VerticalDivider(
                modifier = Modifier.height(64.dp),
                thickness = 1.dp,
                color = Color(0xFFD3C8C1)
            )

            Box(
                modifier = Modifier.weight(1.6f),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = description.ifBlank {
                        "Perfect for light layers."
                    },
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontStyle = FontStyle.Italic,
                        lineHeight = 24.sp
                    ),
                    color = WeatherAccent
                )
            }

            Icon(
                imageVector = Icons.Outlined.WbSunny,
                contentDescription = "Sunny weather",
                modifier = Modifier.size(36.dp),
                tint = WeatherAccent
            )
        }
    }
}
