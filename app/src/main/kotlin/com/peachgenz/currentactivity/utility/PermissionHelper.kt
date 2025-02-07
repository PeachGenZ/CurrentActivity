package com.peachgenz.currentactivity.utility

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import com.peachgenz.currentactivity.R

class PermissionHelper {
    fun isCanDrawOverlays(context: Context): Boolean = Settings.canDrawOverlays(context)

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