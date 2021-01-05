package com.kangdroid.notification.exception

class PreferenceNullException : Exception("""
        PreferenceNullException occurred, should be developer's error rather than user's error.
        Contact Developer with Logcat!
        """)