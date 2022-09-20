package com.example.im.controller.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.im.databinding.ActivityLoginBinding
import com.example.im.model.Model
import com.example.im.model.db.UserAccountManager
import com.hyphenate.EMCallBack
import com.hyphenate.chat.EMClient
import kotlinx.coroutines.*

class LoginActivity : BaseActivity() {
    private val mActivity = this

    private lateinit var binding: ActivityLoginBinding


    companion object {
        //应通过此方法启动activity
        fun start(context: Context, userId: String = "", password: String = "") {
            val intent = Intent(context, LoginActivity::class.java)
            intent.putExtra("userId", userId)
            intent.putExtra("password", password)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = intent.getStringExtra("userId")
        val password = intent.getStringExtra("password")

        binding.run {
            edtLoginName.setText(userId)
            edtLoginPwd.setText(password)
        }

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

        binding.tvLogout.setOnClickListener {
            EMClient.getInstance().logout(true)
        }
    }

    private fun register() {
        RegisterActivity.start(this)
        finish()
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
        //去环信服务器登录
        EMClient.getInstance().login(userId, pwd, object : EMCallBack {
            override fun onSuccess() {
                CoroutineScope(Job()).launch {
                    //保存用户账号信息到本地数据库
                    withContext(Dispatchers.Default) {
                        val res = UserAccountManager.addAccountFromClient(
                            userId,
                            pwd,
                            mActivity
                        )
                        if (res){
                            //对模型层数据处理
                            Model.loginSuccess(userId)
                            //提示登录成功
                            runOnUiThread {
                                Toast.makeText(mActivity, "登录成功！", Toast.LENGTH_SHORT).show()
                            }
                            //跳转到主页面
                            MainActivity.start(baseContext)
                            finish()
                        }
                    }
                }
            }

            override fun onError(code: Int, error: String?) {
                //提示登录失败
                runOnUiThread {
                    Toast.makeText(mActivity, "登录失败：$error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onProgress(progress: Int, status: String?) {
                super.onProgress(progress, status)
                //todo 登录动画
            }
        })

    }
}