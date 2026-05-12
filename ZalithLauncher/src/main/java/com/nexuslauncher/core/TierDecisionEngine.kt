package com.nexuslauncher.core

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * TierDecisionEngine — aplica otimizações automáticas baseadas no Tier.
 * Expõe o resultado atual via StateFlow para a UI consumir.
 */
class TierDecisionEngine(private val context: Context) {

    private val _tierResult = MutableStateFlow<TierResult?>(null)
    val tierResult: StateFlow<TierResult?> = _tierResult

    private val _appliedSettings = MutableStateFlow<AppliedSettings?>(null)
    val appliedSettings: StateFlow<AppliedSettings?> = _appliedSettings

    /** Classifica e aplica as configurações do Tier atual. */
    suspend fun evaluateAndApply(): TierResult {
        val result = TierClassifier.classify(context)
        _tierResult.value = result
        val settings = applyPreset(result)
        _appliedSettings.value = settings
        return result
    }

    /** Re-avalia sem alterar as configurações (ex.: após thermal event). */
    suspend fun reevaluate(): TierResult {
        val result = TierClassifier.classify(context)
        _tierResult.value = result
        return result
    }

    private fun applyPreset(result: TierResult): AppliedSettings {
        val preset = TierProfile.presetFor(result.tier)
        return AppliedSettings(
            tier             = result.tier,
            fps              = preset.fps,
            resolution       = preset.resolution,
            renderer         = preset.renderer.name,
            shadersEnabled   = preset.shadersEnabled,
            particleDensity  = preset.particleDensity,
            effectsLevel     = preset.effectsLevel.name,
            renderDistance   = preset.renderDistance,
            bloomEnabled     = preset.bloomEnabled,
            recommendations  = TierProfile.recommendations(result)
        )
    }

    fun forceOverride(fps: Int? = null, resolution: Float? = null) {
        val current = _appliedSettings.value ?: return
        _appliedSettings.value = current.copy(
            fps        = fps        ?: current.fps,
            resolution = resolution ?: current.resolution
        )
    }
}

data class AppliedSettings(
    val tier: NexusTier,
    val fps: Int,
    val resolution: Float,
    val renderer: String,
    val shadersEnabled: Boolean,
    val particleDensity: Int,
    val effectsLevel: String,
    val renderDistance: Int,
    val bloomEnabled: Boolean,
    val recommendations: List<String>
)
