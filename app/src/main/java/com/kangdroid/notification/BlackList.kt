package com.kangdroid.notification

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.os.Bundle
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.util.Log
import androidx.preference.*
import com.kangdroid.notification.settings.Settings

class BlackList: PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {
    var mSwitchMap: HashMap<SwitchPreference, String> = HashMap()
    lateinit var mProgressDialog: ProgressDialog
    lateinit var mScreen: PreferenceScreen
    lateinit var mSharedPreference: SharedPreferences
    lateinit var mThisWrapper: BlackList

    inner class ShowProgress: AsyncTask<Void, Void, Unit>() {

        override fun onPreExecute() {
            mProgressDialog.show()
        }

        @SuppressLint("WrongConstant")
        override fun doInBackground(vararg params: Void?) {
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
                mSwitch.onPreferenceChangeListener = mThisWrapper
                mScreen.addPreference(mSwitch)
            }

            // Set PreferenceScreens
            preferenceScreen = mScreen
            return
        }

        override fun onPostExecute(result: Unit?) {
            mProgressDialog.dismiss()
        }
    }

    @SuppressLint("WrongConstant")
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        mProgressDialog = ProgressDialog(activity)
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        mProgressDialog.setMessage(getString(R.string.getting_app_list))

        // Shared Preference for getting values
        mSharedPreference = PreferenceManager.getDefaultSharedPreferences(activity)
        mScreen = preferenceManager.createPreferenceScreen(activity)
        mThisWrapper = this

        ShowProgress().execute()
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