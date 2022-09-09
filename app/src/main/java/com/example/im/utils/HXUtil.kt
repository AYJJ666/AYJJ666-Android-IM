package com.example.im.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.im.model.Model
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

object HXUtil {
    fun getToken(activity: Activity) {
        Model.getGlobalThreadPool().execute {
            val client = OkHttpClient()

            val requestBody = FormBody.Builder()
//                .addEncoded("content-type","application/json; charset=utf-8")
                .add("grant_type","client_credentials")
                .add("client_id", "YXA6loFbeqikQWKK_muANInpuw")
                .add("client_secret","YXA6w1ouce2NCqFxRbhmxPKa_ny3ZQc")
                .add("ttl","600")
                .build()

            val request = Request.Builder()
                .addHeader("Content-Type", "application/json")
                .url("https://im-api-v2.easemob.com/${Constant.ORG_NAME}/${Constant.APP_NAME}/token")
                .post(requestBody)
                .build()



            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    activity.runOnUiThread {
                        Toast.makeText(activity, "失败：${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val string = Gson().toJson(response.body)

                    activity.runOnUiThread {
                        Toast.makeText(activity, string, Toast.LENGTH_SHORT).show()
                    }
                    Log.d("HXUtil", string)
                    Log.d("HXUtil:call", string)
                }

            })
        }
    }
}