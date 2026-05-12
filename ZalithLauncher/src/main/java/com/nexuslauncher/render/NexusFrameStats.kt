package com.nexuslauncher.render

import kotlin.math.max
import kotlin.math.min

/**
 * NexusFrameStats — Coleta e agrega estatísticas de frame em tempo real.
 *
 * Thread-safe. Mantém janela deslizante de 120 frames.
 * Expõe FPS, frame time médio, mínimo e máximo.
 */
class NexusFrameStats(private val windowSize: Int = 120) {

    private val frameTimes = FloatArray(windowSize) { 16.67f }
    private var head    = 0
    private var count   = 0
    private var _total  = 0L

    @Volatile var totalFrames: Long = 0L
        private set

    /** Registra o tempo de renderização de um frame em milissegundos. */
    @Synchronized
    fun recordFrame(frameMs: Float) {
        if (count < windowSize) count++
        else frameTimes[head].also { /* remove oldest */ }

        frameTimes[head] = frameMs
        head = (head + 1) % windowSize
        totalFrames++
        _total++
    }

    val averageFrameMs: Float
        @Synchronized get() = if (count == 0) 16.67f else
            frameTimes.take(count).average().toFloat()

    val averageFps: Float
        get() = if (averageFrameMs > 0) 1000f / averageFrameMs else 0f

    val minFps: Float
        @Synchronized get() {
            val maxMs = frameTimes.take(count).maxOrNull() ?: 16.67f
            return if (maxMs > 0) 1000f / maxMs else 0f
        }

    val maxFps: Float
        @Synchronized get() {
            val minMs = frameTimes.take(count).minOrNull() ?: 16.67f
            return if (minMs > 0) 1000f / minMs else 0f
        }

    val p99FrameMs: Float
        @Synchronized get() {
            if (count == 0) return 16.67f
            val sorted = frameTimes.take(count).sorted()
            val idx = (count * 0.99f).toInt().coerceAtMost(count - 1)
            return sorted[idx]
        }

    val jitterMs: Float
        @Synchronized get() {
            if (count < 2) return 0f
            val slice = frameTimes.take(count)
            val avg = slice.average().toFloat()
            return slice.map { kotlin.math.abs(it - avg) }.average().toFloat()
        }

    fun reset() {
        synchronized(this) {
            frameTimes.fill(16.67f)
            head  = 0
            count = 0
        }
    }

    fun toSummary(): String = buildString {
        appendLine("FPS: ${String.format("%.1f", averageFps)} (min: ${String.format("%.0f", minFps)} max: ${String.format("%.0f", maxFps)})")
        appendLine("Frame avg: ${String.format("%.2f", averageFrameMs)} ms")
        appendLine("P99 frame: ${String.format("%.2f", p99FrameMs)} ms")
        appendLine("Jitter:    ${String.format("%.2f", jitterMs)} ms")
        appendLine("Total:     $totalFrames frames")
    }
}
