package com.example.im.model.db

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.im.IMApplication.Companion.mContext
import com.example.im.model.Model
import com.example.im.model.bean.Message
import com.example.im.model.dao.MessageDao
import com.example.im.utils.ActivityManager
import com.example.im.utils.Constant
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage

object MessageManager {
    private val messageDao = AppDatabase.getDatabase(mContext).messageDao()
    private val myId = EMClient.getInstance().currentUser

    private val mLBM = LocalBroadcastManager.getInstance(mContext)

    private val cache = ArrayList<Message>()
    private var cacheUserId = ""

    fun deleteHistory(userId: String){
        Model.getGlobalThreadPool().execute {
            messageDao.deleteByUserId(userId)
            cache.clear()
            ConversationManager.clearByUserId(userId)
            mLBM.sendBroadcast(Intent(userId))
        }
    }

    fun sendMessage(content: String, userId: String) {
        //发送到环信服务器
        val msg = EMMessage.createTextSendMessage(content, userId)
        msg.chatType = EMMessage.ChatType.Chat
        EMClient.getInstance().chatManager().sendMessage(msg)
        //保存到本地
        val message = Message(myId, userId, content, Message.TYPE_SENT, Message.TEXT)
        saveMessage(message)
    }

    fun sendMessage(message: Message) {
        //发送到环信服务器
        val msg = EMMessage.createTextSendMessage(message.content, message.userId)
        msg.chatType = EMMessage.ChatType.Chat
        EMClient.getInstance().chatManager().sendMessage(msg)
        //保存到本地
        saveMessage(message)
    }

    fun saveMessage(message: Message) {
//        list.add(message)
        messageDao.insertMessage(message)
        //添加缓存
        cache.add(message)
        //更新会话
        ConversationManager.addMessage(message)
        //发送广播
        mLBM.sendBroadcast(Intent(Constant.NEW_MESSAGE))
        if (ActivityManager.isChatActivity()) {
            mLBM.sendBroadcast(Intent(message.userId))
        }
    }

    fun getMessagesByUserId(userId: String): List<Message> {
        if (cacheUserId != userId) {
            cacheUserId = userId
            cache.clear()
            cache.addAll(
                messageDao.getMessagesByUserId(userId)
            )
        }
        return cache
    }

    fun clear() {
        messageDao.deleteAll()
    }
}