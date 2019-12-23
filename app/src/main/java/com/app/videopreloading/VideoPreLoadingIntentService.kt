package com.app.videopreloading

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheKeyFactory
import com.google.android.exoplayer2.upstream.cache.CacheUtil
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Log
import com.google.android.exoplayer2.util.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class VideoPreLoadingIntentService : IntentService(VideoPreLoadingIntentService::class.java.simpleName) {
    private val TAG = VideoPreLoadingIntentService::class.java.simpleName
    private lateinit var mContext: Context
    private var simpleCache: SimpleCache? = null
    private var cachingJob: Job? = null
    private var videosList: ArrayList<String>? = null

    override fun onHandleIntent(intent: Intent?) {
        mContext = applicationContext
        simpleCache = MyApp.simpleCache

        if (intent != null) {
            val extras = intent.extras
            videosList = extras?.getStringArrayList(Constants.VIDEO_LIST)

            if (!videosList.isNullOrEmpty()) {
                preCacheVideo(videosList)
            }
        }
    }

    private fun preCacheVideo(videosList: ArrayList<String>?) {
        var videoUrl: String? = null
        if (!videosList.isNullOrEmpty()) {
            videoUrl = videosList[0]
            videosList.removeAt(0)
        } else {
            stopSelf()
        }
        if (!videoUrl.isNullOrBlank()) {
            val videoUri = Uri.parse(videoUrl)
            val dataSpec = DataSpec(videoUri, 0, 1000*1024, null)
            val defaultCacheKeyFactory = CacheUtil.DEFAULT_CACHE_KEY_FACTORY
            val progressListener =
                CacheUtil.ProgressListener { requestLength, bytesCached, newBytesCached ->
                    val downloadPercentage: Double = (bytesCached * 100.0
                            / requestLength)

                    Log.d(TAG, "preCacheVideo downloadPercentage: $downloadPercentage")
                }

            val cacheKey = CacheUtil.generateKey(videoUri)
            Log.d(TAG, "preCacheVideo generateKey: $cacheKey")

            val cached = CacheUtil.getCached(dataSpec, simpleCache, defaultCacheKeyFactory)

            Log.d(TAG, "preCacheVideo cached.first: ${cached.first} cached.second: ${cached.second}")

            val dataSource: DataSource =
                DefaultDataSourceFactory(
                    mContext,
                    Util.getUserAgent(mContext, getString(R.string.app_name))
                )
                    .createDataSource()

            cachingJob = GlobalScope.launch(Dispatchers.IO) {
                cacheVideo(dataSpec, defaultCacheKeyFactory, dataSource, progressListener)

                preCacheVideo(videosList)
            }
        }
    }

    private fun cacheVideo(
        dataSpec: DataSpec,
        defaultCacheKeyFactory: CacheKeyFactory?,
        dataSource: DataSource,
        progressListener: CacheUtil.ProgressListener
    ) {
        CacheUtil.cache(
            dataSpec,
            simpleCache,
            defaultCacheKeyFactory,
            dataSource,
            progressListener,
            null
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        cachingJob?.cancel()
    }
}