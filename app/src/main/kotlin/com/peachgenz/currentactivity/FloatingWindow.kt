package com.peachgenz.currentactivity

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.WindowManager
import com.peachgenz.currentactivity.databinding.LayoutFloatingWindowBinding


class FloatingWindow(private val mContext: Context) {

    private var xInitCord: Int = 0
    private var yInitCord: Int = 0
    private var xInitMargin: Int = 0
    private var yInitMargin: Int = 0

    private val mWindowManager: WindowManager by lazy {
        (mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
    }

    private val layoutInflater by lazy {
        LayoutInflater.from(mContext).inflate(R.layout.layout_floating_window, null)
    }

    private val mBinding: LayoutFloatingWindowBinding by lazy {
        LayoutFloatingWindowBinding.inflate(
            LayoutInflater.from(mContext),
            layoutInflater as ViewGroup,
            false
        )
    }

    private val initParams by lazy {
        WindowManager.LayoutParams().apply {
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            format = PixelFormat.TRANSLUCENT
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            gravity = Gravity.END or Gravity.TOP
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
        }
    }

    private val mParams by lazy {
        WindowManager.LayoutParams().apply {
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            format = PixelFormat.TRANSLUCENT
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            gravity = Gravity.NO_GRAVITY
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
        }
    }

    private var isShow = false

    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchAction() {
        mBinding.root.setOnTouchListener { view, motionEvent ->
            val xCord = motionEvent.rawX.toInt()
            val yCord = motionEvent.rawY.toInt()
            val xCordDestination: Int
            val yCordDestination: Int
            val action: Int = motionEvent.action

            if (action == MotionEvent.ACTION_DOWN) {
                xInitCord = xCord
                yInitCord = yCord
                xInitMargin = mParams.x
                yInitMargin = mParams.y
            } else if (action == MotionEvent.ACTION_MOVE) {
                val xDiffMove: Int = xCord - xInitCord
                val yDiffMove: Int = yCord - yInitCord
                xCordDestination = xInitMargin + xDiffMove
                yCordDestination = yInitMargin + yDiffMove

                mParams.x = xCordDestination
                mParams.y = yCordDestination
                mWindowManager.updateViewLayout(view, mParams)
            }
            true
        }
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
        mWindowManager.addView(mBinding.root, initParams)
        setupTouchAction()
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
