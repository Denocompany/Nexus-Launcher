package com.nexuslauncher.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.nexuslauncher.datastore.NexusDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SolarViewModel(private val dataStore: NexusDataStore) : ViewModel() {
    val lastPlanet = dataStore.getLastPlanet()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    fun updateLastPlanet(id: String) = viewModelScope.launch { dataStore.setLastPlanet(id) }

    companion object {
        fun factory(ds: NexusDataStore) = viewModelFactory { initializer { SolarViewModel(ds) } }
    }
}
