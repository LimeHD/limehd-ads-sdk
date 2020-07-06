package tv.limehd.adsmodule.google

import android.content.Context
import android.os.Handler
import android.util.Log
import tv.limehd.adsmodule.LimeAds
import tv.limehd.adsmodule.interfaces.AdRequestListener
import tv.limehd.adsmodule.interfaces.AdShowListener
import tv.limehd.adsmodule.interfaces.FragmentState

/**
 * Class with GoogleAd logic
 * For better comprehension all business logic will be
 * put right here, but not in the LimeAds class
 *
 * @link MyTarget, Ima
 */

class Google(private val context: Context,
             private val lastAd: String,
             private val fragmentState: FragmentState,
             private val adRequestListener: AdRequestListener,
             private val adShowListener: AdShowListener,
             private val limeAds: LimeAds) {

    companion object {
        private const val TAG = "Google"
    }

    //********************************************* GOOGLE INTERSTITIAL TIMER HANDLER ****************************************************** //

    val googleTimerHandler: Handler = Handler()
    var timer = 30
    var isAllowedToRequestGoogleAd = true
    val googleTimerRunnable: Runnable = object : Runnable {
        override fun run() {
            if (timer > 0) {
                timer--
                Log.d(TAG, "Google timer: $timer")
                googleTimerHandler.postDelayed(this, 1000)
            }else{
                isAllowedToRequestGoogleAd = true
            }
        }
    }

    /**
     * Get Google Ad
     * Check if we are allowed to load google interstitial ad, cause of the timer
     *
     * @param   isLoadInterstitial  stands for understanding are we loading interstitial or in main stream with all ads
     */

    fun getGoogleAd(isLoadInterstitial: Boolean) {
        Log.d(TAG, "Load google ad")
        val googleLoader = GoogleLoader(context, lastAd, fragmentState, adRequestListener, adShowListener, isLoadInterstitial, limeAds, this)
        if(isLoadInterstitial) {
            if (isAllowedToRequestGoogleAd) {
                isAllowedToRequestGoogleAd = false
                timer = 0
                googleLoader.loadAd()
            }
        }else{
            googleLoader.loadAd()
        }
    }
}