package tv.limehd.adsmodule

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.google.gson.GsonBuilder
import org.json.JSONObject
import tv.limehd.adsmodule.ima.ImaFragment
import tv.limehd.adsmodule.ima.ImaLoader
import tv.limehd.adsmodule.interfaces.AdRequestListener
import tv.limehd.adsmodule.interfaces.AdShowListener
import tv.limehd.adsmodule.interfaces.FragmentState
import tv.limehd.adsmodule.model.Ad
import tv.limehd.adsmodule.model.AdStatus
import tv.limehd.adsmodule.myTarget.MyTarget
import tv.limehd.adsmodule.myTarget.MyTargetFragment
import java.util.*

/**
 * Класс для работы с рекламой
 */

class LimeAds {

    companion object {
        private const val TAG = "LimeAds"
        private const val testAdTagUrl = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator="
        private var myTargetFragment = MyTargetFragment()
        private lateinit var viewGroup: ViewGroup
        private lateinit var fragmentState: FragmentState
        private var resId: Int = -1
        var adsList = listOf<Ad>()
        var limeAds: LimeAds? = null
        private lateinit var json: JSONObject
        private lateinit var context: Context
        private var isInitialized = false
        var adRequestListener: AdRequestListener? = null
        var adShowListener: AdShowListener? = null
        private lateinit var fragmentManager: FragmentManager
        private var currentAdStatus: AdStatus = AdStatus.Online
        private val myTargetAdStatus: HashMap<String, Int> = HashMap()
        private val imaAdStatus: HashMap<String, Int> = HashMap()
        private lateinit var myTarget: MyTarget

        /**
         * Init LimeAds library
         *
         * @param   json    JSONObject which server gives to library to load ads
         */

        @JvmStatic
        @Throws(IllegalArgumentException::class)
        fun init(json: JSONObject) {
            if(json.isNull("ads") || json.isNull("ads_global") || json.getJSONArray("ads").length() == 0){
                throw IllegalArgumentException("JSONObject is empty!")
            }
            this.json = json
            limeAds = LimeAds()
            limeAds?.getAdsList()
            isInitialized = true
        }

        /**
         * Load ad in correct order. That depends on the adsList
         */

        @JvmStatic
        @JvmOverloads
        fun getAd(context: Context,
                  resId: Int,
                  fragmentState: FragmentState,
                  isOnline: Boolean,
                  adRequestListener: AdRequestListener? = null,
                  adShowListener: AdShowListener? = null) {
            this.context = context
            this.adRequestListener = adRequestListener
            this.adShowListener = adShowListener
            val activity = context as Activity
            this.viewGroup = activity.findViewById(resId)
            this.fragmentState = fragmentState
            val activityOfFragment = context as FragmentActivity
            this.fragmentManager = activityOfFragment.supportFragmentManager
            this.resId = resId

            currentAdStatus = when(isOnline){
                true -> {
                    AdStatus.Online
                }
                false -> {
                    AdStatus.Archive
                }
            }

            for(ad in adsList){
                when(ad.type_sdk){
                    AdType.MyTarget.typeSdk -> {
                        val online = ad.is_onl
                        val archive = ad.is_arh
                        myTargetAdStatus[context.getString(R.string.isOnline)] = online
                        myTargetAdStatus[context.getString(R.string.isArchive)] = archive
                    }
                    AdType.IMA.typeSdk -> {
                        val online = ad.is_onl
                        val archive = ad.is_arh
                        imaAdStatus[context.getString(R.string.isOnline)] = online
                        imaAdStatus[context.getString(R.string.isArchive)] = archive
                    }
                }
            }

            when(adsList[0].type_sdk){
                AdType.Google.typeSdk -> limeAds?.getGoogleAd()
                AdType.IMA.typeSdk -> limeAds?.getImaAd()
                AdType.Yandex.typeSdk -> limeAds?.getYandexAd()
                AdType.MyTarget.typeSdk -> limeAds?.getMyTargetAd()
                AdType.IMADEVICE.typeSdk -> limeAds?.getImaDeviceAd()
            }
        }

        /**
         * Function returns TRUE, if library has been initialized
         * Function returns FALSE, if library has not been initialized
         *
         * @return Boolean
         */

        @JvmStatic
        fun isInitialized() : Boolean = isInitialized

        /**
         * Show fragment with loaded ad
         * Starts displaying ad
         *
         * @param   fragment      Fragment on which library will show ad (MyTargetFragment, ImaFragment ...)
         */
        @JvmStatic
        fun showAd(fragment: Fragment){
            fragmentManager.beginTransaction().replace(resId, fragment).commit()
            when(fragment){
                is MyTargetFragment -> fragment.initializePlaying()
                is ImaFragment -> fragment.initializePlaying()
            }
        }

    }

    /**
     * Get ads list from param JSONObject. This list
     * already has the correct order in which library will
     * load ad
     */

    private fun getAdsList() {
        val gson = GsonBuilder().create()
        adsList = gson.fromJson(json.getJSONArray("ads").toString(), Array<Ad>::class.java).toList()
    }

    val lastAd: String get() = adsList.last().type_sdk

    /**
     * Получить/вызвать слудующию рекламу после currentAd
     *
     * @param   currentAd   реклама на которой сейчас произошла загрузка
     */

    fun getNextAd(currentAd: String) {
        var nextAd: String? = null
        for(i in adsList.indices){
            if(adsList[i].type_sdk == currentAd){
                nextAd = adsList[i + 1].type_sdk
            }
        }
        Log.d(TAG, "Next ad after '$currentAd' is '$nextAd'")
        when(nextAd){
            AdType.Google.typeSdk -> getGoogleAd()
            AdType.IMA.typeSdk -> getImaAd()
            AdType.Yandex.typeSdk -> getYandexAd()
            AdType.MyTarget.typeSdk -> getMyTargetAd()
            AdType.IMADEVICE.typeSdk -> getImaDeviceAd()
        }
    }

    /**
     * Получить рекламу от площадки myTarget
     */

    private fun getMyTargetAd() {
        Log.d(TAG, "Load mytarget ad")
        myTarget = MyTarget(context, resId, myTargetFragment, fragmentManager, fragmentState, lastAd, adRequestListener!!, this)
        if(currentAdStatus == AdStatus.Online){
            if(myTargetAdStatus[context.getString(R.string.isOnline)] == 1){
                Log.d(TAG, "isOnline == 1, load MyTargetAd")
                myTarget.loadMyTarget()
            }else{
                Log.d(TAG, "isOnline == 0, not loading MyTargetAd")
                if(lastAd == AdType.MyTarget.typeSdk){
                    fragmentState.onErrorState(context.resources.getString(R.string.no_ad_found_at_all))
                }else {
                    getNextAd(AdType.MyTarget.typeSdk)
                }
            }
        }else if(currentAdStatus == AdStatus.Archive){
            if(myTargetAdStatus[context.getString(R.string.isArchive)] == 1){
                Log.d(TAG, "isArchive == 1, load MyTargetAd")
                myTarget.loadMyTarget()
            }else{
                Log.d(TAG, "isArchive == 0, not loading MyTargetAd")
                if(lastAd == AdType.MyTarget.typeSdk){
                    fragmentState.onErrorState(context.resources.getString(R.string.no_ad_found_at_all))
                }else {
                    getNextAd(AdType.MyTarget.typeSdk)
                }
            }
        }
    }

    /**
     * Получить рекламу от площадки IMA
     *
     */

    private fun getImaAd() {
        Log.d(TAG, "Load IMA ad")

        if(currentAdStatus == AdStatus.Online){
            if(imaAdStatus[context.getString(R.string.isOnline)] == 1){
                Log.d(TAG, "isOnline == 1, load ImaAd")
                loadImaAd()
            }else{
                Log.d(TAG, "isOnline == 0, not loading ImaAd")
                if(lastAd == AdType.IMA.typeSdk){
                    fragmentState.onErrorState(context.resources.getString(R.string.no_ad_found_at_all))
                }else {
                    getNextAd(AdType.IMA.typeSdk)
                }
            }
        }else if(currentAdStatus == AdStatus.Archive){
            if(imaAdStatus[context.getString(R.string.isArchive)] == 1){
                Log.d(TAG, "isArchive == 1, load ImaAd")
                loadImaAd()
            }else{
                Log.d(TAG, "isArchive == 0, not loading ImaAd")
                if(lastAd == AdType.IMA.typeSdk){
                    fragmentState.onErrorState(context.resources.getString(R.string.no_ad_found_at_all))
                }else {
                    getNextAd(AdType.IMA.typeSdk)
                }
            }
        }
    }

    private fun loadImaAd() {
        val imaLoader = ImaLoader(context, testAdTagUrl, viewGroup, this)
        imaLoader.loadImaAd(fragmentState)
    }

    /**
     * Получить рекламу для площадки Google
     */

    private fun getGoogleAd() {
        Log.d(TAG, "Load google ad")
        // If success then give AdFragment
        // Otherwise, onNoAd callback will be occurred

        Log.d(TAG, "GoogleAd onNoAd called")

        if(lastAd == AdType.Google.typeSdk){
            fragmentState.onErrorState(context.resources.getString(R.string.no_ad_found_at_all))
        }else {
            getNextAd(AdType.Google.typeSdk)
        }
    }

    /**
     * Получить рекламу для площадки Yandex
     */

    private fun getYandexAd() {
        Log.d(TAG, "Load yandex ad")

        // If success then give AdFragment
        // Otherwise, onNoAd callback will be occurred

        Log.d(TAG, "YandexAd onNoAd called")

        if(lastAd == AdType.Yandex.typeSdk){
            fragmentState.onErrorState(context.resources.getString(R.string.no_ad_found_at_all))
        }else {
            getNextAd(AdType.Yandex.typeSdk)
        }
    }

    private fun getImaDeviceAd() {
        Log.d(TAG, "Load Ima-Device ad")

        // If success then give AdFragment
        // Otherwise, onNoAd callback will be occurred

        Log.d(TAG, "Ima-Device onNoAd called")

        if(lastAd == AdType.IMADEVICE.typeSdk){
            fragmentState.onErrorState(context.resources.getString(R.string.no_ad_found_at_all))
        }else {
            getNextAd(AdType.IMADEVICE.typeSdk)
        }
    }

}

