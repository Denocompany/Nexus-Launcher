package com.nexuslauncher.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.nexuslauncher.datastore.NexusDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PerformanceViewModel(private val dataStore: NexusDataStore) : ViewModel() {
    val perfPreset   = dataStore.getPerfPreset()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 2)
    val boostEnabled = dataStore.getBoostEnabled()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)
    val targetFps    = dataStore.getTargetFps()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 60f)

    fun updatePreset(v: Int)      = viewModelScope.launch { dataStore.setPerfPreset(v) }
    fun updateBoost(v: Boolean)   = viewModelScope.launch { dataStore.setBoostEnabled(v) }
    fun updateTargetFps(v: Float) = viewModelScope.launch { dataStore.setTargetFps(v) }

    companion object {
        fun factory(ds: NexusDataStore) = viewModelFactory { initializer { PerformanceViewModel(ds) } }
    }
}
