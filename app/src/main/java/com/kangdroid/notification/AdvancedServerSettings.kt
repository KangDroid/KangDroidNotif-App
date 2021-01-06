package com.kangdroid.notification

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.preference.*
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
        mServerAutoChecking = findPreference(mSharedViewModel.KEY_SERVER_AUTOCHECKING) as? SwitchPreference
            ?: throw PreferenceNullException()
        mServerAutoChecking.onPreferenceChangeListener = this

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
            }
        }
        return true
    }

    fun updateServerStatusUI(mServerRetStatus: Boolean) {
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