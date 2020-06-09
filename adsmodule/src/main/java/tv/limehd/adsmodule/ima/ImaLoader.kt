package tv.limehd.adsmodule.ima

import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.ViewGroup
import com.google.ads.interactivemedia.v3.api.*
import com.google.ads.interactivemedia.v3.api.player.ContentProgressProvider
import com.google.ads.interactivemedia.v3.api.player.VideoProgressUpdate
import tv.limehd.adsmodule.AdType
import tv.limehd.adsmodule.LimeAds
import tv.limehd.adsmodule.R
import tv.limehd.adsmodule.interfaces.FragmentState

class ImaLoader constructor(private val context: Context, private val adTagUrl: String, private val container: ViewGroup, private val limeAds: LimeAds)
    : AdsLoader.AdsLoadedListener, AdErrorEvent.AdErrorListener, AdEvent.AdEventListener {

    companion object {
        private const val TAG = "ImaLoader"
    }

    private var isTimeout = true

    private lateinit var imaFragment: ImaFragment

    private lateinit var mSdkFactory: ImaSdkFactory
    private lateinit var mSdkSetting: ImaSdkSettings
    private lateinit var mAdsLoader: AdsLoader
    private lateinit var adsManager: AdsManager

    private lateinit var fragmentState: FragmentState

    private var TIMEOUT = 3L
    private val leftHandler: Handler = Handler()

    private var leftRunnable: Runnable = object : Runnable {
        override fun run() {
            if (TIMEOUT > 0) {
                TIMEOUT--
                Log.d(TAG, TIMEOUT.toString())
                leftHandler.postDelayed(this, 1000)
            }else{
                if(isTimeout){
                    fragmentState.onErrorState(context.resources.getString(R.string.timeout_occurred))
                }
            }
        }
    }

    fun loadImaAd(fragmentState: FragmentState) {
        Log.d(TAG, "loadImaAd called")
        this.fragmentState = fragmentState

        mSdkFactory = ImaSdkFactory.getInstance()
        mSdkSetting = mSdkFactory.createImaSdkSettings()
        mSdkSetting.language = "ru"

        val adDisplayContainer = mSdkFactory.createAdDisplayContainer()
        adDisplayContainer.adContainer = container

        mAdsLoader = mSdkFactory.createAdsLoader(context, mSdkSetting, adDisplayContainer)
        mAdsLoader.addAdsLoadedListener(this)
        mAdsLoader.addAdErrorListener(this)

        val adsRequest = mSdkFactory.createAdsRequest()
        adsRequest.adTagUrl = adTagUrl
        adsRequest.adDisplayContainer = adDisplayContainer

        adsRequest.contentProgressProvider = ContentProgressProvider {
            VideoProgressUpdate(0, 120)
        }
        mAdsLoader.requestAds(adsRequest)

        leftHandler.postDelayed(leftRunnable, 1000)
    }

    override fun onAdsManagerLoaded(adsManagerLoadedEvent: AdsManagerLoadedEvent?) {
        Log.d(TAG, "onAdsManagerLoaded")
        isTimeout = false
        adsManager = adsManagerLoadedEvent!!.adsManager
        adsManager.addAdEventListener(this)
        adsManager.addAdErrorListener(this)
        adsManager.init()
    }

    override fun onAdError(adErrorEvent: AdErrorEvent?) {
        Log.d(TAG, "Ima onAdError called")
        isTimeout = false
        if(limeAds.lastAd == AdType.IMA.typeSdk){
            fragmentState.onErrorState(adErrorEvent?.error?.message.toString())
        }else {
            limeAds.getNextAd(AdType.IMA.typeSdk)
        }
    }

    override fun onAdEvent(adEvent: AdEvent?) {
        when(adEvent?.type){
            AdEvent.AdEventType.LOADED -> {
                Log.d(TAG, "loaded")
                adsManager.start()
                imaFragment = ImaFragment()
                fragmentState.onSuccessState(imaFragment)
            }
            AdEvent.AdEventType.ALL_ADS_COMPLETED -> {
                Log.d(TAG, "ALL_ADS_COMPLETED")
            }
            AdEvent.AdEventType.CLICKED -> {
                Log.d(TAG, "CLICKED")
            }
            AdEvent.AdEventType.COMPLETED -> {
                Log.d(TAG, "COMPLETED")
            }
            AdEvent.AdEventType.CUEPOINTS_CHANGED -> {
                Log.d(TAG, "CUEPOINTS_CHANGED")
            }
            AdEvent.AdEventType.CONTENT_PAUSE_REQUESTED -> {
                Log.d(TAG, "CONTENT_PAUSE_REQUESTED")
            }
            AdEvent.AdEventType.CONTENT_RESUME_REQUESTED -> {
                Log.d(TAG, "CONTENT_RESUME_REQUESTED")
            }
            AdEvent.AdEventType.FIRST_QUARTILE -> {
                Log.d(TAG, "FIRST_QUARTILE")
            }
            AdEvent.AdEventType.LOG -> {
                Log.d(TAG, "LOG")
            }
            AdEvent.AdEventType.AD_BREAK_READY -> {
                Log.d(TAG, "AD_BREAK_READY")
            }
            AdEvent.AdEventType.MIDPOINT -> {
                Log.d(TAG, "MIDPOINT")
            }
            AdEvent.AdEventType.PAUSED -> {
                Log.d(TAG, "PAUSED")
            }
            AdEvent.AdEventType.RESUMED -> {
                Log.d(TAG, "RESUMED")
            }
            AdEvent.AdEventType.SKIPPABLE_STATE_CHANGED -> {
                Log.d(TAG, "SKIPPABLE_STATE_CHANGED")
            }
            AdEvent.AdEventType.SKIPPED -> {
                Log.d(TAG, "SKIPPED")
            }
            AdEvent.AdEventType.STARTED -> {
                Log.d(TAG, "STARTED")
            }
            AdEvent.AdEventType.TAPPED -> {
                Log.d(TAG, "TAPPED")
            }
            AdEvent.AdEventType.ICON_TAPPED -> {
                Log.d(TAG, "ICON_TAPPED")
            }
            AdEvent.AdEventType.THIRD_QUARTILE -> {
                Log.d(TAG, "THIRD_QUARTILE")
            }
            AdEvent.AdEventType.AD_PROGRESS -> {
                Log.d(TAG, "AD_PROGRESS")
            }
            AdEvent.AdEventType.AD_BUFFERING -> {
                Log.d(TAG, "AD_BUFFERING")
            }
            AdEvent.AdEventType.AD_BREAK_STARTED -> {
                Log.d(TAG, "AD_BREAK_STARTED")
            }
            AdEvent.AdEventType.AD_BREAK_ENDED -> {
                Log.d(TAG, "AD_BREAK_ENDED")
            }
            AdEvent.AdEventType.AD_PERIOD_STARTED -> {
                Log.d(TAG, "AD_PERIOD_STARTED")
            }
            AdEvent.AdEventType.AD_PERIOD_ENDED -> {
                Log.d(TAG, "AD_PERIOD_ENDED")
            }
        }
    }

}