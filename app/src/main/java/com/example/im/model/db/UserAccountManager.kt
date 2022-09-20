package com.example.im.model.db

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.im.IMApplication.Companion.mContext
import com.example.im.model.Model
import com.example.im.model.bean.UserAccount
import com.example.im.model.bean.UserInfo
import com.example.im.utils.Constant
import com.example.im.utils.NameUtil
import com.hyphenate.EMValueCallBack
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMUserInfo
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object UserAccountManager {
    const val TAG = "UserAccountManager"

    private val userAccountDao = AppDatabase.getDatabase(mContext).UserAccountDao()
    val mLBM = LocalBroadcastManager.getInstance(mContext)


    var currentUserAccount: UserAccount
        get() {
            val userAccount = getAccountByUserId(EMClient.getInstance().currentUser).apply {
                this?.let{
                    if (avatarUrl == "") avatarUrl =
                        "https://download-sdk.oss-cn-beijing.aliyuncs.com/downloads/IMDemo/avatar/Image1.png"
                    if (signature == "") signature = "这个人很懒，什么都没说。"
                }
            }
            if (userAccount == null){
                return UserAccount("000",false)
            }else{
                return userAccount
            }
        }
        set(value) {
            addAccount(value)
        }

    suspend fun addAccountFromClient(userId: String, pwd: String, baseContext: Context): Boolean =
        suspendCoroutine { continuation ->
            Model.getGlobalThreadPool().execute {
                EMClient.getInstance().userInfoManager().fetchUserInfoByUserId(
                    arrayOf(userId),
                    object : EMValueCallBack<Map<String, EMUserInfo>> {
                        override fun onSuccess(value: Map<String, EMUserInfo>) {
                            value[userId]
                                ?.let {
                                    if (it.nickname == "") {
                                        CoroutineScope(Job()).launch {
                                            withContext(Dispatchers.Default){
                                                initNickname(NameUtil.getNickname(), baseContext as Activity)
                                                continuation.resume(true)
                                            }
                                        }

                                    } else {
                                        addAccount(
                                            UserAccount(it, pwd)
                                        )
                                        continuation.resume(true)
                                    }
                                }
                        }

                        override fun onError(error: Int, errorMsg: String?) {
                            //提示查询信息失败
                        }
                    })
            }
        }

    fun logout() {
        EMClient.getInstance().logout(true)
        ContactManager.clear()
        MessageManager.clear()
    }

    fun addAccount(userAccount: UserAccount): Long {
        val res = userAccountDao.insertUserAccount(userAccount)
        mLBM.sendBroadcast(Intent(Constant.USER_ACCOUNT_CHANGED))
        return res
    }

    private fun getAccountByUserId(userId: String): UserAccount? {
        return userAccountDao.getAccountByUserId(userId)
    }

    fun refreshCurrentUserAccount() {
        val emClient = EMClient.getInstance()
        emClient.userInfoManager().fetchUserInfoByUserId(arrayOf(emClient.currentUser),
            object : EMValueCallBack<Map<String?, EMUserInfo>?> {
                override fun onSuccess(value: Map<String?, EMUserInfo>?) {
                    value?.let {
                        for (i in it.values) {
                            if (i.userId == emClient.currentUser) {
                                Log.d(TAG, "SettingFragment")
                                addAccount(UserAccount(i))
                                mLBM.sendBroadcast(Intent(Constant.USER_ACCOUNT_CHANGED))
                            }
                        }
                    }
                }

                override fun onError(error: Int, errorMsg: String?) {
                    TODO("Not yet implemented")
                }

            })
    }

    fun hasAccount(userId: String): Boolean {
        return AppDatabase.getDatabase(mContext).UserAccountDao()
            .getAccountNumber(userId) == 1
    }

    fun setAllInfo() {
        // 设置所有用户属性。
        val userInfo = EMUserInfo()
        userInfo.userId = EMClient.getInstance().currentUser
        userInfo.nickname = "easemob"
        userInfo.avatarUrl = "http://www.easemob.com"
        userInfo.birth = "2000.10.10"
        userInfo.signature = "hello world"
        userInfo.phoneNumber = "13333333333"
        userInfo.email = "123456@qq.com"
        userInfo.gender = 1
        EMClient.getInstance().userInfoManager()
            .updateOwnInfo(userInfo, object : EMValueCallBack<String?> {
                override fun onSuccess(value: String?) {}
                override fun onError(error: Int, errorMsg: String) {}
            })
    }

    fun setNickname(nickname: String, activity: Activity) {
        Model.getGlobalThreadPool().execute {
            EMClient.getInstance().userInfoManager().updateOwnInfoByAttribute(
                EMUserInfo.EMUserInfoType.NICKNAME,
                nickname,
                object : EMValueCallBack<String?> {
                    override fun onSuccess(value: String?) {
                        activity.runOnUiThread {
                            refreshCurrentUserAccount()
                            Toast.makeText(activity, "昵称修改成功！", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onError(error: Int, errorMsg: String) {
                        activity.runOnUiThread {
                            Toast.makeText(activity, "昵称修改失败！", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        }

    }

    suspend fun initNickname(nickname: String, activity: Activity): Boolean =
        suspendCoroutine { continuation ->
            EMClient.getInstance().userInfoManager().updateOwnInfoByAttribute(
                EMUserInfo.EMUserInfoType.NICKNAME,
                nickname,
                object : EMValueCallBack<String> {
                    override fun onSuccess(value: String) {
                        refreshCurrentUserAccount()
                        continuation.resume(true)
                    }

                    override fun onError(error: Int, errorMsg: String) {
                    }
                })
        }

    fun setAvatarUrl(url: String) {
        // 以修改用户头像为例，演示如何修改指定用户属性。
        EMClient.getInstance().userInfoManager().updateOwnInfoByAttribute(
            EMUserInfo.EMUserInfoType.AVATAR_URL,
            url,
            object : EMValueCallBack<String?> {
                override fun onSuccess(value: String?) {
                    refreshCurrentUserAccount()
                }

                override fun onError(error: Int, errorMsg: String) {}
            })
    }


    fun register(userId: String, pwd: String) {
        EMClient.getInstance().run {
            createAccount(userId, pwd)
        }
    }
}