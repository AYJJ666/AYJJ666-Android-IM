package com.example.im

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import com.example.im.model.Model
import com.example.im.utils.SpUtils
import com.example.im.utils.TestUtils
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMOptions

class IMApplication : Application() {
    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var mContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("BaseActivity", "Application")

        mContext = applicationContext

        //初始化环信sdk
        val options = EMOptions()
        options.appKey = "1120220714100786#my-im"
        options.acceptInvitationAlways = false  //设置需要同意才能接受邀请
        EMClient.getInstance().init(this, options)

        //初始化数据模型层类
        Model.init(this)

        SpUtils.init(this)


        //测试代码
        TestUtils.test()

    }



}

