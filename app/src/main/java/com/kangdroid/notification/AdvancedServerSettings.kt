package com.kangdroid.notification

import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.activityViewModels
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.kangdroid.notification.exception.PreferenceNullException
import com.kangdroid.notification.server.ServerManagement
import com.kangdroid.notification.viewmodel.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdvancedServerSettings : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {
    // UI Constants
    private val KEY_SERVER_RELOAD: String = "server_reload"
    private val KEY_SERVER_URLEDIT: String = "enter_server_url"
    private val KEY_SERVER_PORTEDIT: String = "enter_server_port"

    // UI Variable
    private lateinit var mCheckServerManual: Preference
    private lateinit var mServerURLEditor: EditTextPreference
    private lateinit var mServerPortEditor: EditTextPreference
    private lateinit var mServerAutoChecking: SwitchPreference
    private lateinit var mServerAutoInterval: EditTextPreference

    // View Model
    private val mSharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.advanced_server_preference, rootKey)

        // Manual Server Refresh
        mCheckServerManual =
            findPreference(KEY_SERVER_RELOAD) as? Preference ?: throw PreferenceNullException()
        mCheckServerManual.setOnPreferenceClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val mSucceed = ServerManagement.checkServerAlive()

                withContext(Dispatchers.Main) {
                    updateServerStatusUI(mSucceed)
                }
            }
            true
        }

        // Server URL
        mServerURLEditor = findPreference(KEY_SERVER_URLEDIT) as? EditTextPreference
            ?: throw PreferenceNullException()
        mServerURLEditor.text = ServerManagement.mServerBaseUrl
        mServerURLEditor.onPreferenceChangeListener = this

        // Server Port
        mServerPortEditor = findPreference(KEY_SERVER_PORTEDIT) as? EditTextPreference
            ?: throw PreferenceNullException()
        mServerPortEditor.text = ServerManagement.mServerPort
        mServerPortEditor.onPreferenceChangeListener = this

        // Server Auto Checking Switch
        mServerAutoChecking =
            findPreference(mSharedViewModel.KEY_SERVER_AUTOCHECKING) as? SwitchPreference
                ?: throw PreferenceNullException()
        mServerAutoChecking.onPreferenceChangeListener = this

        // Server Auto Checking Interval
        mServerAutoInterval =
            findPreference(mSharedViewModel.KEY_SERVER_AUTOCHECKING_INTR) as? EditTextPreference
                ?: throw PreferenceNullException()
        mServerAutoInterval.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER
        }
        mServerAutoInterval.summary = (mSharedViewModel.mAutoCheckingInterval).toString()
        mServerAutoInterval.isEnabled = mSharedViewModel.mAutoCheckingEnabled
        mServerAutoInterval.onPreferenceChangeListener = this

    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        when (preference?.key) {
            // URL Edit Update
            KEY_SERVER_URLEDIT -> {
                ServerManagement.mServerBaseUrl = newValue as String
                GlobalScope.launch(Dispatchers.IO) {
                    val mSucceed = ServerManagement.checkServerAlive()

                    withContext(Dispatchers.Main) {
                        updateServerStatusUI(mSucceed)
                        mSharedViewModel.mServerOn = mSucceed
                    }
                }
            }

            // Port Edit Update
            KEY_SERVER_PORTEDIT -> {
                ServerManagement.mServerPort = newValue as String
                GlobalScope.launch(Dispatchers.IO) {
                    val mSucceed = ServerManagement.checkServerAlive()

                    withContext(Dispatchers.Main) {
                        updateServerStatusUI(mSucceed)
                        mSharedViewModel.mServerOn = mSucceed
                    }
                }
            }

            // Server Auto-checking Update
            mSharedViewModel.KEY_SERVER_AUTOCHECKING -> {
                mSharedViewModel.mAutoCheckingEnabled = newValue as Boolean
                mServerAutoInterval.isEnabled = mSharedViewModel.mAutoCheckingEnabled
            }

            // Server Auto-Checking Interval
            mSharedViewModel.KEY_SERVER_AUTOCHECKING_INTR -> {
                when (val mInputRequest: Long = (newValue as String).toLong()) {
                    in Long.MIN_VALUE until 1000 -> {
                        // TODO: Show dialog that below 1000ms would cause significant battery drain.
                        return false
                    }
                    in 50000..Long.MAX_VALUE -> {
                        // TODO: Show Dialog that more than 50000ms is too-slow.
                        return false
                    }
                    in 1000 until 50000 -> {
                        mSharedViewModel.mAutoCheckingInterval = mInputRequest
                        mServerAutoInterval.summary = (mSharedViewModel.mAutoCheckingInterval).toString()
                    }
                    else -> {
                        return false
                    }
                }
            }
        }
        return true
    }

    private fun updateServerStatusUI(mServerRetStatus: Boolean) {
        val mErrorString = getString(R.string.server_connection_error)

        if (mServerRetStatus) {
            // Server URL Editor
            mServerURLEditor.text = ServerManagement.mServerBaseUrl
            mServerURLEditor.summary = ServerManagement.mServerBaseUrl

            // Server Port Editor
            mServerPortEditor.text = ServerManagement.mServerPort
            mServerPortEditor.summary = ServerManagement.mServerPort

        } else {
            // Server URL Editor
            mServerURLEditor.summary = mErrorString

            // Server Port Editor
            mServerPortEditor.summary = mErrorString
        }
    }
}