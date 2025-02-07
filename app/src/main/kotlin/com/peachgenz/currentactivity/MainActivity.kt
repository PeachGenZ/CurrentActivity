package com.peachgenz.currentactivity

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Process.myUid
import androidx.appcompat.app.AppCompatActivity
import com.peachgenz.currentactivity.databinding.ActivityMainBinding
import com.peachgenz.currentactivity.service.WatchingService
import com.peachgenz.currentactivity.utility.PermissionHelper

class MainActivity : AppCompatActivity() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var floatingWindow: FloatingWindow? = null

        fun windowChange(context: Context, name: String) {
            if (null == floatingWindow) {
                floatingWindow = FloatingWindow(context)
            }
            floatingWindow?.onWindowChange(name)
        }
    }

    private val viewbinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val serviceIntent by lazy {
        Intent(this@MainActivity, WatchingService::class.java)
    }
    private var permissionHelper = PermissionHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewbinding.root)
        initView()
    }

    override fun onResume() {
        super.onResume()
        if (permissionHelper.isCanDrawOverlays(this)) {
            getTopActivity()
            return
        }

        permissionHelper.showOverlayPermissionDialog(this)
    }

    private fun initView() {
        viewbinding.swFloat.setOnCheckedChangeListener { _, isChecked ->
            when (isChecked) {
                true -> {
                    floatingWindow?.show()
                    startWatchingService()
                }

                false -> {
                    floatingWindow?.hide()
                    stopWatchingService()
                }
            }
        }
    }

    private fun getTopActivity() = useUsageStateToGet()

    private fun useUsageStateToGet() {
        if (isUsageStatsPermissionEnabled()) {
            viewbinding.swFloat.isChecked = true
        } else {
            permissionHelper.showUsageAccessPermissionDialog(this)
        }
    }

    private fun isUsageStatsPermissionEnabled(): Boolean {
        getSystemService(Context.APP_OPS_SERVICE)?.let {
            val appOps = it as AppOpsManager
            val mode = appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                myUid(),
                packageName
            )

            return mode == AppOpsManager.MODE_ALLOWED
        }
        return false
    }

    private fun startWatchingService() = startService(serviceIntent)
    private fun stopWatchingService() = stopService(serviceIntent)
}
