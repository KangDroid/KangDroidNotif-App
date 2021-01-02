package com.kangdroid.notification

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.os.Bundle
import android.content.pm.PackageManager
import androidx.preference.*
import com.kangdroid.notification.settings.Settings

class BlackList: PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {
    var mSwitchMap: HashMap<SwitchPreference, String> = HashMap()

    @SuppressLint("WrongConstant")
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Shared Preference for getting values
        val mSharedPreference = PreferenceManager.getDefaultSharedPreferences(activity)

        val screen: PreferenceScreen = preferenceManager.createPreferenceScreen(activity)

        // UI Initiate Code starts here.

        val mPackageManager = activity?.packageManager
        val mPackages: List<PackageInfo> = mPackageManager?.getInstalledPackages(PackageManager.MATCH_ALL)!!
        for (i in mPackages.indices) {
            if (mPackages[i].applicationInfo.flags.and(ApplicationInfo.FLAG_SYSTEM) == 1) {
                continue
            }

            var mSwitch = SwitchPreference(activity)
            with (mSwitch) {
                title = mPackages[i].applicationInfo.loadLabel(mPackageManager)
                icon = mPackages[i].applicationInfo.loadIcon(mPackageManager)
                key = "switch_" + mPackages[i].packageName

                Settings.mHashBlackList[mPackages[i].packageName] = mSharedPreference.getBoolean(key, false)
                setDefaultValue(false)
            }

            mSwitchMap[mSwitch] = mPackages[i].packageName
            mSwitch.onPreferenceChangeListener = this
            screen.addPreference(mSwitch)
        }

        // Set PreferenceScreens
        preferenceScreen = screen
    }

    /**
     * Since this fragment only manages swtichpreference from app list, so we do not need to check.
     */
    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        val mPackageName: String = mSwitchMap[(preference as SwitchPreference)]!!
        Settings.Companion.mHashBlackList[mPackageName] = newValue as Boolean
        return true
    }
}