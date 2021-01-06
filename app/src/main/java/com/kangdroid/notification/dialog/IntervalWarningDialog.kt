package com.kangdroid.notification.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.kangdroid.notification.R

class IntervalWarningDialog(var mLess: Boolean) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val mDialogBuilder = AlertDialog.Builder(it)
            with (mDialogBuilder) {
                if (mLess) {
                    setMessage(R.string.time_less_warning)
                } else {
                    setMessage(R.string.time_too_much_warning)
                }
                setPositiveButton(R.string.dialog_ok) { _, _ ->
                    // do something
                    dismiss()
                }
                create()
            }
        } ?: throw IllegalArgumentException("Activity Null")
    }
}