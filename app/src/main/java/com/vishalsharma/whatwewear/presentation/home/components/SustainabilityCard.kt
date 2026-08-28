package com.vishalsharma.whatwewear.presentation.home.components

import android.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp

@Composable
fun SustainabilityCard(
    score: Int,
    message: String
){
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text="$score%",
            style = MaterialTheme.typography.displaySmall
        )
        LinearProgressIndicator(
            progress = {
                score.coerceIn(0,100) / 100f
            },
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}