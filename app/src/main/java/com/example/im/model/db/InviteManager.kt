package com.example.im.model.db

import android.content.Intent
import android.graphics.ColorSpace
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.disklrucache.DiskLruCache
import com.example.im.IMApplication.Companion.mContext
import com.example.im.model.Model
import com.example.im.model.bean.InvitationInfo
import com.example.im.model.bean.UserInfo
import com.example.im.model.dao.InviteDao
import com.example.im.utils.Constant
import com.hyphenate.EMValueCallBack
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMUserInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

object InviteManager {
    private val inviteDao = AppDatabase.getDatabase(mContext).inviteDao()


    fun getAllInvitation() = inviteDao.allInvitation()

    fun saveInvitation(
        userId: String,
        reason: String,
        status: InvitationInfo.InvitationStatus
    ) {
        //获取信息
        EMClient.getInstance().userInfoManager()
            .fetchUserInfoByUserId(
                arrayOf(userId),
                object : EMValueCallBack<Map<String, EMUserInfo>> {
                    override fun onSuccess(value: Map<String, EMUserInfo>?) {
                        if (value != null) {
                            value[userId]?.let {
                                if (it.userId == userId) {
                                    inviteDao.insertInvitation(InvitationInfo(it, reason, status))
                                }
                            }
                        }
                    }

                    override fun onError(error: Int, errorMsg: String?) {
                        TODO("Not yet implemented")
                    }

                })

    }

    // 同意好友申请。
    fun acceptInvitation(userId: String) {
        Model.getGlobalThreadPool().execute {
            EMClient.getInstance().contactManager().acceptInvitation(userId)
            //设置邀请信息状态
            inviteDao.updateStatus(userId, InvitationInfo.InvitationStatus.INVITE_ACCEPT)
            //获取并保存新联系人信息
            CoroutineScope(Job()).launch {
                ContactManager.addContact(userId)
            }
        }
    }

    // 拒绝好友申请。
    fun refuseInvitation(userId: String) {
        Model.getGlobalThreadPool().execute {
            EMClient.getInstance().contactManager().declineInvitation(userId)
            //设置邀请信息状态
            inviteDao.updateStatus(userId, InvitationInfo.InvitationStatus.INVITE_REFUSE)
        }
    }

    fun clean() {
//        InviteDao.clean()
    }

}