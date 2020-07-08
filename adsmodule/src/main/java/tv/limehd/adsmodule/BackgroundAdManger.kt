package tv.limehd.adsmodule

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import com.google.ads.interactivemedia.v3.api.*
import com.google.ads.interactivemedia.v3.api.player.ContentProgressProvider
import com.google.ads.interactivemedia.v3.api.player.VideoProgressUpdate
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.my.target.instreamads.InstreamAd
import tv.limehd.adsmodule.interfaces.AdLoader
import tv.limehd.adsmodule.model.Ad
import tv.limehd.adsmodule.myTarget.MyTargetLoader

class BackgroundAdManger(private val adsList: List<Ad>,
                         private val container: ViewGroup,
                         private val adTagUrl: String,
                         private val context: Context
) :  AdsLoader.AdsLoadedListener, AdErrorEvent.AdErrorListener{

    companion object {
        private const val TAG = "BackgroundAdManger"
        lateinit var imaAdsManager: AdsManager
        lateinit var myTargetInstreamAd: InstreamAd
        lateinit var googleInterstitialAd: InterstitialAd
        var isAdLoaded = false
    }

    fun getNextAd(currentAd: String) {
        var nextAd: String? = null
        for(i in adsList.indices){
            if(adsList[i].type_sdk == currentAd){
                nextAd = adsList[i + 1].type_sdk
            }
        }
        Log.d(TAG, "Next ad after '$currentAd' is '$nextAd'")
        when(nextAd){
            AdType.Google.typeSdk -> loadGoogleAd()
            AdType.IMA.typeSdk -> loadIma(container)
            AdType.MyTarget.typeSdk -> loadMyTarget()
        }
    }

    // ***************************************************** IMA SDK ********************************************************* //

    private lateinit var mSdkFactory: ImaSdkFactory
    private lateinit var mSdkSetting: ImaSdkSettings
    private lateinit var mAdsLoader: AdsLoader

    fun loadIma(container: ViewGroup) {
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

        adsRequest.setVastLoadTimeout(Constants.TIMEOUT)

        mAdsLoader.requestAds(adsRequest)
    }

    override fun onAdsManagerLoaded(adsManagerLoaderEvent: AdsManagerLoadedEvent?) {
        // Ima ad is successfully loaded. We should save to cache
        Log.d(TAG, "onAdsManagerLoaded: called. Save to cache Ima")
        imaAdsManager = adsManagerLoaderEvent!!.adsManager
        isAdLoaded = true
    }

    override fun onAdError(p0: AdErrorEvent?) {
        Log.d(TAG, "onAdError: load next ad after Ima")
//        getNextAd(AdType.IMA.typeSdk)
    }

    // ***************************************************** MyTarget SDK ********************************************************* //

    fun loadMyTarget() {
        val myTargetLoader = MyTargetLoader(context)
        myTargetLoader.loadAd()
        myTargetLoader.setAdLoader(object : AdLoader {
            override fun onRequest() {
                TODO()
            }

            override fun onLoaded(instreamAd: InstreamAd) {
                Log.d(TAG, "onLoaded: called. Save to cache MyTarget")
                myTargetInstreamAd = instreamAd
                isAdLoaded = true
            }

            override fun onError(error: String) {
                TODO()
            }

            override fun onNoAd(error: String) {
                Log.d(TAG, "onNoAd: load next ad after MyTarget")
//                getNextAd(AdType.MyTarget.typeSdk)
            }
        })
    }

    // ***************************************************** Google SDK ********************************************************* //

    private lateinit var interstitialAd: InterstitialAd

    fun loadGoogleAd() {
        interstitialAd = InterstitialAd(context)
        interstitialAd.adUnitId = LimeAds.googleUnitId
        interstitialAd.loadAd(AdRequest.Builder().build())
        interstitialAd.adListener = object : AdListener() {
            override fun onAdImpression() {
                TODO()
            }

            override fun onAdLeftApplication() {
                TODO()
            }

            override fun onAdClicked() {
                TODO()
            }

            override fun onAdFailedToLoad(errorType: Int) {
                // load next ad
                Log.d(TAG, "onAdFailedToLoad: load next ad after google")
//                getNextAd(AdType.Google.typeSdk)
            }

            override fun onAdClosed() {
                TODO()
            }

            override fun onAdOpened() {
                TODO()
            }

            override fun onAdLoaded() {
                Log.d(TAG, "onAdLoaded: called. Save to cache Google")
                googleInterstitialAd = interstitialAd
                isAdLoaded = true
            }
        }
    }

}