package com.peachgenz.currentactivity

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Build
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

    private var permissionHelper = PermissionHelper()
    private var isUseAccessibilityService = false
    private var isManualHide = false

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
            when {
                isChecked && isUseAccessibilityService -> {
                    floatingWindow?.show()
                }

                isChecked -> startWatchingService()

                else -> floatingWindow?.hide()
            }

            isManualHide = !isChecked
        }
    }

    private fun getTopActivity() {
        if (isUseAccessibilityService) {
            useAccessibilityToGet()
        } else {
            useUsageStateToGet()
        }
    }

    private fun useAccessibilityToGet() {
        if (permissionHelper.isAccessibilityEnabled(this)) {
            if (!isManualHide) {
                viewbinding.swFloat.isChecked = true
            }
            return
        }

        permissionHelper.showAccessibilityPermissionDialog(this)
        return
    }

    private fun useUsageStateToGet() {
        if (isUsageStatsPermissionEnabled()) {
            if (!isManualHide) {
                viewbinding.swFloat.isChecked = true
            }
            return
        }

        permissionHelper.showUsageAccessPermissionDialog(this)
        return
    }

    private fun isUsageStatsPermissionEnabled(): Boolean {
        getSystemService(Context.APP_OPS_SERVICE)?.let {
            val appOps = it as AppOpsManager
            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appOps.unsafeCheckOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    myUid(),
                    packageName
                )
            } else {
                appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, myUid(), packageName)
            }

            return mode == AppOpsManager.MODE_ALLOWED
        }
        return false
    }

    private fun startWatchingService() {
        val intent = Intent(this@MainActivity, WatchingService::class.java)
        startService(intent)
    }
}
