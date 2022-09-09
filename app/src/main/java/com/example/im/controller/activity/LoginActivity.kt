package com.example.im.controller.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.im.databinding.ActivityLoginBinding
import com.example.im.model.Model
import com.example.im.model.bean.UserAccount
import com.example.im.model.db.UserAccountManager
import com.hyphenate.EMCallBack
import com.hyphenate.EMValueCallBack
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMUserInfo
import com.hyphenate.exceptions.HyphenateException

class LoginActivity : BaseActivity() {
    private val mContext = this

    private lateinit var binding: ActivityLoginBinding


    companion object {
        //应通过此方法启动activity
        fun start(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //初始化控件
        initListener()
    }

    private fun initListener() {
        binding.btnLoginLogin.setOnClickListener {
            login()
        }
        binding.btnLoginRegister.setOnClickListener {
            register()
        }

        binding.btnLoginToMain.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun register() {
        //获取用户名密码
        val name = binding.edtLoginName.text.toString()
        val pwd = binding.edtLoginPwd.text.toString()
        //校验用户名密码
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(pwd)) {
            Toast.makeText(this, "用户名或密码不能为空！", Toast.LENGTH_SHORT).show()
            return
        }
        //注册
        Model.getGlobalThreadPool().execute {
            try {
                EMClient.getInstance().createAccount(name, pwd)
                runOnUiThread {
                    Toast.makeText(this, "注册成功！", Toast.LENGTH_SHORT).show()
                }
            } catch (e: HyphenateException) {
                runOnUiThread {
                    Toast.makeText(this, "注册失败：" + e.description, Toast.LENGTH_SHORT).show()
                }
                println(e)
            }


        }
    }

    //登录按钮的业务逻辑
    private fun login() {
        //获取用户名密码
        val userId = binding.edtLoginName.text.toString()
        val pwd = binding.edtLoginPwd.text.toString()
        //校验用户名密码
        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(pwd)) {
            Toast.makeText(this, "用户名或密码不能为空！", Toast.LENGTH_SHORT).show()
            return
        }
        //登录逻辑处理
        Model.getGlobalThreadPool().execute {
            //去环信服务器登录
            EMClient.getInstance().login(userId, pwd, object : EMCallBack {
                override fun onSuccess() {
                    //保存用户账号信息到本地数据库
                    Model.getGlobalThreadPool().execute {
                        EMClient.getInstance().userInfoManager().fetchUserInfoByUserId(
                            arrayOf(userId),
                            object : EMValueCallBack<Map<String, EMUserInfo>> {
                                override fun onSuccess(value: Map<String, EMUserInfo>?) {
                                    value?.get(userId)
                                        ?.let { UserAccountManager.addAccount(UserAccount(it, pwd)) }
                                    //todo
//                                        ?.let { Model.getUserAccountDao().addAccount(it) }
                                }

                                override fun onError(error: Int, errorMsg: String?) {
                                    //提示查询信息失败
                                    runOnUiThread {
                                        Toast.makeText(mContext, "同步信息失败！", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                }
                            })
                    }
                    //对模型层数据处理
                    Model.loginSuccess(userId)
                    //提示登录成功
                    runOnUiThread {
                        Toast.makeText(mContext, "登录成功！", Toast.LENGTH_SHORT).show()
                    }
                    //跳转到主页面
                    MainActivity.start(baseContext)
                    finish()
                }

                override fun onError(code: Int, error: String?) {
                    //提示登录失败
                    runOnUiThread {
                        Toast.makeText(mContext, "登录失败：$error", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onProgress(progress: Int, status: String?) {
                    super.onProgress(progress, status)
                }
            })
        }
    }
}