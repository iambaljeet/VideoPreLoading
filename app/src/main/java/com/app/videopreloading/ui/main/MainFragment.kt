package com.app.videopreloading.ui.main

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.videopreloading.Constants
import com.app.videopreloading.HomeScreenCallback
import com.app.videopreloading.R
import com.app.videopreloading.VideoPreLoadingIntentService
import kotlinx.android.synthetic.main.main_fragment.*
import java.lang.Exception

class MainFragment : Fragment() {
    private var homeScreenCallback: HomeScreenCallback? = null

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private val videoList = arrayListOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        videoList.add("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4")
        videoList.add("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4")

        startPreLoadingService()

        buttonPlayVideo1.setOnClickListener {
            homeScreenCallback?.openVideoPlayScreen(videoList.get(0))
        }
        buttonPlayVideo2.setOnClickListener {
            homeScreenCallback?.openVideoPlayScreen(videoList.get(0))
        }
    }

    private fun startPreLoadingService() {
        val preloadingServiceIntent = Intent(context, VideoPreLoadingIntentService::class.java)
        preloadingServiceIntent.putStringArrayListExtra(Constants.VIDEO_LIST, videoList)
        context?.startService(preloadingServiceIntent)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            homeScreenCallback = context as HomeScreenCallback
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDetach() {
        super.onDetach()
        homeScreenCallback = null
    }
}