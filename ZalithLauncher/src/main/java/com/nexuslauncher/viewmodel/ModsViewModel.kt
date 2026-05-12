package com.nexuslauncher.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.nexuslauncher.datastore.NexusDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ModsViewModel(private val dataStore: NexusDataStore) : ViewModel() {
    val modsEnabled          = dataStore.getModsEnabled()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())
    val resourcePacksEnabled = dataStore.getResourcePacksEnabled()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    fun updateModsEnabled(v: Set<String>)          = viewModelScope.launch { dataStore.setModsEnabled(v) }
    fun updateResourcePacksEnabled(v: Set<String>) = viewModelScope.launch { dataStore.setResourcePacksEnabled(v) }

    companion object {
        fun factory(ds: NexusDataStore) = viewModelFactory { initializer { ModsViewModel(ds) } }
    }
}
