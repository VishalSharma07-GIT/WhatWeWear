package com.vishalsharma.whatwewear.data.preferences

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "what_we_wear_preferences"

val Context.dataStore by preferencesDataStore( // creats one datastore of the entire app
    name = DATASTORE_NAME
)
class OnboardingPreferences(
    private val context: Context


){
    suspend fun saveOnboardingCompleted() {

        context.dataStore.edit { preferences ->

            preferences[PreferenceKeys.HAS_COMPLETED_ONBOARDING] = true

        }

    }
    val hasCompletedOnboarding: Flow<Boolean> =
        context.dataStore.data.map { preferences ->

            preferences[PreferenceKeys.HAS_COMPLETED_ONBOARDING] ?: false

        }
}
