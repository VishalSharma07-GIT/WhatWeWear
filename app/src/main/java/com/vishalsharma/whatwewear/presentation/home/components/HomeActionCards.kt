
package com.vishalsharma.whatwewear.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.ArrowOutward
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val ActionText = Color(0xFF211B18)
private val ActionMuted = Color(0xFF655B57)
private val DigitizeBackground = Color(0xFFF0E4DF)
private val PlannerBackground = Color(0xFFEAE7E2)
private val ActionAccent = Color(0xFF59423C)

@Composable
fun HomeActionCards(
    onDigitizeClick: () -> Unit,
    onPlannerClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ActionCard(
            title = "Digitize",
            subtitle = "Add new pieces to your wardrobe.",
            actionLabel = "ADD ITEM",
            backgroundColor = DigitizeBackground,
            onClick = onDigitizeClick,
            modifier = Modifier.weight(1f),
            icon = {
                Icon(
                    imageVector = Icons.Outlined.AddAPhoto,
                    contentDescription = null,
                    modifier = Modifier.size(27.dp),
                    tint = ActionAccent
                )
            }
        )

        ActionCard(
            title = "Planner",
            subtitle = "Plan your looks for the week.",
            actionLabel = "VIEW PLAN",
            backgroundColor = PlannerBackground,
            onClick = onPlannerClick,
            modifier = Modifier.weight(1f),
            icon = {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(27.dp),
                    tint = ActionAccent
                )
            }
        )
    }
}

@Composable
private fun ActionCard(
    title: String,
    subtitle: String,
    actionLabel: String,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(190.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            icon()

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = ActionText,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = ActionMuted,
                minLines = 2
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = actionLabel,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = ActionText
                )

                Icon(
                    imageVector = Icons.Outlined.ArrowOutward,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp),
                    tint = ActionText
                )
            }
        }
    }
}
