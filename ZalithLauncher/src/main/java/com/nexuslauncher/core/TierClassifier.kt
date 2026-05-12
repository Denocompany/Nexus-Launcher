package com.nexuslauncher.core

import android.app.ActivityManager
import android.content.Context
import android.opengl.EGL14
import android.opengl.GLES30
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Tier de desempenho do Nexus Launcher — Fase 2.
 * Classifica dispositivos em 5 níveis: Ultra, Alto, Avançado, Médio, Baixo.
 */
enum class NexusTier(val label: String, val level: Int) {
    T1_ULTRA    ("Ultra",    5),
    T2_ALTO     ("Alto",     4),
    T3_AVANCADO ("Avançado", 3),
    T4_MEDIO    ("Médio",    2),
    T5_BAIXO    ("Baixo",    1);

    val isHighEnd  get() = level >= 4
    val isMidRange get() = level == 3
    val isLowEnd   get() = level <= 2
}

data class TierResult(
    val tier: NexusTier,
    val reason: String,
    val ramGb: Float,
    val cpuCores: Int,
    val gpuRenderer: String,
    val hasVulkan: Boolean,
    val apiLevel: Int,
    val thermalOk: Boolean,
    val suggestedFps: Int,
    val suggestedResolution: Float,
    val suggestedRenderer: String,
    val shadersEnabled: Boolean,
    val particleDensity: Int
)

object TierClassifier {

    suspend fun classify(context: Context): TierResult = withContext(Dispatchers.IO) {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val info = ActivityManager.MemoryInfo().also { am.getMemoryInfo(it) }
        val ramGb   = info.totalMem / (1024f * 1024f * 1024f)
        val cores   = Runtime.getRuntime().availableProcessors()
        val api     = Build.VERSION.SDK_INT
        val gpu     = detectGpuRenderer()
        val vulkan  = api >= 24 && hasVulkanSupport()
        val thermal = !am.isLowRamDevice

        val score = computeScore(ramGb, cores, api, vulkan, thermal)

        val tier = when {
            score >= 90 -> NexusTier.T1_ULTRA
            score >= 70 -> NexusTier.T2_ALTO
            score >= 50 -> NexusTier.T3_AVANCADO
            score >= 30 -> NexusTier.T4_MEDIO
            else        -> NexusTier.T5_BAIXO
        }

        val preset = TierProfile.presetFor(tier)

        TierResult(
            tier              = tier,
            reason            = buildReason(ramGb, cores, api, vulkan, score),
            ramGb             = ramGb,
            cpuCores          = cores,
            gpuRenderer       = gpu,
            hasVulkan         = vulkan,
            apiLevel          = api,
            thermalOk         = thermal,
            suggestedFps      = preset.fps,
            suggestedResolution = preset.resolution,
            suggestedRenderer = if (vulkan) "Vulkan" else "OpenGL ES",
            shadersEnabled    = preset.shadersEnabled,
            particleDensity   = preset.particleDensity
        )
    }

    private fun computeScore(ramGb: Float, cores: Int, api: Int, vulkan: Boolean, thermal: Boolean): Int {
        var score = 0
        score += when {
            ramGb >= 8f -> 35
            ramGb >= 6f -> 28
            ramGb >= 4f -> 20
            ramGb >= 2f -> 10
            else        ->  4
        }
        score += when {
            cores >= 8 -> 30
            cores >= 6 -> 22
            cores >= 4 -> 15
            else       ->  8
        }
        score += when {
            api >= 30 -> 20
            api >= 26 -> 14
            api >= 24 -> 8
            else      ->  3
        }
        if (vulkan) score += 12
        if (!thermal) score -= 15
        return score.coerceIn(0, 100)
    }

    private fun detectGpuRenderer(): String = try {
        Build.HARDWARE.ifBlank { "Unknown GPU" }
    } catch (e: Exception) { "Unknown GPU" }

    private fun hasVulkanSupport(): Boolean = try {
        Build.VERSION.SDK_INT >= 24
    } catch (e: Exception) { false }

    private fun buildReason(ramGb: Float, cores: Int, api: Int, vulkan: Boolean, score: Int) =
        "Score=$score | RAM=${String.format("%.1f", ramGb)}GB | CPU=$cores cores | API=$api | Vulkan=$vulkan"
}
