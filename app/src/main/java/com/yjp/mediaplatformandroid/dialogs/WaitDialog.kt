package com.yjp.mediaplatformandroid.dialogs

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity


class WaitDialog : DialogFragment() {

    companion object {

        private val DEFAULT_TEXT = "请稍候"
        private val MESSAGE_PARAM = "message_param"
        private var sWaitDialog: DialogFragment? = null

        @Synchronized
        fun showWaitDialog(activity: AppCompatActivity, message: String) {
            sWaitDialog = sWaitDialog?:WaitDialog.newInstance(message)
            sWaitDialog!!.isCancelable = false
            sWaitDialog!!.show(activity.supportFragmentManager, null)
        }

        @Synchronized
        fun dismissWaitDialog() {
            sWaitDialog?.dismiss()
            sWaitDialog = null
        }

        fun newInstance(message: String): DialogFragment {
            val fragment = WaitDialog()
            val args = Bundle()
            args.putString(MESSAGE_PARAM, message)
            fragment.arguments = args

            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val message = arguments?.getString(MESSAGE_PARAM)

        val dlg = ProgressDialog(context)
        dlg.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        dlg.setMessage(message?: DEFAULT_TEXT)

        return dlg
    }

}
