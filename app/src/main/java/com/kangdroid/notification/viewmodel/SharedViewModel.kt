package com.kangdroid.notification.viewmodel

import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    var mServerOn: Boolean = false
    var mAutoCheckingEnabled: Boolean = false
    var mAutoCheckingInterval: Long = 2000
    val KEY_SERVER_AUTOCHECKING: String = "enable_auto_checking"
    val KEY_SERVER_AUTOCHECKING_INTR: String = "auto_checking_interval"
}