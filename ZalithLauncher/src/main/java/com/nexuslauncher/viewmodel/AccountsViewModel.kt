package com.nexuslauncher.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.nexuslauncher.datastore.NexusDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AccountsViewModel(private val dataStore: NexusDataStore) : ViewModel() {
    val activeAccount   = dataStore.getActiveAccount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")
    val offlineAccounts = dataStore.getOfflineAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())
    val activeSkin      = dataStore.getActiveSkin()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    fun updateActiveAccount(id: String)        = viewModelScope.launch { dataStore.setActiveAccount(id) }
    fun updateOfflineAccounts(v: Set<String>)  = viewModelScope.launch { dataStore.setOfflineAccounts(v) }
    fun updateActiveSkin(id: String)           = viewModelScope.launch { dataStore.setActiveSkin(id) }

    companion object {
        fun factory(ds: NexusDataStore) = viewModelFactory { initializer { AccountsViewModel(ds) } }
    }
}
