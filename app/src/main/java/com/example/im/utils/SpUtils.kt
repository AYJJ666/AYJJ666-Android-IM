package com.example.im.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * sp储存类
 */
object SpUtils {
    private lateinit var mSp: SharedPreferences

    const val IS_NEW_INVITE = "isNewInvite"

    fun init(context: Context) {
        mSp = context.getSharedPreferences("im", Context.MODE_PRIVATE)!!
    }

    fun save(key: String, value: Any) {
        when (value) {
            is String -> mSp.edit().putString(key, value).apply()
            is Boolean -> mSp.edit().putBoolean(key, value).apply()
            is Int -> mSp.edit().putInt(key, value).apply()
        }
    }

    fun getString(key: String, defValue: String) = mSp.getString(key, defValue)
    fun getBoolean(key: String, defValue: Boolean) = mSp.getBoolean(key, defValue)
    fun getInt(key: String, defValue: Int) = mSp.getInt(key, defValue)
}