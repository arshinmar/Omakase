package com.ht6.omakase

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation

class CameraActivityOld : AppCompatActivity() {

    private lateinit var nav: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        nav  = Navigation.findNavController(this,  R.id.fragment_nav)

    }
}