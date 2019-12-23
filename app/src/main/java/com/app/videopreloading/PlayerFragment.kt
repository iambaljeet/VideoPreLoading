package com.app.videopreloading

import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.analytics.AnalyticsCollector
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheUtil
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Clock
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.player_fragment.*


class PlayerFragment : Fragment() {
    private var cacheDataSourceFactory: CacheDataSourceFactory? = null
    private var simpleExoPlayer: SimpleExoPlayer? = null
    private var simpleCache: SimpleCache? = null

    companion object {
        fun newInstance() = PlayerFragment()
    }

    private lateinit var viewModel: PlayerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.player_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PlayerViewModel::class.java)

        val videoUrl = arguments?.getString(Constants.VIDEO_URL)

        simpleExoPlayer = context?.let { SimpleExoPlayer.Builder(it,
            DefaultRenderersFactory(it),
            DefaultTrackSelector(it), DefaultLoadControl(), DefaultBandwidthMeter.getSingletonInstance(it), Looper.getMainLooper(),
            AnalyticsCollector(Clock.DEFAULT), false, Clock.DEFAULT).build() }

        simpleCache = MyApp.simpleCache

        cacheDataSourceFactory = CacheDataSourceFactory(simpleCache,
            DefaultHttpDataSourceFactory(context?.let { Util.getUserAgent(it, getString(R.string.app_name)) }),
            CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR
        )

        val videoUri = Uri.parse(videoUrl)
        val mediaSource = ProgressiveMediaSource.Factory(cacheDataSourceFactory).createMediaSource(videoUri)
        val dataSpec = DataSpec(videoUri)

        CacheUtil.getCached(dataSpec, simpleCache, CacheUtil.DEFAULT_CACHE_KEY_FACTORY)

        playerView.player = simpleExoPlayer
        simpleExoPlayer?.playWhenReady = true
        simpleExoPlayer?.seekTo(0, 0)
        simpleExoPlayer?.repeatMode = Player.REPEAT_MODE_OFF
        simpleExoPlayer?.prepare(mediaSource, true, false)
    }

}
