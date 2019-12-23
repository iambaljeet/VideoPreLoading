package com.app.videopreloading

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController

class MainActivity : AppCompatActivity(), HomeScreenCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
    }

    override fun openVideoPlayScreen(videoUrl: String) {
        val bundle = Bundle()
        bundle.putString(Constants.VIDEO_URL, videoUrl)

        findNavController(R.id.container).navigate(R.id.action_mainFragment_to_playerFragment, bundle)
    }
}
