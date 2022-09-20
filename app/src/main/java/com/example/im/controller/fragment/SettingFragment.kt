package com.example.im.controller.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.example.im.IMApplication.Companion.mContext
import com.example.im.controller.activity.LoginActivity
import com.example.im.databinding.FragmentSettingBinding
import com.example.im.model.Model
import com.example.im.model.bean.InvitationInfo
import com.example.im.model.dao.InviteDao
import com.example.im.model.db.*
import com.example.im.utils.Constant
import com.hyphenate.chat.EMClient
import kotlinx.coroutines.*


/**
 * A simple [Fragment] subclass.
 * Use the [SettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    //todo context为何为空
    private lateinit var mLBM: LocalBroadcastManager
    private lateinit var mBR: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

        mLBM = context?.let { LocalBroadcastManager.getInstance(it) }!!
        mBR = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                Log.d(TAG, "刷新成功")
                initUserAccount()
                binding.srlRefresh.isRefreshing = false
            }

        }
        mLBM.registerReceiver(mBR, IntentFilter(Constant.USER_ACCOUNT_CHANGED))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null)
            _binding = FragmentSettingBinding.inflate(inflater, container, false)

        initUserAccount()

        binding.srlRefresh.setOnRefreshListener {
            Model.getGlobalThreadPool().execute {
                Log.d(TAG, "刷新")
                UserAccountManager.refreshCurrentUserAccount()
            }
            binding.srlRefresh.isRefreshing = false
        }

        binding.btnSettingLogout.setOnClickListener {
            CoroutineScope(Job()).launch {
                //清空数据库
                UserAccountManager.logout()
                context?.let { it1 -> LoginActivity.start(it1) }
                activity?.finish()
            }

        }

        binding.btnSetNickname.setOnClickListener {
            activity?.let { it1 ->
                UserAccountManager.setNickname(
                    binding.etSetNickname.text.toString(),
                    it1
                )
                initUserAccount()
            }
        }

        binding.btnSetAvatarUrl.setOnClickListener {
            activity?.let {
                UserAccountManager.setAvatarUrl(
                    binding.etAvatarUrl.text.toString()
                )
                initUserAccount()
            }
        }


        return binding.root
    }


    private fun initUserAccount() {
        //基本信息
        Model.getGlobalThreadPool().execute {
            UserAccountManager.currentUserAccount.let {
                activity?.runOnUiThread {
                    binding.run {
                        tvNickname.text = it.nickname
                        tvSign.text = it.signature
                    }
                    Glide.with(binding.root)
                        .load(it.avatarUrl)
                        .into(binding.imgAvatar)

                }
            }


        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mLBM.unregisterReceiver(mBR)
    }

    companion object {
        const val TAG = "SettingFragment"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment SettingFragment.
         */
        @JvmStatic
        fun newInstance() =
            SettingFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}