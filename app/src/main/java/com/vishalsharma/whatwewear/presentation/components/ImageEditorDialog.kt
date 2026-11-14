package com.vishalsharma.whatwewear.presentation.components

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Flip
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material.icons.outlined.RotateRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.roundToInt

private enum class Handle { None, TopLeft, TopRight, BottomLeft, BottomRight, Move }

private const val MinSize = 0.12f

private fun moveCrop(c: Rect, handle: Handle, dx: Float, dy: Float): Rect = when (handle) {
    Handle.TopLeft -> Rect(
        (c.left + dx).coerceIn(0f, c.right - MinSize),
        (c.top + dy).coerceIn(0f, c.bottom - MinSize),
        c.right, c.bottom
    )
    Handle.TopRight -> Rect(
        c.left,
        (c.top + dy).coerceIn(0f, c.bottom - MinSize),
        (c.right + dx).coerceIn(c.left + MinSize, 1f),
        c.bottom
    )
    Handle.BottomLeft -> Rect(
        (c.left + dx).coerceIn(0f, c.right - MinSize),
        c.top, c.right,
        (c.bottom + dy).coerceIn(c.top + MinSize, 1f)
    )
    Handle.BottomRight -> Rect(
        c.left, c.top,
        (c.right + dx).coerceIn(c.left + MinSize, 1f),
        (c.bottom + dy).coerceIn(c.top + MinSize, 1f)
    )
    Handle.Move -> {
        val left = (c.left + dx).coerceIn(0f, 1f - c.width)
        val top = (c.top + dy).coerceIn(0f, 1f - c.height)
        Rect(left, top, left + c.width, top + c.height)
    }
    Handle.None -> c
}

@Composable
fun ImageEditorDialog(
    source: Bitmap,
    onDismiss: () -> Unit,
    onDone: (Bitmap) -> Unit
) {
    var working by remember { mutableStateOf(source) }
    var crop by remember { mutableStateOf(Rect(0f, 0f, 1f, 1f)) }

    fun transform(matrix: Matrix) {
        working = Bitmap.createBitmap(working, 0, 0, working.width, working.height, matrix, true)
        crop = Rect(0f, 0f, 1f, 1f)
    }

    fun applyCrop(): Bitmap {
        val bw = working.width
        val bh = working.height
        val x = (crop.left * bw).roundToInt().coerceIn(0, bw - 1)
        val y = (crop.top * bh).roundToInt().coerceIn(0, bh - 1)
        val w = (crop.width * bw).roundToInt().coerceIn(1, bw - x)
        val h = (crop.height * bh).roundToInt().coerceIn(1, bh - y)
        return Bitmap.createBitmap(working, x, y, w, h)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .systemBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Outlined.Close, contentDescription = "Close", tint = Color.White)
                }
                Text("Edit photo", color = Color.White, fontSize = 17.sp)
                TextButton(onClick = { onDone(applyCrop()) }) {
                    Text("Done", color = Color.White, fontSize = 16.sp)
                }
            }

            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                val density = LocalDensity.current
                val scale = min(
                    constraints.maxWidth.toFloat() / working.width,
                    constraints.maxHeight.toFloat() / working.height
                )
                val widthDp = with(density) { (working.width * scale).toDp() }
                val heightDp = with(density) { (working.height * scale).toDp() }
                val imageBitmap = remember(working) { working.asImageBitmap() }

                Box(modifier = Modifier.size(widthDp, heightDp)) {
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.fillMaxSize()
                    )

                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(working) {
                                var handle = Handle.None
                                val touch = 40.dp.toPx()
                                detectDragGestures(
                                    onDragStart = { start ->
                                        val w = size.width.toFloat()
                                        val h = size.height.toFloat()
                                        val l = crop.left * w
                                        val t = crop.top * h
                                        val r = crop.right * w
                                        val b = crop.bottom * h
                                        fun near(x: Float, y: Float) =
                                            hypot(start.x - x, start.y - y) < touch
                                        handle = when {
                                            near(l, t) -> Handle.TopLeft
                                            near(r, t) -> Handle.TopRight
                                            near(l, b) -> Handle.BottomLeft
                                            near(r, b) -> Handle.BottomRight
                                            start.x in l..r && start.y in t..b -> Handle.Move
                                            else -> Handle.None
                                        }
                                    },
                                    onDrag = { change, drag ->
                                        change.consume()
                                        crop = moveCrop(
                                            crop,
                                            handle,
                                            drag.x / size.width,
                                            drag.y / size.height
                                        )
                                    },
                                    onDragEnd = { handle = Handle.None }
                                )
                            }
                    ) {
                        val l = crop.left * size.width
                        val t = crop.top * size.height
                        val r = crop.right * size.width
                        val b = crop.bottom * size.height
                        val dim = Color(0x99000000)

                        // Darken everything outside the crop box
                        drawRect(dim, Offset(0f, 0f), Size(size.width, t))
                        drawRect(dim, Offset(0f, b), Size(size.width, size.height - b))
                        drawRect(dim, Offset(0f, t), Size(l, b - t))
                        drawRect(dim, Offset(r, t), Size(size.width - r, b - t))

                        // Border and thirds grid
                        drawRect(Color.White, Offset(l, t), Size(r - l, b - t), style = Stroke(2.dp.toPx()))
                        val grid = Color(0x66FFFFFF)
                        for (i in 1..2) {
                            val x = l + (r - l) * i / 3f
                            val y = t + (b - t) * i / 3f
                            drawLine(grid, Offset(x, t), Offset(x, b), 1.dp.toPx())
                            drawLine(grid, Offset(l, y), Offset(r, y), 1.dp.toPx())
                        }

                        // Corner handles
                        listOf(Offset(l, t), Offset(r, t), Offset(l, b), Offset(r, b)).forEach {
                            drawCircle(Color.White, 9.dp.toPx(), it)
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ToolButton(Icons.Outlined.RotateRight, "Rotate") {
                    transform(Matrix().apply { postRotate(90f) })
                }
                ToolButton(Icons.Outlined.Flip, "Flip") {
                    transform(Matrix().apply { postScale(-1f, 1f) })
                }
                ToolButton(Icons.Outlined.Replay, "Reset") {
                    working = source
                    crop = Rect(0f, 0f, 1f, 1f)
                }
            }
        }
    }
}

@Composable
private fun ToolButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = label, tint = Color.White)
        Text(label, color = Color.White, fontSize = 12.sp)
    }
}

// The small edit icon that sits on a photo
@Composable
fun EditPhotoButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color(0xCC211B18))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Edit,
            contentDescription = "Edit photo",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}