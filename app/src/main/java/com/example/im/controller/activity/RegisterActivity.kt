package com.example.im.controller.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.im.controller.viewModel.RegisterViewModel
import com.example.im.databinding.ActivityRegistBinding
import com.example.im.model.db.ContactManager
import com.example.im.model.db.UserAccountManager
import com.hyphenate.chat.EMClient
import com.hyphenate.exceptions.HyphenateException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistBinding
    private lateinit var viewModel: RegisterViewModel

    companion object {
        private const val TAG = "RegisterActivity"
        //应通过此方法启动activity
        fun start(context: Context) {
            val intent = Intent(context, RegisterActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]

        initView()

    }

    private fun initView() {
        binding.run {
            etUserId.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    Log.d(TAG, "失去焦点")
                    val userId = etUserId.text.toString()
                    if (userId != "") {
                        CoroutineScope(Dispatchers.Default).launch {
                            val res = ContactManager.getContactFromClient(userId)
                            Log.d(TAG, res.toString())
                            if (res != null) {
                                runOnUiThread {
                                    tvUserIdTips.visibility = View.VISIBLE
                                }

                            } else {
                                runOnUiThread {
                                    tvUserIdTips.visibility = View.GONE
                                }

                            }

                        }
                    }


                }
            }

            btnRegister.setOnClickListener {
                register()
            }
            btnLogin.setOnClickListener {
                LoginActivity.start(baseContext)
                finish()
            }
        }
    }

    private fun register() {
        binding.run {
            val userId = etUserId.text.toString()
            val pwd = etUserId.text.toString()
            val checkPwd = etCheckPwd.text.toString()

            if (userId == "") {
                Toast.makeText(baseContext, "请设置账号！", Toast.LENGTH_SHORT).show()
                return
            }
            if (pwd == "") {
                Toast.makeText(baseContext, "请设置密码！", Toast.LENGTH_SHORT).show()
                return
            }
            if (pwd != checkPwd) {
                Toast.makeText(baseContext, "两次输入的密码不一致！", Toast.LENGTH_SHORT).show()
                return
            }

            CoroutineScope(Dispatchers.Default).launch {
                try {
                    UserAccountManager.register(userId, pwd)

                    LoginActivity.start(baseContext, userId, pwd)
                    runOnUiThread {
                        Toast.makeText(baseContext, "注册成功！", Toast.LENGTH_SHORT).show()
                    }

                    finish()
                } catch (e: HyphenateException) {
                    runOnUiThread{
                        Toast.makeText(baseContext, "注册失败：" + e.description, Toast.LENGTH_SHORT).show()
                    }

                }
            }

        }
    }
}