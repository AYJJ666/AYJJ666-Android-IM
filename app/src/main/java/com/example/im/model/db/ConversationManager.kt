package com.example.im.model.db

import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.im.IMApplication.Companion.mContext
import com.example.im.model.Model
import com.example.im.model.bean.Conversation
import com.example.im.model.bean.Message
import com.example.im.utils.Constant
import com.example.im.utils.SetList
import java.text.FieldPosition
import java.time.LocalTime
import java.util.*

object ConversationManager {
    private var conversations = SetList()
    private val conversationDao = AppDatabase.getDatabase(mContext).conversationDao()
    private val mLBM = LocalBroadcastManager.getInstance(mContext)
    private const val BMKey = "index"

    const val TAG = "ConversationManager"

    init {
        //从数据库读取会话列表
        conversations.addAll(conversationDao.allConversation())
        Log.d(TAG, "从数据库读取会话列表:$conversations")
    }

    fun addMessage(message: Message) {
        //保存
        val index = conversations.add(message)
        conversationDao.insertConversation(conversations[index])
        //广播
        mLBM.sendBroadcast(Intent(Constant.CONVERSATION_CHANGED).apply {
            putExtra(
                BMKey,
                index
            )
        })
    }

    fun setMessageCount(index: Int, count: Int) {
        Model.getGlobalThreadPool().execute {
            conversationDao.updateCount(conversations[index].userId, count)
            conversations[index].count = count
        }
    }

    fun getConversations(): SetList {
        return conversations
    }

    fun deleteConversation(position: Int) {
        Model.getGlobalThreadPool().execute {
            conversationDao.deleteById(conversations[position].userId)
            conversations.removeAt(position)
            mLBM.sendBroadcast(Intent(Constant.CONVERSATION_CHANGED).apply {
                putExtra("index", position)
            })
        }

    }

    fun deleteConversation(userId: String) {
        var index: Int = -1
        for (i in 0 until conversations.size) {
            if (conversations[i].userId == userId) {
                index = i
                break
            }
        }
        if (index != -1) {
            deleteConversation(index)
        }
    }

    fun clearByUserId(userId: String) {
        for (i in 0 until conversations.size) {
            if (conversations[i].userId == userId) {
                conversations[i].run {
                    message = ""
                    count = 0
                    time = Date().time
                }
                mLBM.sendBroadcast(Intent(Constant.CONVERSATION_CHANGED).apply {
                    putExtra("index", i)
                })
                break
            }
        }
    }
}