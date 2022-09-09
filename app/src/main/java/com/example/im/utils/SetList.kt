package com.example.im.utils

import android.util.Log
import com.example.im.controller.activity.ChatActivity
import com.example.im.model.bean.Conversation
import com.example.im.model.bean.Message
import java.util.*

/**
 * 自定义有序不重复（userId不重复）集合
 * 仅用于存储Conversation类对象
 */
class SetList : LinkedList<Conversation>() {
    override fun add(element: Conversation): Boolean {
        return if (size == 0) {
            super.add(element)
        } else {
            val index = this.indexOf(element)
            if (index != -1) {
                this[index] = element
                true
            } else {
                this.add(0, element)
                true
            }
        }
    }

    fun add(message: Message): Int {
        return if (size == 0) {
            val conversation = Conversation(message).apply {
                if (!(ActivityManager.isChatActivity() && ChatActivity.userId == message.userId)) {
                    count = 1
                }
            }
            super.add(conversation)
            size - 1
        } else {
            val obj = Conversation(message)
            val index = this.indexOf(obj)
            if (index != -1) {
                val temp = this.removeAt(index)
                temp.setValue(message)
                //处于该会话聊天界面时不计数
                if (!(ActivityManager.isChatActivity() && ChatActivity.userId == temp.userId)) {
                    temp.count = temp.count + 1
                }
                this.add(0, temp)
                0
            } else {
                if (!(ActivityManager.isChatActivity() && ChatActivity.userId == message.userId)) {
                    obj.count = 1
                }
                add(0, obj)
                0
            }
        }
    }

//    fun remove(userId: String): Int {
//        val index = this.indexOf(Conversation(userId))
//        removeAt(index)
//        return if (index != -1) {
//            removeAt(index)
//            index
//        } else 0
//    }
}
