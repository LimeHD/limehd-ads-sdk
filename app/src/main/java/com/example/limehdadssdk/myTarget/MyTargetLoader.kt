package com.example.limehdadssdk.myTarget

import android.content.Context
import com.example.limehdadssdk.callback.AdLoader
import com.my.target.instreamads.InstreamAd

class MyTargetLoader constructor(private val context: Context) : InstreamAd.InstreamAdListener {

    private lateinit var adLoader: AdLoader
    private val slotId = 621422

    fun setAdLoader(adLoader: AdLoader){
        this.adLoader = adLoader
    }

    fun loadAd() {
        val instreamAd = InstreamAd(slotId, context)
        instreamAd.listener = this
        instreamAd.load()
    }

    override fun onLoad(instreamAd: InstreamAd) {
        adLoader.onLoaded(instreamAd)
    }

    override fun onComplete(p0: String, p1: InstreamAd) {
        TODO("Not yet implemented")
    }

    override fun onBannerPause(p0: InstreamAd, p1: InstreamAd.InstreamAdBanner) {
        TODO("Not yet implemented")
    }

    override fun onBannerStart(p0: InstreamAd, p1: InstreamAd.InstreamAdBanner) {
        TODO("Not yet implemented")
    }

    override fun onNoAd(p0: String, p1: InstreamAd) {
        adLoader.onNoAd()
    }

    override fun onBannerResume(p0: InstreamAd, p1: InstreamAd.InstreamAdBanner) {
        TODO("Not yet implemented")
    }

    override fun onBannerTimeLeftChange(p0: Float, p1: Float, p2: InstreamAd) {
        TODO("Not yet implemented")
    }

    override fun onError(p0: String, p1: InstreamAd) {
        adLoader.onError()
    }

    override fun onBannerComplete(p0: InstreamAd, p1: InstreamAd.InstreamAdBanner) {
        TODO("Not yet implemented")
    }

}