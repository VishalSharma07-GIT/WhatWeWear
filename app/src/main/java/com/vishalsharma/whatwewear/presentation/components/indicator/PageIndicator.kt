package com.vishalsharma.whatwewear.presentation.components.indicator

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vishalsharma.whatwewear.ui.theme.TextSecondary
import com.vishalsharma.whatwewear.ui.theme.IndicatorActive
import com.vishalsharma.whatwewear.ui.theme.IndicatorInactive
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun PageIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->

            val width = animateDpAsState(
                targetValue = if (index == currentPage) 28.dp else 10.dp,
                animationSpec = tween(durationMillis = 300),
                label = "IndicatorWidth"
            )

            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .width(width.value)
                    .height(10.dp)
                    .background(
                        color = if (index == currentPage) {
                            IndicatorActive
                        } else {
                            IndicatorInactive
                        },
                        shape = RoundedCornerShape(50)                    )
            )

        }
    }
}
