package com.vishalsharma.whatwewear.presentation.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vishalsharma.whatwewear.ui.theme.TextSecondary

@Composable
fun AuthDivider(
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        HorizontalDivider(
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "OR CONTINUE WITH",
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary.copy(alpha = 0.5f)
        )

        HorizontalDivider(
            modifier = Modifier.weight(1f)
        )

    }
}

