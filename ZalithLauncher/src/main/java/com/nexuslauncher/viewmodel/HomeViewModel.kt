package com.nexuslauncher.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.nexuslauncher.datastore.NexusDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val dataStore: NexusDataStore) : ViewModel() {
    val lastInstance = dataStore.getLastInstance()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    fun updateLastInstance(id: String) = viewModelScope.launch { dataStore.setLastInstance(id) }

    companion object {
        fun factory(ds: NexusDataStore) = viewModelFactory { initializer { HomeViewModel(ds) } }
    }
}
