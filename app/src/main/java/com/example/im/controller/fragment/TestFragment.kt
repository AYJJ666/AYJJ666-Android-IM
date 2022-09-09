package com.example.im.controller.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.im.R

class TestFragment : Fragment() {

    private var root: View? = null

    private var testTxt: TextView? = null
    private var testBtn: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            println("TestFragment:onCreate")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (root == null){
            root = inflater.inflate(R.layout.fragment_chat, container, false)
        }
        testTxt = root?.findViewById(R.id.tv_chat_test)
        testBtn = root?.findViewById(R.id.btn_chat_test)

        println("TestFragment:onCreateView")

        testBtn?.setOnClickListener {
            testTxt?.text = "Yes,I am,and you"
        }

        return root
    }

}