package tv.limehd.adsmodule

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import com.google.ads.interactivemedia.v3.api.AdsLoader
import com.google.ads.interactivemedia.v3.api.AdsManager
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory
import com.google.ads.interactivemedia.v3.api.ImaSdkSettings
import com.google.ads.interactivemedia.v3.api.player.ContentProgressProvider
import com.google.ads.interactivemedia.v3.api.player.VideoProgressUpdate
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.my.target.instreamads.InstreamAd
import tv.limehd.adsmodule.interfaces.AdLoader
import tv.limehd.adsmodule.myTarget.MyTargetLoader
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class BackgroundAdManger(private val adTagUrl: String, private val context: Context){

    companion object {
        private const val TAG = "BackgroundAdManger"
        lateinit var imaAdsManager: AdsManager
        lateinit var myTargetInstreamAd: InstreamAd
        lateinit var googleInterstitialAd: InterstitialAd
    }

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
                imaAdsManager = it!!.adsManager
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
                    myTargetInstreamAd = instreamAd
                    it.resume(true)
                }

                override fun onError(error: String) {
                    TODO()
                }

                override fun onNoAd(error: String) {
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
                    it.resume(false)
                }

                override fun onAdClosed() {
                    TODO()
                }

                override fun onAdOpened() {
                    TODO()
                }

                override fun onAdLoaded() {
                    googleInterstitialAd = interstitialAd
                    it.resume(true)
                }
            }
        }
    }

}