package com.example.im.model.listener

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.im.controller.fragment.ChatFragment
import com.example.im.model.bean.InvitationInfo
import com.example.im.model.bean.Message
import com.example.im.model.db.ContactManager
import com.example.im.model.db.InviteManager
import com.example.im.model.db.MessageManager
import com.example.im.utils.Constant
import com.example.im.utils.SpUtils
import com.hyphenate.EMContactListener
import com.hyphenate.EMMessageListener
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMTextMessageBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


/**
 * 全局事件监听类
 */
class EventListener(context: Context) {
    //广播管理者对象
    private val mLBM = LocalBroadcastManager.getInstance(context)

    init {
        setContactListener()
        setMessageListener()
    }

    private fun setMessageListener() {
        EMClient.getInstance().chatManager().addMessageListener {
            for (i in it) {
                //todo 根据类型处理
                when (i.body) {
                    is EMTextMessageBody -> {
                        val body = i.body as EMTextMessageBody
                        Log.d("新消息事件", i.msgTime.toString())
                        val message =
                            Message(
                                i.from,
                                i.to,
                                body.message,
                                Message.TYPE_RECEIVE,
                                Message.TEXT,
                                i.msgTime
                            )
                        //保存
                        MessageManager.saveMessage(message)
                    }
                }
            }
        }
    }

    private fun setContactListener() {
        //注册一个联系人变化的监听 todo （29）
        EMClient.getInstance().contactManager().setContactListener(object : EMContactListener {
            override fun onContactAdded(username: String?) {
                if (username != null && username != "") {
                    //数据更新（保存到本地）
                    CoroutineScope(Job()).launch {
                        ContactManager.addContact(username)
                    }
                }
            }

            override fun onContactDeleted(username: String) {
                //删除本地联系人
                ContactManager.deleteContact(username)
            }

            //接收到好友邀请
            override fun onContactInvited(username: String, reason: String) {
                //更新本地数据
                InviteManager.saveInvitation(
                    username,
                    reason,
                    InvitationInfo.InvitationStatus.NEW_INVITE
                )
                //红点标记
                SpUtils.save(SpUtils.IS_NEW_INVITE, true)
                //发送广播
                mLBM.sendBroadcast(Intent(Constant.CONTACT_INVITE_CHANGED))
            }

            override fun onFriendRequestAccepted(username: String?) {
                //todo
                //更新本地数据
                //红点标记
                //发送广播
            }

            override fun onFriendRequestDeclined(username: String?) {
                //todo
                //更新本地数据
                //红点标记
                //发送广播
            }
        })
    }


}