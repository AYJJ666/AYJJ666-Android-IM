package com.example.im.controller.activity

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.im.utils.ActivityManager

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("BaseActivity:onCreate", javaClass.simpleName)
        ActivityManager.addActivity(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("BaseActivity:onDestroy", javaClass.simpleName)
        ActivityManager.removeActivity(this)
    }
}