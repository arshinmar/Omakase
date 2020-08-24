package com.ht6.omakase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_buttons.*



class ImageConfirmFragment: Fragment()  {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_buttons, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //buttonkeepgoingreal.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.mainActivity, null))
        acceptPictureButton.setOnClickListener {
//            // We are showing only toast message. However, you can do anything you need.
          Toast.makeText(getActivity()?.getApplicationContext(), "good photo", Toast.LENGTH_SHORT).show()
       }
        retakePictureButton.setOnClickListener {
//            // We are showing only toast message. However, you can do anything you need.
            Toast.makeText(getActivity()?.getApplicationContext(), "retake photo", Toast.LENGTH_SHORT).show()
        }
    }


}