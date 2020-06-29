package tv.limehd.adsmodule.google

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import tv.limehd.adsmodule.AdType
import tv.limehd.adsmodule.LimeAds
import tv.limehd.adsmodule.R
import tv.limehd.adsmodule.interfaces.AdRequestListener
import tv.limehd.adsmodule.interfaces.AdShowListener
import tv.limehd.adsmodule.interfaces.FragmentState

/**
 * This class stands for loading google ads logic
 * This is where all business magic happens
 * Errors/Clicks/Completion/Loaded/Requested - will be thrown to AdRequest and AdShow listeners
 */

class GoogleLoader(
    private val context: Context,
    private val lastAd: String,
    private val fragmentState: FragmentState,
    private val adRequestListener: AdRequestListener,
    private val adShowListener: AdShowListener,
    private val limeAds: LimeAds
) {

    companion object {
        private const val TAG = "GoogleLoader"
    }

    private lateinit var interstitialAd: InterstitialAd

    fun loadAd() {
        interstitialAd = InterstitialAd(context)
        interstitialAd.adUnitId = "ca-app-pub-3940256099942544/1033173712"
        adRequestListener.onRequest(context.getString(R.string.requested), AdType.Google)
        interstitialAd.loadAd(AdRequest.Builder().build())
        interstitialAd.adListener = object : AdListener() {
            override fun onAdImpression() {
                Log.d(TAG, "onAdImpression: called")
            }

            override fun onAdLeftApplication() {
                Log.d(TAG, "onAdLeftApplication: called")
                adShowListener.onSkip(context.getString(R.string.skipped), AdType.Google)
            }

            override fun onAdClicked() {
                Log.d(TAG, "onAdClicked: called")
                adShowListener.onClick(context.getString(R.string.clicked), AdType.Google)
            }

            override fun onAdFailedToLoad(errorType: Int) {
                Log.d(TAG, "onAdFailedToLoad: called")
                var errorMessage = ""
                when(errorType){
                    0 -> errorMessage = "ERROR_CODE_INTERNAL_ERROR"
                    1 -> errorMessage = "ERROR_CODE_INVALID_REQUEST"
                    2 -> errorMessage = " ERROR_CODE_NETWORK_ERROR"
                    3 -> errorMessage = "ERROR_CODE_NO_FILL"
                }
                if(errorType == 3){
                    // No Ad Error
                    adRequestListener.onNoAd(errorMessage, AdType.Google)
                }else{
                    // Some other error happened
                    adRequestListener.onError(errorMessage, AdType.Google)
                }
                if(lastAd == AdType.Google.typeSdk){
                    fragmentState.onErrorState(context.resources.getString(R.string.no_ad_found_at_all))
                }else {
                    limeAds.getNextAd(AdType.Google.typeSdk)
                }
            }

            override fun onAdClosed() {
                Log.d(TAG, "onAdClosed: called")
                adShowListener.onComplete(context.getString(R.string.completed), AdType.Google)
            }

            override fun onAdOpened() {
                Log.d(TAG, "onAdOpened: called")
                adShowListener.onShow(context.getString(R.string.showing), AdType.Google)
            }

            override fun onAdLoaded() {
                Log.d(TAG, "onAdLoaded: called")
                adRequestListener.onLoaded(context.getString(R.string.loaded), AdType.Google)
                if(interstitialAd.isLoaded){
                    interstitialAd.show()
                }
            }
        }
    }

}