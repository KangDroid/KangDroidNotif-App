package com.kangdroid.notification

import android.os.Bundle
import android.util.Log
import android.widget.Switch
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference
import com.kangdroid.notification.server.ServerManagement
import com.kangdroid.notification.settings.Settings

class MainPreferenceFragment : PreferenceFragmentCompat() {
    private val TAG_VAL: String = "MainPreference"
    private var mServerStatus: Preference? = null
    private val mServerManagement: ServerManagement = ServerManagement()
    private var mDisableCharging: SwitchPreference? = null
    private var mCheckServerManual: Preference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_preference, rootKey)

        // Shared Preference for getting values
        val mSharedPreference = PreferenceManager.getDefaultSharedPreferences(activity)

        val mPreferenceChangeListener: (preference: Preference, newValue: Any) -> Boolean = {preference: Preference, newValue: Any ->
            if (preference.key == "disable_charging_state") {
                Settings.Companion.mDisableChargingNotification = newValue as Boolean
            }
            true
        }

        // Preference
        mServerStatus = findPreference("server_status") as Preference?
        mServerStatus?.title = "Server Status: OFF"

        // Lambda function to update server status with "checkServerAlive()"
        val mUpdateServerStatus: (Any, Boolean) -> Unit = {
            mServerPane, mStatus ->
            if (mStatus) {
                (mServerPane as Preference).title = "Server Status: ON"
            } else {
                (mServerPane as Preference).title = "Server Status: OFF"
            }
        }
        mServerManagement.checkServerAlive(mUpdateServerStatus, mServerStatus)

        // Charging-Disable SwitchPreference
        mDisableCharging = findPreference("disable_charging_state") as SwitchPreference?
        Settings.Companion.mDisableChargingNotification = mSharedPreference.getBoolean("disable_charging_state", false)
        mDisableCharging?.setOnPreferenceChangeListener(mPreferenceChangeListener)

        // Manual Server Refresh
        mCheckServerManual = findPreference("server_reload") as Preference?
        mCheckServerManual?.setOnPreferenceClickListener {
            if (it.key == "server_reload") {
                mServerManagement.checkServerAlive(mUpdateServerStatus, mServerStatus)
            }
            true
        }
    }
}