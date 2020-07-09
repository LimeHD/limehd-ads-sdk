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
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class BackgroundAdManger(private val adsList: List<Ad>,
                         private val container: ViewGroup,
                         private val adTagUrl: String,
                         private val context: Context
){

    companion object {
        private const val TAG = "BackgroundAdManger"
        lateinit var imaAdsManager: AdsManager
        lateinit var myTargetInstreamAd: InstreamAd
        lateinit var googleInterstitialAd: InterstitialAd
        var isAdLoaded = false
    }

//    fun getNextAd(currentAd: String) {
//        var nextAd: String? = null
//        for(i in adsList.indices){
//            if(adsList[i].type_sdk == currentAd){
//                nextAd = adsList[i + 1].type_sdk
//            }
//        }
//        Log.d(TAG, "Next ad after '$currentAd' is '$nextAd'")
//        when(nextAd){
//            AdType.Google.typeSdk -> loadGoogleAd()
//            AdType.IMA.typeSdk -> loadIma(container)
//            AdType.MyTarget.typeSdk -> loadMyTarget()
//        }
//    }

    // ***************************************************** IMA SDK ********************************************************* //

    private lateinit var mSdkFactory: ImaSdkFactory
    private lateinit var mSdkSetting: ImaSdkSettings
    private lateinit var mAdsLoader: AdsLoader

    suspend fun loadIma(container: ViewGroup) : Boolean {
        Log.d(TAG, "loadIma: called")
        mSdkFactory = ImaSdkFactory.getInstance()
        mSdkSetting = mSdkFactory.createImaSdkSettings()
        mSdkSetting.language = "ru"

        val adDisplayContainer = mSdkFactory.createAdDisplayContainer()
        adDisplayContainer.adContainer = container

        mAdsLoader = mSdkFactory.createAdsLoader(context, mSdkSetting, adDisplayContainer)

        val adsRequest = mSdkFactory.createAdsRequest()
        adsRequest.adTagUrl = adTagUrl
        adsRequest.adDisplayContainer = adDisplayContainer

        adsRequest.contentProgressProvider = ContentProgressProvider {
            VideoProgressUpdate(0, 120)
        }

        adsRequest.setVastLoadTimeout(Constants.TIMEOUT)

        mAdsLoader.requestAds(adsRequest)

        return suspendCoroutine {cont ->
            mAdsLoader.addAdsLoadedListener {
                Log.d(TAG, "onAdsManagerLoaded: called. Save to cache Ima")
                imaAdsManager = it!!.adsManager
                isAdLoaded = true
                cont.resume(true)
            }
            mAdsLoader.addAdErrorListener {
                cont.resume(false)
            }
        }
    }

    // ***************************************************** MyTarget SDK ********************************************************* //

    suspend fun loadMyTarget() : Boolean {
        Log.d(TAG, "loadMyTarget: called")
        val myTargetLoader = MyTargetLoader(context)
        myTargetLoader.loadAd()
        return suspendCoroutine {
            myTargetLoader.setAdLoader(object : AdLoader {
                override fun onRequest() {
                    TODO()
                }

                override fun onLoaded(instreamAd: InstreamAd) {
                    Log.d(TAG, "onLoaded: called. Save to cache MyTarget")
                    myTargetInstreamAd = instreamAd
                    isAdLoaded = true
                    it.resume(true)
                }

                override fun onError(error: String) {
                    TODO()
                }

                override fun onNoAd(error: String) {
                    Log.d(TAG, "onNoAd: load next ad after MyTarget")
                    it.resume(false)
                }
            })
        }
    }

    // ***************************************************** Google SDK ********************************************************* //

    private lateinit var interstitialAd: InterstitialAd

    suspend fun loadGoogleAd() : Boolean {
        Log.d(TAG, "loadGoogleAd: called")
        interstitialAd = InterstitialAd(context)
        interstitialAd.adUnitId = LimeAds.googleUnitId
        interstitialAd.loadAd(AdRequest.Builder().build())
        return suspendCoroutine {
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
                    it.resume(false)
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
                    it.resume(true)
                }
            }
        }
    }

}