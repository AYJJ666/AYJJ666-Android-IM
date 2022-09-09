package com.example.im.utils

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import com.example.im.IMApplication

object Utils {

    //获取屏幕宽度
    fun getScreenWidth(): Int {
        // todo 这里有弃用的方法
        val windowManager =
            IMApplication.mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }
}