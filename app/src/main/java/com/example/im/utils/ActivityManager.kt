package com.example.im.utils

import android.app.Activity
import com.example.im.controller.activity.ChatActivity

/**
 * 全局activity管理类
 */
object ActivityManager {
    private val activities = ArrayList<Activity>()
    fun addActivity(activity: Activity) {
        activities.add(activity)
    }

    fun removeActivity(activity: Activity) {
        activities.remove(activity)
    }

    fun finishAll() {
        for (activity in activities) {
            if (!activity.isFinishing) {
                activity.finish()
            }
        }
        activities.clear()
    }

    fun isChatActivity(): Boolean {
        return if (activities.isEmpty()) false else (activities[activities.size - 1] is ChatActivity)
    }
}