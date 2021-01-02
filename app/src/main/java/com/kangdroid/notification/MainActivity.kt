package com.kangdroid.notification

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat


class MainActivity : AppCompatActivity(), PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isNotificationGranted()) {
            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            showWarningDialog(intent)
        }
        setContentView(R.layout.activity_main)

        var fragmentManager = supportFragmentManager

        fragmentManager
                .beginTransaction()
                .replace(R.id.main_pref, MainPreferenceFragment())
                .commit()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun isNotificationGranted(): Boolean {
        val mNotifSets = NotificationManagerCompat.getEnabledListenerPackages(this)
        return mNotifSets.contains(packageName)
    }

    fun showWarningDialog(intent: Intent) {
        var innerDialog: AlertDialog.Builder = AlertDialog.Builder(this)
        with(innerDialog) {
            setTitle("Notice")
            setMessage("Notification access permission is not granted yet.\nPress ok to open settings, press no to quit this application.")
            setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                startActivity(intent)
            })
            setNegativeButton("No") { dialog, which ->
                finishAffinity()
            }
            show()
        }
    }

    // For managing Back Stack
    override fun onSupportNavigateUp(): Boolean {
        if (supportFragmentManager.backStackEntryCount == 0) {
            finishAffinity()
        } else {
            supportFragmentManager.popBackStack()
        }
        return super.onSupportNavigateUp()
    }

    // For changing Preference Fragment
    override fun onPreferenceStartFragment(caller: PreferenceFragmentCompat?, pref: Preference?): Boolean {
        val mArgs: Bundle? = pref?.extras
        val mFragment: Fragment = supportFragmentManager.fragmentFactory.instantiate(
                classLoader,
                pref!!.fragment
        )
        mFragment.arguments = mArgs
        mFragment.setTargetFragment(caller , 0)

        supportFragmentManager.beginTransaction()
                .replace(R.id.main_pref, mFragment)
                .addToBackStack(null)
                .commit();
        return true
    }
}