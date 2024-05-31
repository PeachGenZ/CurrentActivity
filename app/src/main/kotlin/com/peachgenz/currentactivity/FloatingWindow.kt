package com.peachgenz.currentactivity

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import com.peachgenz.currentactivity.databinding.LayoutFloatingWindowBinding

class FloatingWindow(private val mContext: Context) {

    private val mWindowManager: WindowManager by lazy {
        (mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
    }

    private val layoutInflater by lazy {
        LayoutInflater.from(mContext)
            .inflate(R.layout.layout_floating_window, null)
    }

    private val mBinding: LayoutFloatingWindowBinding by lazy {
        LayoutFloatingWindowBinding.inflate(LayoutInflater.from(mContext), layoutInflater as ViewGroup, false)
    }

    private val mParams by lazy {
        WindowManager.LayoutParams().apply {
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            format = PixelFormat.RGBA_8888
            flags =
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            gravity = Gravity.END or Gravity.TOP
            x = 0
            y = 0
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
        }
    }

    private var isShow = false

    fun onWindowChange(packageName: String, activityName: String) {
        mBinding.tvContent.text = packageName + "\n" + activityName
    }

    fun onWindowChange(name: String) {
        if (name.isEmpty()) {
            return
        }
        mBinding.tvContent.text = name
        if (!isShow) {
            show()
        }
    }

    fun show() {
        if (isShow) {
            return
        }
        mWindowManager.addView(mBinding.root, mParams)
        isShow = true
        callback?.windowHide(false)
    }

    fun hide() {
        if (!isShow) {
            return
        }
        mWindowManager.removeView(mBinding.root)
        isShow = false
        callback?.windowHide(true)
    }

    companion object {
        var callback: IFloatingWindowState? = null
    }
}
