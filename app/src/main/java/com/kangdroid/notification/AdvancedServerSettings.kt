package com.kangdroid.notification

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class AdvancedServerSettings: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.advanced_server_preference, rootKey)
    }
}