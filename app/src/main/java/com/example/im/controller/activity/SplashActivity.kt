package com.example.im.controller.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.Toast
import com.example.im.R
import com.example.im.model.Model
import com.example.im.model.db.UserAccountManager
import com.google.gson.Gson
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMUserInfo


@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == 0) {
                if (!isFinishing) {
                    toMainOrLogin()
                }
            }
        }
    }

    private fun toMainOrLogin() {
        Model.getGlobalThreadPool().execute {
            //判断是否登录过
            if (EMClient.getInstance().isLoggedInBefore) {    //登陆过
                if (UserAccountManager.hasAccount(EMClient.getInstance().currentUser)) {
                    //跳转到主页
                    MainActivity.start(this)
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "账号信息保存异常！", Toast.LENGTH_SHORT).show()
                        LoginActivity.start(this)
                    }
                }
            } else {//跳转到登录页面
                LoginActivity.start(this)
            }

            //结束页面
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //延时2s发送消息
        handler.sendMessageDelayed(Message.obtain(), 1000)
    }

    override fun onDestroy() {
        super.onDestroy()

        //销毁消息
        handler.removeCallbacksAndMessages(null)
    }
}