package de.bixilon.minosoft.gui.qmcl.crash

import android.util.Log

object QmclCrashReport {
    private const val TAG = "QMCL_Crash"
    
    fun report(error: Throwable) {
        Log.e(TAG, "Crash reported", error)
        CrashReportState.crashed = true
        CrashReportState.lastError = error
    }
}
