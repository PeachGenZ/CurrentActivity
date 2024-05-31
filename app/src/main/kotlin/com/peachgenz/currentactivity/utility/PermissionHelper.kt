package com.peachgenz.currentactivity.utility

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityManager
import androidx.appcompat.app.AlertDialog
import com.peachgenz.currentactivity.R

class PermissionHelper {
    fun isCanDrawOverlays(context: Context): Boolean = Settings.canDrawOverlays(context)

    fun isAccessibilityEnabled(activity: Activity): Boolean {
        val am = activity.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        return am.isEnabled.also {
            Log.d(
                "MainActivity",
                "AccessibilityService服务是否已经启动：$it"
            )
        }
    }

    fun showUsageAccessPermissionDialog(activity: Activity) {
        AlertDialog.Builder(activity)
            .setMessage(activity.getString(R.string.require_usage_access_permission_dialog))
            .setPositiveButton(activity.getString(R.string.button_go_to_setting)) { dialogInterface, _ ->
                activity.startActivity(
                    Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                        // data = Uri.parse("package:" + this@MainActivity.packageName)
                    }
                )
                dialogInterface.dismiss()
            }
            .setNegativeButton(activity.getString(R.string.button_cancel)) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()
            .show()
    }

    fun showAccessibilityPermissionDialog(activity: Activity) {
        AlertDialog.Builder(activity)
            .setMessage("需要开启无障碍服务")
            .setPositiveButton("去设置") { dialogInterface, _ ->
                activity.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                dialogInterface.dismiss()
            }
            .setNegativeButton("取消") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()
            .show()
    }

    fun showOverlayPermissionDialog(activity: Activity) {
        AlertDialog.Builder(activity)
            .setMessage(activity.getString(R.string.require_app_overlay_permission_dialog))
            .setPositiveButton(activity.getString(R.string.button_go_to_setting)) { dialogInterface, _ ->
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).run {
                    data = Uri.parse("package:" + activity.packageName)
                    activity.startActivity(this)
                }
                dialogInterface.dismiss()
            }
            .setNegativeButton(activity.getString(R.string.button_cancel)) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()
            .show()
    }
}