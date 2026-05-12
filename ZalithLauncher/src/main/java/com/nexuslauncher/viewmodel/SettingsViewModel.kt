package com.nexuslauncher.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.nexuslauncher.datastore.NexusDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val dataStore: NexusDataStore) : ViewModel() {
    val autoUpdate      = dataStore.getAutoUpdate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)
    val telemetry       = dataStore.getTelemetry().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)
    val experimental    = dataStore.getExperimental().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)
    val autoSave        = dataStore.getAutoSave().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)
    val crashReport     = dataStore.getCrashReport().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)
    val fullscreen      = dataStore.getFullscreen().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)
    val vsync           = dataStore.getVsync().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)
    val resolution      = dataStore.getResolution().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
    val touchSens       = dataStore.getTouchSens().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 1)
    val haptic          = dataStore.getHaptic().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)
    val gamepad         = dataStore.getGamepad().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)
    val bgLoad          = dataStore.getBgLoad().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)
    val language        = dataStore.getLanguage().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "Português (Brasil)")
    val settingsTheme   = dataStore.getSettingsTheme().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
    val nexusAI         = dataStore.getNexusAI().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)
    val predictiveBoost = dataStore.getPredictiveBoost().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)
    val gpuOverclock    = dataStore.getGpuOverclock().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)
    val beta            = dataStore.getBeta().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)
    val gamePath        = dataStore.getGamePath().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    fun setAutoUpdate(v: Boolean)      = viewModelScope.launch { dataStore.setAutoUpdate(v) }
    fun setTelemetry(v: Boolean)       = viewModelScope.launch { dataStore.setTelemetry(v) }
    fun setExperimental(v: Boolean)    = viewModelScope.launch { dataStore.setExperimental(v) }
    fun setAutoSave(v: Boolean)        = viewModelScope.launch { dataStore.setAutoSave(v) }
    fun setCrashReport(v: Boolean)     = viewModelScope.launch { dataStore.setCrashReport(v) }
    fun setFullscreen(v: Boolean)      = viewModelScope.launch { dataStore.setFullscreen(v) }
    fun setVsync(v: Boolean)           = viewModelScope.launch { dataStore.setVsync(v) }
    fun setResolution(v: Int)          = viewModelScope.launch { dataStore.setResolution(v) }
    fun setTouchSens(v: Int)           = viewModelScope.launch { dataStore.setTouchSens(v) }
    fun setHaptic(v: Boolean)          = viewModelScope.launch { dataStore.setHaptic(v) }
    fun setGamepad(v: Boolean)         = viewModelScope.launch { dataStore.setGamepad(v) }
    fun setBgLoad(v: Boolean)          = viewModelScope.launch { dataStore.setBgLoad(v) }
    fun setLanguage(v: String)         = viewModelScope.launch { dataStore.setLanguage(v) }
    fun setSettingsTheme(v: Int)       = viewModelScope.launch { dataStore.setSettingsTheme(v) }
    fun setNexusAI(v: Boolean)         = viewModelScope.launch { dataStore.setNexusAI(v) }
    fun setPredictiveBoost(v: Boolean) = viewModelScope.launch { dataStore.setPredictiveBoost(v) }
    fun setGpuOverclock(v: Boolean)    = viewModelScope.launch { dataStore.setGpuOverclock(v) }
    fun setBeta(v: Boolean)            = viewModelScope.launch { dataStore.setBeta(v) }
    fun setGamePath(v: String)         = viewModelScope.launch { dataStore.setGamePath(v) }

    companion object {
        fun factory(ds: NexusDataStore) = viewModelFactory { initializer { SettingsViewModel(ds) } }
    }
}
