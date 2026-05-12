package com.nexuslauncher.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.nexuslauncher.datastore.NexusDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VisualViewModel(private val dataStore: NexusDataStore) : ViewModel() {
    val visualQuality   = dataStore.getVisualQuality().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 2)
    val visualHdr       = dataStore.getVisualHdr().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)
    val visualTheme     = dataStore.getVisualTheme().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
    val visualAccent    = dataStore.getVisualColorAccent().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
    val visualShaders   = dataStore.getVisualShaders().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)
    val visualShaderIdx = dataStore.getVisualShaderIdx().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
    val brightness      = dataStore.getVisualBrightness().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.7f)
    val saturation      = dataStore.getVisualSaturation().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.5f)
    val contrast        = dataStore.getVisualContrast().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.5f)

    fun updateQuality(v: Int)       = viewModelScope.launch { dataStore.setVisualQuality(v) }
    fun updateHdr(v: Boolean)       = viewModelScope.launch { dataStore.setVisualHdr(v) }
    fun updateTheme(v: Int)         = viewModelScope.launch { dataStore.setVisualTheme(v) }
    fun updateAccent(v: Int)        = viewModelScope.launch { dataStore.setVisualColorAccent(v) }
    fun updateShaders(v: Boolean)   = viewModelScope.launch { dataStore.setVisualShaders(v) }
    fun updateShaderIdx(v: Int)     = viewModelScope.launch { dataStore.setVisualShaderIdx(v) }
    fun updateBrightness(v: Float)  = viewModelScope.launch { dataStore.setVisualBrightness(v) }
    fun updateSaturation(v: Float)  = viewModelScope.launch { dataStore.setVisualSaturation(v) }
    fun updateContrast(v: Float)    = viewModelScope.launch { dataStore.setVisualContrast(v) }

    companion object {
        fun factory(ds: NexusDataStore) = viewModelFactory { initializer { VisualViewModel(ds) } }
    }
}
