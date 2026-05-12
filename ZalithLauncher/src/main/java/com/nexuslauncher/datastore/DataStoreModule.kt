package com.nexuslauncher.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.nexusDataStore: DataStore<Preferences> by preferencesDataStore(name = "nexus_prefs")
