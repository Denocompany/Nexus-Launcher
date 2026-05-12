package com.nexuslauncher.ui.solar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nexuslauncher.core.AppliedSettings
import com.nexuslauncher.core.NexusBoostEngine
import com.nexuslauncher.core.NexusSystemMonitor
import com.nexuslauncher.core.NexusTier
import com.nexuslauncher.core.TierDecisionEngine
import com.nexuslauncher.core.TierResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * SolarSystemViewModel — Fonte de verdade para toda a UI do Sistema Solar.
 *
 * Expõe:
 *  - Tier atual e configurações aplicadas
 *  - Métricas em tempo real (FPS, CPU, GPU, RAM)
 *  - Estado do Nexus Boost
 *  - Planeta selecionado e lua selecionada
 */
class SolarSystemViewModel(application: Application) : AndroidViewModel(application) {

    private val ctx            = application.applicationContext
    private val decisionEngine = TierDecisionEngine(ctx)
    private val boostEngine    = NexusBoostEngine(ctx)
    private val monitor        = NexusSystemMonitor(ctx, intervalMs = 1000L)

    // ── Tier ──────────────────────────────────────────────────────────────
    val tierResult:     StateFlow<TierResult?>     = decisionEngine.tierResult
    val appliedSettings:StateFlow<AppliedSettings?> = decisionEngine.appliedSettings
    val boostReport:    StateFlow<NexusBoostEngine.BoostReport> = boostEngine.report
    val systemMetrics:  StateFlow<NexusSystemMonitor.SystemMetrics> = monitor.metrics

    // ── Navigation ────────────────────────────────────────────────────────
    private val _selectedPlanet = MutableStateFlow<String?>(null)
    val selectedPlanet: StateFlow<String?> = _selectedPlanet.asStateFlow()

    private val _selectedMoon = MutableStateFlow<String?>(null)
    val selectedMoon: StateFlow<String?> = _selectedMoon.asStateFlow()

    init {
        monitor.start()
        viewModelScope.launch { decisionEngine.evaluateAndApply() }
    }

    fun selectPlanet(id: String?) {
        _selectedPlanet.value = id
        _selectedMoon.value   = null
    }

    fun selectMoon(moon: String?) {
        _selectedMoon.value = moon
    }

    fun triggerBoost() {
        viewModelScope.launch {
            val tier = tierResult.value ?: return@launch
            boostEngine.boost(tier)
        }
    }

    fun resetBoost() = boostEngine.reset()

    fun reevaluateTier() {
        viewModelScope.launch { decisionEngine.reevaluate() }
    }

    /** Intensidade de brilho do planeta com base no Tier atual (0f..1f). */
    fun planetGlowIntensity(): Float = when (tierResult.value?.tier) {
        NexusTier.T1_ULTRA    -> 1.0f
        NexusTier.T2_ALTO     -> 0.8f
        NexusTier.T3_AVANCADO -> 0.6f
        NexusTier.T4_MEDIO    -> 0.4f
        NexusTier.T5_BAIXO    -> 0.2f
        null                  -> 0.5f
    }

    /** Fator de velocidade de pulsação com base no FPS atual. */
    fun planetPulseSpeed(): Float {
        val fps = systemMetrics.value.fpsCurrent
        return (fps / 60f).coerceIn(0.3f, 2.0f)
    }

    override fun onCleared() {
        super.onCleared()
        monitor.stop()
    }
}
