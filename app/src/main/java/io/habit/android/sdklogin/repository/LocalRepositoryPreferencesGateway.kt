package io.habit.android.sdklogin.repository

import android.content.Context
import androidx.preference.PreferenceManager

class LocalRepositoryPreferencesGateway(context: Context) : LocalRepository {

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    override var auth: String?
        get() = preferences.getString("authorization", null)
        set(value) {
            preferences.edit().apply {
                putString("authorization", value)
                commit()
            }
        }

}