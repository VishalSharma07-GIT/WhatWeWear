package com.vishalsharma.whatwewear.ai

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.subject.SubjectSegmentation
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenterOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GarmentExtractor @Inject constructor() {

    private val segmenter = SubjectSegmentation.getClient(
        SubjectSegmenterOptions.Builder().enableForegroundBitmap().build()
    )

    suspend fun extract(source: Bitmap, box: List<Int>): Bitmap =
        withContext(Dispatchers.Default) {
            val cropped = crop(source, box)
            val foreground = try {
                segmenter.process(InputImage.fromBitmap(cropped, 0)).await().foregroundBitmap
            } catch (e: Exception) {
                null // model not ready: fall back to the plain crop
            }
            compose(trim(foreground) ?: cropped)
        }

    private fun crop(src: Bitmap, box: List<Int>): Bitmap {
        if (box.size != 4) return src
        val (ymin, xmin, ymax, xmax) = box.map { it.coerceIn(0, 1000) }
        if (ymax <= ymin || xmax <= xmin) return src

        val w = src.width
        val h = src.height
        val left = xmin / 1000f * w
        val right = xmax / 1000f * w
        val top = ymin / 1000f * h
        val bottom = ymax / 1000f * h
        val padX = (right - left) * 0.04f
        val padY = (bottom - top) * 0.04f

        val l = (left - padX).toInt().coerceAtLeast(0)
        val t = (top - padY).toInt().coerceAtLeast(0)
        val r = (right + padX).toInt().coerceAtMost(w)
        val b = (bottom + padY).toInt().coerceAtMost(h)
        if (r - l < 50 || b - t < 50) return src
        return Bitmap.createBitmap(src, l, t, r - l, b - t)
    }

    private fun trim(bmp: Bitmap?): Bitmap? {
        if (bmp == null) return null
        val w = bmp.width
        val h = bmp.height
        val px = IntArray(w * h)
        bmp.getPixels(px, 0, w, 0, 0, w, h)
        var minX = w; var minY = h; var maxX = -1; var maxY = -1
        for (y in 0 until h) for (x in 0 until w) {
            if (Color.alpha(px[y * w + x]) > 20) {
                if (x < minX) minX = x
                if (x > maxX) maxX = x
                if (y < minY) minY = y
                if (y > maxY) maxY = y
            }
        }
        if (maxX < 0 || (maxX - minX) < w * 0.3f || (maxY - minY) < h * 0.3f) return null
        return Bitmap.createBitmap(bmp, minX, minY, maxX - minX + 1, maxY - minY + 1)
    }

    private fun compose(item: Bitmap): Bitmap {
        val pad = (maxOf(item.width, item.height) * 0.08f).toInt()
        val out = Bitmap.createBitmap(item.width + pad * 2, item.height + pad * 2, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)
        canvas.drawColor(0xFFF2EBE4.toInt())
        canvas.drawBitmap(item, pad.toFloat(), pad.toFloat(), null)
        return out
    }
}