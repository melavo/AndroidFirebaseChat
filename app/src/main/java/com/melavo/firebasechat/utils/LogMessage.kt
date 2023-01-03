package com.melavo.firebasechat.utils

import android.util.Log
import com.melavo.firebasechat.BuildConfig.DEBUG

object LogMessage {

    private val logVisible = DEBUG

    internal fun v(msg: String) {
        if (logVisible) Log.v("firebasechat",msg)
    }

    internal fun e(msg: String) {
        if (logVisible) Log.e("firebasechat",msg)
    }

}