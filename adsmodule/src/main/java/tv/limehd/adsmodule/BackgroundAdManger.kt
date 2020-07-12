package tv.limehd.adsmodule

import android.content.Context
import android.util.Log
import android.view.View
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

class BackgroundAdManger(private val adTagUrl: String, private val context: Context, private val limeAds: LimeAds){

    companion object {
        private const val TAG = "BackgroundAdManger"
        var imaAdsManager: AdsManager? = null
        var myTargetInstreamAd: InstreamAd? = null
        var googleInterstitialAd: InterstitialAd? = null

        fun clearVariables() {
            googleInterstitialAd = null
            imaAdsManager = null
            myTargetInstreamAd = null
        }
    }

    // ***************************************************** IMA SDK ********************************************************* //

    private lateinit var mSdkFactory: ImaSdkFactory
    private lateinit var mSdkSetting: ImaSdkSettings
    private lateinit var mAdsLoader: AdsLoader

    suspend fun loadIma(container: ViewGroup) : Boolean {
        Log.d(TAG, "loadIma: called")

        container.visibility = View.GONE

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

        LimeAds.adRequestListener?.onRequest(context.getString(R.string.requested), AdType.IMA)
        mAdsLoader.requestAds(adsRequest)

        return suspendCoroutine {cont ->
            mAdsLoader.addAdsLoadedListener {
                Log.d(TAG, "loadIma: ima loaded")
                imaAdsManager = it!!.adsManager
                cont.resume(true)
            }
            mAdsLoader.addAdErrorListener {
                Log.d(TAG, "loadIma: error")
                LimeAds.adRequestListener?.onError(it?.error?.message.toString(), AdType.IMA)
                cont.resume(false)
            }
        }
    }

    // ***************************************************** MyTarget SDK ********************************************************* //

    suspend fun loadMyTarget() : Boolean {
        Log.d(TAG, "loadMyTarget: called")
        LimeAds.adRequestListener?.onRequest(context.getString(R.string.requested), AdType.MyTarget)
        val myTargetLoader = MyTargetLoader(context)
        myTargetLoader.loadAd()
        return suspendCoroutine {
            myTargetLoader.setAdLoader(object : AdLoader {
                override fun onRequest() {
                    TODO()
                }

                override fun onLoaded(instreamAd: InstreamAd) {
                    Log.d(TAG, "onLoaded: mytarget loaded")
                    LimeAds.adRequestListener?.onLoaded(context.getString(R.string.loaded), AdType.MyTarget)
                    myTargetInstreamAd = instreamAd
                    it.resume(true)
                }

                override fun onError(error: String) {
                    LimeAds.adRequestListener?.onError(context.getString(R.string.requestError), AdType.MyTarget)
                }

                override fun onNoAd(error: String) {
                    Log.d(TAG, "onNoAd: mytarget error")
                    LimeAds.adRequestListener?.onNoAd(context.getString(R.string.noAd), AdType.MyTarget)
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
        LimeAds.adRequestListener?.onRequest(context.getString(R.string.requested), AdType.Google)
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
                    LimeAds.adShowListener?.onClick(context.getString(R.string.clicked), AdType.Google)
                }

                override fun onAdFailedToLoad(errorType: Int) {
                    Log.d(TAG, "onAdFailedToLoad: google error")

                    var errorMessage = ""
                    when(errorType){
                        0 -> errorMessage = "ERROR_CODE_INTERNAL_ERROR"
                        1 -> errorMessage = "ERROR_CODE_INVALID_REQUEST"
                        2 -> errorMessage = " ERROR_CODE_NETWORK_ERROR"
                        3 -> errorMessage = "ERROR_CODE_NO_FILL"
                    }
                    if(errorType == 3){
                        // No Ad Error
                        LimeAds.adRequestListener?.onNoAd(errorMessage, AdType.Google)
                    }else{
                        // Some other error happened
                        LimeAds.adRequestListener?.onError(errorMessage, AdType.Google)
                    }

                    LimeAds.adShowListener?.onError(context.getString(R.string.error), AdType.Google)
                    it.resume(false)
                }

                override fun onAdClosed() {

                    LimeAds.adShowListener?.onComplete(context.getString(R.string.completed), AdType.Google)

                    // should restart BackgroundAdManager
                    clearVariables()
                    LimeAds.startBackgroundRequests(context, LimeAds.resId, LimeAds.fragmentState, LimeAds.adShowListener!!)

                    // should start preroll handler
                    limeAds.prerollTimerHandler.postDelayed(limeAds.prerollTimerRunnable, 1000)
                }

                override fun onAdOpened() {
                    LimeAds.adShowListener?.onShow(context.getString(R.string.showing), AdType.Google)
                }

                override fun onAdLoaded() {
                    Log.d(TAG, "onAdLoaded: google loaded")
                    googleInterstitialAd = interstitialAd
                    it.resume(true)
                }
            }
        }
    }

}