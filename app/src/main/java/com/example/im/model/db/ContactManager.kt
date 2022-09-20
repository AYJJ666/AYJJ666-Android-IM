package com.example.im.model.db

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.media.session.MediaSession
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.im.IMApplication.Companion.mContext
import com.example.im.model.Model
import com.example.im.model.bean.Contact
import com.example.im.model.bean.UserInfo
import com.example.im.utils.Constant
import com.hyphenate.EMCallBack
import com.hyphenate.EMValueCallBack
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMUserInfo
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object ContactManager {
    private val contactDao = AppDatabase.getDatabase(mContext).ContactDao()
    private val contacts = contactDao.allContacts() as ArrayList
    private const val TAG = "ContactManger"
    private val mLBM = LocalBroadcastManager.getInstance(mContext)

    fun refreshContacts() {
        Model.getGlobalThreadPool().execute {
            EMClient.getInstance().run {
                val usernames = contactManager().allContactsFromServer
                userInfoManager().fetchUserInfoByUserId(
                    usernames.toTypedArray(),
                    object : EMValueCallBack<Map<String, EMUserInfo>> {
                        override fun onSuccess(value: Map<String, EMUserInfo>) {
                            val res = value.filter {
                                it.value.nickname != ""
                            }.values

                            val list = ArrayList<Contact>()
                            res.forEach { e -> e.let { list.add(Contact(it)) } }
                            //清空并添加新拉取新的信息
                            contacts.clear()
                            contacts.addAll(list)

                            contactDao.deleteAll()
                            contactDao.insertContacts(list)
                        }

                        override fun onError(error: Int, errorMsg: String?) {
//                            TODO("Not yet implemented")
                        }
                    })
            }
        }
    }

    fun getAllContactsFromClint(): List<Contact> {
        refreshContacts()
        return getAllContacts()
    }

    fun getAllContacts() = contacts

    fun hasContact(userId: String): Boolean {
        return contactDao.hasContact(userId) == 1
    }

    fun getContactByUserId(userId: String): Contact {
        return contactDao.getContactByUserId(userId)
    }

    fun addContact(userId: String, reason: String) {
        EMClient.getInstance().contactManager()
            .aysncAddContact(userId, reason, object : EMCallBack {
                override fun onSuccess() {
                }

                override fun onError(code: Int, error: String?) {
//                        TODO("Not yet implemented")
                }
            })
    }

    suspend fun addContact(userId: String) {
        getContactFromClient(userId)?.let {
            it.isFriend = true
            contactDao.insertContact(it)
            contacts.add(it)
            mLBM.sendBroadcast(Intent(Constant.CONTACT_CHANGED))
        }

    }

    fun deleteContactFromClient(userId: String) {
        Model.getGlobalThreadPool().execute {
            EMClient.getInstance().contactManager().deleteContact(userId)
            deleteContact(userId)
        }
    }

    fun deleteContact(userId: String) {
        Model.getGlobalThreadPool().execute {
            contacts.removeIf { e -> e.userId == userId }
            contactDao.deleteByUserId(userId)
            contacts.removeIf { e -> e.userId == userId }
            //删除会话
            ConversationManager.deleteConversation(userId)
            //删除消息历史记录
            MessageManager.deleteHistory(userId)
            mLBM.sendBroadcast(Intent(Constant.CONTACT_CHANGED))
        }
    }

//    fun addContactByUserIdFromClient(userId: String) {
//        EMClient.getInstance().userInfoManager()
//            .fetchUserInfoByUserId(
//                arrayOf(userId),
//                object : EMValueCallBack<Map<String, EMUserInfo>> {
//                    override fun onSuccess(value: Map<String, EMUserInfo>) {
//                        val userInfoList = value.values.filter {
//                            it.nickname != ""
//                        }
//                        val list = ArrayList<Contact>().apply {
//                            for (i in userInfoList) {
//                                add(Contact())
//                            }
//                        }
//                        contactDao.saveContacts(list)
//                        //发送联系人变化的广播
//                        mLBM.sendBroadcast(Intent(Constant.CONTACT_CHANGED))
//                    }
//
//                    override fun onError(error: Int, errorMsg: String?) {
//                        TODO("Not yet implemented")
//                    }
//                })
//    }

    suspend fun getContactFromClient(userId: String): Contact? =
        suspendCoroutine { continuation ->
            EMClient.getInstance().userInfoManager()
                .fetchUserInfoByUserId(
                    arrayOf(userId),
                    object : EMValueCallBack<Map<String, EMUserInfo>> {
                        override fun onSuccess(value: Map<String, EMUserInfo>) {
                            if (value[userId] != null && value[userId]?.nickname != "") {
                                value[userId]?.let { continuation.resume(Contact(it)) }
                            } else {
                                continuation.resume(null)
                            }
                        }

                        override fun onError(error: Int, errorMsg: String?) {
                            TODO("Not yet implemented")
                        }

                    })

        }

    fun clear() {
        contacts.clear()
        contactDao.deleteAll()
    }


}