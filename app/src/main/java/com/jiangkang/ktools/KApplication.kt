package com.jiangkang.ktools

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Debug
import android.os.StrictMode
import androidx.core.util.LogWriter
import androidx.multidex.MultiDex
import com.facebook.drawee.backends.pipeline.Fresco
import com.github.anrwatchdog.ANRWatchDog
import com.jiangkang.tools.King
import com.jiangkang.tools.utils.ToastUtils
import com.squareup.leakcanary.LeakCanary
import java.lang.StringBuilder

/**
 * @author jiangkang
 * @date 2017/9/6
 */
open class KApplication : Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)

        MultiDex.install(this)

        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            val stackTrace = e.stackTrace
            val reason = StringBuilder()
            reason.appendln(e.message)
            stackTrace.forEach {
                reason.appendln(it.toString())
            }
            val intent = Intent(applicationContext,CrashInfoActivity::class.java)
                    .apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        putExtra("crash_info",reason.toString())
                    }
            startActivity(intent)
        }
    }

    override fun onCreate() {
        Debug.startMethodTracing()
        super.onCreate()

        enableStrictMode()

        initLeakCanary()

        King.init(this)

        initANRWatchDog()

        Debug.stopMethodTracing()


        Fresco.initialize(this)
    }


    private fun initANRWatchDog() {
        ANRWatchDog().start()
    }

    private fun initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)
    }


    private fun enableStrictMode() {
        if (BuildConfig.DEBUG) {
            enableThreadPolicy()
            enableVmPolicy()
        }
    }

    private fun enableVmPolicy() {
        StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                        .detectAll()
                        .penaltyLog()
                        .build()
        )
    }

    private fun enableThreadPolicy() {
        StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                        .detectAll()
                        .penaltyLog()
                        .build()
        )
    }


}
