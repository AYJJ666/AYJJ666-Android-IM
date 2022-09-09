package com.example.im.controller.activity

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.im.R
import com.example.im.controller.adapter.FragmentPagerAdapter
import com.example.im.controller.fragment.ChatFragment
import com.example.im.controller.fragment.ContactFragment
import com.example.im.controller.fragment.SettingFragment
import com.example.im.databinding.ActivityMainBinding
import com.example.im.model.Model
import com.example.im.utils.TestUtils
import com.hyphenate.chat.EMClient

//主页面
class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding

    private var chatFragment = ChatFragment.newInstance()
    private val contactFragment = ContactFragment.newInstance()
    private val settingFragment = SettingFragment.newInstance()
    private lateinit var currentFragment: Fragment


    companion object {
        const val TAG = "MainActivity"

        //应通过此方法启动activity
        fun start(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val navHostFragment =
//            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
//        binding.navigationMainBottom.setupWithNavController(navHostFragment.navController)


//        val navController = findNavController(R.id.fragment_container)
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_chat, R.id.navigation_contact, R.id.navigation_setting
//            )
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        binding.navigationMainBottom.setupWithNavController(navController)


//        initViewPager()
        initNavigationView()

//        setPage()

//        EMClient.getInstance().chatManager().loadAllConversations()
        Log.d(TAG, supportFragmentManager.fragments.size.toString())

    }


    private fun initNavigationView() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, chatFragment)
        currentFragment = chatFragment
        fragmentTransaction.add(R.id.fragment_container, contactFragment)
        fragmentTransaction.add(R.id.fragment_container, settingFragment)
        fragmentTransaction.hide(contactFragment)
        fragmentTransaction.hide(settingFragment)
        fragmentTransaction.commit()

        binding.navigationMainBottom.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_chat -> {
                    if (currentFragment !is ChatFragment) {
                        supportFragmentManager.beginTransaction()
                            .hide(currentFragment)
                            .show(chatFragment)
                            .commit()
                        currentFragment = chatFragment
                    }

                    return@setOnItemSelectedListener true
                }
                R.id.navigation_contact -> {
                    if (currentFragment !is ContactFragment) {
                        supportFragmentManager.beginTransaction()
                            .hide(currentFragment)
                            .show(contactFragment)
                            .commit()
                        currentFragment = contactFragment
                    }
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_setting -> {
                    if (currentFragment !is SettingFragment) {
                        supportFragmentManager.beginTransaction()
                            .hide(currentFragment)
                            .show(settingFragment)
                            .commit()
                        currentFragment = settingFragment
                    }
                    return@setOnItemSelectedListener true
                }
                else -> {
                    false
                }
            }
        }
    }

//    private fun initViewPager() {
//        //添加3个fragment到viewPage2
//        val list = ArrayList<Fragment>().apply {
//            add(ChatFragment.newInstance())
//            add(ContactFragment.newInstance())
//            add(SettingFragment.newInstance())
//        }
//
//        val fragmentPagerAdapter = FragmentPagerAdapter(supportFragmentManager, lifecycle, list)
//        binding.vpMain.adapter = fragmentPagerAdapter
//
//        binding.vpMain.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                when (position) {
//                    0 -> {
//                        binding.navigationMainBottom.selectedItemId = R.id.navigation_item1
//                    }
//                    1 -> {
//                        binding.navigationMainBottom.selectedItemId = R.id.navigation_item2
//                    }
//                    2 -> {
//                        binding.navigationMainBottom.selectedItemId = R.id.navigation_item3
//                    }
//                }
//            }
//        })
//    }
}