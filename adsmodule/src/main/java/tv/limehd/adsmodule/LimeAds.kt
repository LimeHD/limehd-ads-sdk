package tv.limehd.adsmodule

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.google.gson.GsonBuilder
import com.my.target.instreamads.InstreamAd
import org.json.JSONObject
import tv.limehd.adsmodule.ima.ImaLoader
import tv.limehd.adsmodule.interfaces.AdLoader
import tv.limehd.adsmodule.interfaces.AdRequest
import tv.limehd.adsmodule.interfaces.FragmentState
import tv.limehd.adsmodule.model.Ad
import tv.limehd.adsmodule.myTarget.MyTargetFragment
import tv.limehd.adsmodule.myTarget.MyTargetLoader

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
        lateinit var adRequest: AdRequest

        /**
         * Init LimeAds library
         *
         * @param   json    JSONObject which server gives to library to load ads
         */

        @JvmStatic
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
        fun getAd(context: Context, resId: Int, fragmentState: FragmentState, adRequest: AdRequest) {
            this.context = context
            this.adRequest = adRequest
            val activity = context as Activity
            this.viewGroup = activity.findViewById(resId)
            this.fragmentState = fragmentState
            this.resId = resId
            when(adsList[0].type_sdk){
                AdType.Google.typeSdk -> limeAds?.getGoogleAd()
                AdType.IMA.typeSdk -> limeAds?.getImaAd()
                AdType.Yandex.typeSdk -> limeAds?.getYandexAd()
                AdType.MyTarget.typeSdk -> limeAds?.getMyTargetAd()
                AdType.IMADEVICE.typeSdk -> limeAds?.getImaDeviceAd()
            }
        }

        @JvmStatic
        fun isInitialized() : Boolean = isInitialized
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
        val myTargetLoader = MyTargetLoader(context)
        val activity = context as FragmentActivity
        val fragmentManager = activity.supportFragmentManager
        fragmentManager.beginTransaction().replace(resId, myTargetFragment).commit()
        myTargetLoader.loadAd()
        myTargetLoader.setAdLoader(object : AdLoader {
            override fun onRequest() {
                adRequest.onRequest("Ad is requested", AdType.MyTarget)
            }

            override fun onLoaded(instreamAd: InstreamAd) {
                adRequest.onLoaded("Ad is loaded", AdType.MyTarget)
                myTargetFragment.setInstreamAd(instreamAd)
                myTargetFragment.initializePlaying()
                fragmentState.onSuccessState(myTargetFragment)
            }

            override fun onError(error: String) {
                adRequest.onError(error, AdType.MyTarget)
            }

            override fun onNoAd(error: String) {
                Log.d(TAG, "MyTarget onNoAd called")
                adRequest.onNoAd(error, AdType.MyTarget)
                fragmentManager.beginTransaction().remove(myTargetFragment).commit()
                if(lastAd == AdType.MyTarget.typeSdk){
                    fragmentState.onErrorState(context.resources.getString(R.string.no_ad_found_at_all))
                }else {
                    getNextAd(AdType.MyTarget.typeSdk)
                }
            }
        })
    }

    /**
     * Получить рекламу от площадки IMA
     *
     */

    private fun getImaAd() {
        Log.d(TAG, "Load IMA ad")
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

