package tv.limehd.adsmodule.google

import android.content.Context
import android.util.Log
import tv.limehd.adsmodule.AdType
import tv.limehd.adsmodule.LimeAds
import tv.limehd.adsmodule.R
import tv.limehd.adsmodule.interfaces.FragmentState

class Google(private val context: Context,
             private val lastAd: String,
             private val fragmentState: FragmentState,
             private val limeAds: LimeAds) {

    companion object {
        private const val TAG = "Google"
    }

    /**
     * Получить рекламу для площадки Google
     */

    fun getGoogleAd() {
        Log.d(TAG, "Load google ad")
        // If success then give AdFragment
        // Otherwise, onNoAd callback will be occurred

        Log.d(TAG, "GoogleAd onNoAd called")

        if(lastAd == AdType.Google.typeSdk){
            fragmentState.onErrorState(context.resources.getString(R.string.no_ad_found_at_all))
        }else {
            limeAds.getNextAd(AdType.Google.typeSdk)
        }
    }
}