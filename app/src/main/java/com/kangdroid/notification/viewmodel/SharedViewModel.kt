package com.kangdroid.notification.viewmodel

import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    var mServerOn: Boolean = false
    var mAutoCheckingEnabled: Boolean = false
    val KEY_SERVER_AUTOCHECKING: String = "enable_auto_checking"
}