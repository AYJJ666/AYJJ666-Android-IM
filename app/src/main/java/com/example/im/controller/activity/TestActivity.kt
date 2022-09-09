package com.example.im.controller.activity

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.im.controller.adapter.ContactRecyclerViewAdapter
import com.example.im.databinding.ActivityTestBinding
import com.example.im.utils.HXUtil
import okhttp3.*
import java.io.IOException

class TestActivity : BaseActivity() {
    private lateinit var binding:ActivityTestBinding

    companion object{
        //应通过此方法启动activity
        fun start(context: Context) {
            val intent = Intent(context, ChatActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)


//        binding.rvTest.adapter = ContactRecyclerViewAdapter()
//        binding.rvTest.layoutManager = LinearLayoutManager(baseContext)


        binding.tvTest.setOnClickListener {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://www.baidu.com/")
                .build()



            client.newCall(request).enqueue(object :Callback{
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        Toast.makeText(baseContext, "失败：${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    runOnUiThread { Toast.makeText(baseContext, response.message, Toast.LENGTH_SHORT).show() }
                }

            })
        }

        binding.btnTest.setOnClickListener {
            HXUtil.getToken(this)
        }
    }
}