package tv.limehd.adsmodule.google

import android.content.Context
import android.util.Log
import tv.limehd.adsmodule.LimeAds
import tv.limehd.adsmodule.interfaces.AdRequestListener
import tv.limehd.adsmodule.interfaces.AdShowListener
import tv.limehd.adsmodule.interfaces.FragmentState

class Google(private val context: Context,
             private val lastAd: String,
             private val fragmentState: FragmentState,
             private val adRequestListener: AdRequestListener,
             private val adShowListener: AdShowListener,
             private val limeAds: LimeAds) {

    companion object {
        private const val TAG = "Google"
    }

    /**
     * Получить рекламу для площадки Google
     */

    fun getGoogleAd() {
        Log.d(TAG, "Load google ad")
        val googleLoader = GoogleLoader(context, lastAd, fragmentState, adRequestListener, adShowListener, limeAds)
        googleLoader.loadAd()
    }
}