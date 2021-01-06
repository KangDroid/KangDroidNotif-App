package com.kangdroid.notification

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference
import com.kangdroid.notification.exception.PreferenceNullException
import com.kangdroid.notification.server.ServerManagement
import com.kangdroid.notification.settings.Settings
import com.kangdroid.notification.viewmodel.SharedViewModel
import kotlinx.coroutines.*

class MainPreferenceFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {
    // UI Constants
    private val KEY_SERVER_STATUS: String = "server_status"
    private val KEY_DISABLE_CHARGING: String = "disable_charging_state"

    // UI Variable
    private lateinit var mServerStatus: Preference
    private lateinit var mDisableCharging: SwitchPreference

    // Server Monitoring
    private lateinit var mServerMonitor: Job

    // View Model
    private val mSharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_preference, rootKey)

        // Shared Preference for getting values
        val mSharedPreference = PreferenceManager.getDefaultSharedPreferences(activity)

        // Preference
        mServerStatus =
            findPreference(KEY_SERVER_STATUS) as? Preference ?: throw PreferenceNullException()
        mServerStatus.title = getString(R.string.server_off)

        // Check for Server availability
        GlobalScope.launch(Dispatchers.IO) {
            val mSucceed = ServerManagement.checkServerAlive()

            withContext(Dispatchers.Main) {
                updateServerStatusUI(mSucceed)
                mSharedViewModel.mServerOn = mSucceed
            }
        }

        // Charging-Disable SwitchPreference
        mDisableCharging = findPreference(KEY_DISABLE_CHARGING) as? SwitchPreference
            ?: throw PreferenceNullException()
        Settings.mDisableChargingNotification =
            mSharedPreference.getBoolean(KEY_DISABLE_CHARGING, false)
        mDisableCharging.onPreferenceChangeListener = this

        // Do we need to update?
        mSharedViewModel.mAutoCheckingEnabled =
            mSharedPreference.getBoolean(mSharedViewModel.KEY_SERVER_AUTOCHECKING, false)
        mSharedViewModel.mAutoCheckingInterval = (mSharedPreference.getString(
            mSharedViewModel.KEY_SERVER_AUTOCHECKING_INTR,
            "2000"
        ))!!.toLong()
    }

    override fun onPause() {
        super.onPause()
        if (mSharedViewModel.mAutoCheckingEnabled) {
            mServerMonitor.cancel()
        }
    }

    override fun onResume() {
        super.onResume()
        // Server Status monitor
        if (mSharedViewModel.mAutoCheckingEnabled) {
            mServerMonitor = GlobalScope.launch(Dispatchers.IO) {
                while (true) {
                    val mSucceed = ServerManagement.checkServerAlive()

                    withContext(Dispatchers.Main) {
                        updateServerStatusUI(mSucceed)
                        mSharedViewModel.mServerOn = mSucceed
                    }
                    delay(mSharedViewModel.mAutoCheckingInterval)
                }
            }
        } else {
            updateServerStatusUI(mSharedViewModel.mServerOn)
        }
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        when (preference?.key) {
            // Charging Notification Update
            KEY_DISABLE_CHARGING -> {
                Settings.mDisableChargingNotification = newValue as Boolean
            }
        }
        return true
    }

    private fun updateServerStatusUI(mServerRetStatus: Boolean) {
        if (mServerRetStatus) {
            // Overall Server Connection State
            mServerStatus.title = getString(R.string.server_on)

        } else {
            // Overall Server Connection State
            mServerStatus.title = getString(R.string.server_off)
        }
    }
}