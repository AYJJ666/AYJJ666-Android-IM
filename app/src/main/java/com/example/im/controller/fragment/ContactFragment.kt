package com.example.im.controller.fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.im.R
import com.example.im.controller.activity.AddContactActivity
import com.example.im.controller.activity.TestActivity
import com.example.im.controller.adapter.ContactRecyclerViewAdapter
import com.example.im.databinding.FragmentContactBinding
import com.example.im.model.Model
import com.example.im.model.db.ContactManager

/**
 * A simple [Fragment] subclass.
 * Use the [ContactFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ContactFragment : Fragment() {

    private var _binding: FragmentContactBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        if (_binding == null)
            _binding = FragmentContactBinding.inflate(inflater, container, false)


        initToolbar()
        initRecyclerView()

        //下拉刷新
        binding.srlRefresh.setOnRefreshListener {
            Model.getGlobalThreadPool().execute {
                val list = ContactManager.getAllContactsFromClint()
                activity?.runOnUiThread {
                    (binding.rvContactList.adapter as ContactRecyclerViewAdapter).run {
                        setData(list)
                        //todo
                    }
                    binding.srlRefresh.isRefreshing = false;
                }
            }

        }
        return binding.root
    }

    private fun initRecyclerView() {
        Model.getGlobalThreadPool().execute {
            val list = ContactManager.getAllContacts()

            binding.rvContactList.run {
                adapter = ContactRecyclerViewAdapter(list, context)
                layoutManager = LinearLayoutManager(context)
            }
        }


    }

    private fun initToolbar() {
        binding.toolbar.run {
            title = "联系人"
//            setLogo(R.drawable.im)
        }
        //为toolbar设置menu
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.contact_toolbar_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.toolbar_menu_add_contact -> {
                        Log.d(javaClass.simpleName, "添加联系人")
                        context?.let { AddContactActivity.start(it) }
                    }
                }
                return false
            }
        })
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    companion object {
        const val TAG = "ContactFragment"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment ContactFragment.
         */
        @JvmStatic
        fun newInstance() =
            ContactFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

}