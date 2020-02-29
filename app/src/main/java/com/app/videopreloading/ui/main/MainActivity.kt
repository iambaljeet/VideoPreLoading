package com.app.videopreloading.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.app.videopreloading.R
import com.app.videopreloading.callback.HomeScreenCallback
import com.app.videopreloading.utility.Constants

class MainActivity : AppCompatActivity(),
    HomeScreenCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
    }

    override fun openVideoPlayScreen(videoUrl: String) {
        val bundle = Bundle()
        bundle.putString(Constants.VIDEO_URL, videoUrl)

        findNavController(R.id.container).navigate(
            R.id.action_mainFragment_to_playerFragment, bundle
        )
    }
}
