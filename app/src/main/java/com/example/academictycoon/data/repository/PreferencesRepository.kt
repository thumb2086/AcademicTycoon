package com.example.academictycoon.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class PreferencesRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private object PreferencesKeys {
        val BUNDLE_VERSION = intPreferencesKey("bundle_version")
    }

    val bundleVersion: Flow<Int> = context.dataStore.data.map {
        it[PreferencesKeys.BUNDLE_VERSION] ?: 0
    }

    suspend fun updateBundleVersion(version: Int) {
        context.dataStore.edit {
            it[PreferencesKeys.BUNDLE_VERSION] = version
        }
    }
}
