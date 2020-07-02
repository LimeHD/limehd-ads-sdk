package tv.limehd.adsmodule

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.google.gson.GsonBuilder
import org.json.JSONObject
import tv.limehd.adsmodule.google.Google
import tv.limehd.adsmodule.ima.Ima
import tv.limehd.adsmodule.ima.ImaFragment
import tv.limehd.adsmodule.interfaces.AdRequestListener
import tv.limehd.adsmodule.interfaces.AdShowListener
import tv.limehd.adsmodule.interfaces.FragmentState
import tv.limehd.adsmodule.model.Ad
import tv.limehd.adsmodule.model.AdStatus
import tv.limehd.adsmodule.model.Interstitial
import tv.limehd.adsmodule.myTarget.MyTarget
import tv.limehd.adsmodule.myTarget.MyTargetFragment

/**
 * Класс для работы с рекламой
 */

class LimeAds {

    companion object {
        private const val TAG = "LimeAds"
        private const val testAdTagUrl = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator="
        private lateinit var myTargetFragment: MyTargetFragment
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
        private val googleAdStatus: HashMap<String, Int> = HashMap()
        private lateinit var myTarget: MyTarget
        private lateinit var ima: Ima
        private lateinit var google: Google
        private lateinit var loadedAdStatusMap: HashMap<String, Int>
        private var isGetAdBeingCalled = false
        private lateinit var interstitial: Interstitial

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
            limeAds?.getAdsGlobalModels()
            isInitialized = true
        }

        /**
         * Load ad in correct order. That depends on the JSONObject.
         * Get current ad status (Online or Archive)
         * Get and Save isOnline and isArchive for each ad in JSONObject
         */

        @JvmStatic
        @JvmOverloads
        fun getAd(context: Context,
                  resId: Int,
                  fragmentState: FragmentState,
                  isOnline: Boolean,
                  adRequestListener: AdRequestListener? = null,
                  adShowListener: AdShowListener? = null) {
            isGetAdBeingCalled = true
            this.context = context
            this.adRequestListener = adRequestListener
            this.adShowListener = adShowListener
            val activity = context as Activity
            this.viewGroup = activity.findViewById(resId)
            this.fragmentState = fragmentState
            val activityOfFragment = context as FragmentActivity
            this.fragmentManager = activityOfFragment.supportFragmentManager
            this.resId = resId

            limeAds?.getCurrentAdStatus(isOnline)
            limeAds?.populateAdStatusesHashMaps()

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

        /**
         * Get only google interstitial ad
         *
         * When user exit from fullscreen mode, app should call google interstitial ad
         * If during interstitial -> timer (JSONObject from server) user exit from fullscreen more again,
         * library should not request google interstitial ad. Otherwise if timer is ended and user exit
         * fullscreen mode, then library should request google interstitial ad
         */

        @JvmStatic
        @JvmOverloads
        fun getGoogleInterstitialAd(
            context: Context? = null,
            isOnline: Boolean? = null,
            fragmentState: FragmentState? = null,
            adRequestListener: AdRequestListener? = null,
            adShowListener: AdShowListener? = null
        ) {
            if(!isGetAdBeingCalled){
                this.context = context!!
                this.fragmentState = fragmentState!!
                this.adRequestListener = adRequestListener
                this.adShowListener = adShowListener

                limeAds?.getCurrentAdStatus(isOnline!!)
                limeAds?.populateAdStatusesHashMaps()
            }
            limeAds?.let {
                if(it.isAllowedToRequestGoogleAd){
                    it.isAllowedToRequestGoogleAd = false
                    if(it.timer == 0){
                        it.timer = 30
                    }
                    it.googleTimerHandler.postDelayed(it.googleTimerRunnable, 1000)
                    google = Google(this.context, it.lastAd, this.fragmentState, this.adRequestListener!!, this.adShowListener!!, it)
                    it.loadAd(AdType.Google)
                }
            }
        }

    }

    private fun getCurrentAdStatus(isOnline: Boolean) {
        currentAdStatus = when(isOnline){
            true -> AdStatus.Online
            false -> AdStatus.Archive
        }
    }

    private fun populateAdStatusesHashMaps() {
        for(ad in adsList){
            val online = ad.is_onl
            val archive = ad.is_arh
            when(ad.type_sdk){
                AdType.MyTarget.typeSdk -> {
                    myTargetAdStatus[context.getString(R.string.isOnline)] = online
                    myTargetAdStatus[context.getString(R.string.isArchive)] = archive
                }
                AdType.IMA.typeSdk -> {
                    imaAdStatus[context.getString(R.string.isOnline)] = online
                    imaAdStatus[context.getString(R.string.isArchive)] = archive
                }
                AdType.Google.typeSdk -> {
                    googleAdStatus[context.getString(R.string.isOnline)] = online
                    googleAdStatus[context.getString(R.string.isArchive)] = archive
                }
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

    /**
     * Get ads_global models from JSONObject
     * preroll, preload_ads, yandex_min_api, interstitial
     */

    private fun getAdsGlobalModels() {
        val gson = GsonBuilder().create()
        interstitial = gson.fromJson(json.getJSONObject("ads_global").getJSONObject("interstitial").toString(), Interstitial::class.java)
    }

    val lastAd: String get() = adsList.last().type_sdk      // last ad type sdk in JSONObject

    /**
     * Получить/вызвать слудущию рекламу после currentAd
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
        myTargetFragment = MyTargetFragment(lastAd, fragmentState, this)
        myTarget = MyTarget(context, resId, myTargetFragment, fragmentManager, fragmentState, lastAd, adRequestListener!!, this)
        loadAd(AdType.MyTarget)
    }

    /**
     * Function stands for :
     * 1) If current ad status is online status, then we check isOnline value for current ad (JSONObject)
     *  1.1) If isOnline equals 1, then we load current ad
     *  1.2) Otherwise, if current ad is the last ad in the JSONObject -> ads array, then we throw exception.
     *       If current ad is not the last ad, then we load next ad after current one
     *
     *  @param  adType  Type of the current ad (IMA, MyTarget...)
     *  @param adStatus Status for the ad (Online or Archive)
     */

    private fun loadOrLoadNextOrThrowExceptionByAdStatus(adType: AdType, adStatus: String){
        if(loadedAdStatusMap[adStatus] == 1){
            Log.d(TAG, "$adStatus == 1, load ${adType.typeSdk}")
            when(adType){
                is AdType.IMA -> ima.loadAd()
                is AdType.MyTarget -> myTarget.loadAd()
                is AdType.Google -> google.getGoogleAd()
            }
        }else{
            Log.d(TAG, "$adStatus == 0, not loading ${adType.typeSdk}")
            if(lastAd == adType.typeSdk){
                fragmentState.onErrorState(context.resources.getString(R.string.no_ad_found_at_all), adType)
            }else {
                getNextAd(adType.typeSdk)
            }
        }
    }

    /**
     * Load ad base on AdType and AdStatus
     *
     * @param   adType  Type of the current ad (IMA, MyTarget...)
     */

    private fun loadAd(adType: AdType){
        when(adType){
            is AdType.IMA -> loadedAdStatusMap = imaAdStatus
            is AdType.MyTarget -> loadedAdStatusMap = myTargetAdStatus
            is AdType.Google -> loadedAdStatusMap = googleAdStatus
        }
        if(currentAdStatus == AdStatus.Online){
            loadOrLoadNextOrThrowExceptionByAdStatus(adType, context.getString(R.string.isOnline))
        }else if(currentAdStatus == AdStatus.Archive){
            loadOrLoadNextOrThrowExceptionByAdStatus(adType, context.getString(R.string.isArchive))
        }
    }

    /**
     * Получить рекламу от площадки IMA
     */

    private fun getImaAd() {
        Log.d(TAG, "Load IMA ad")
        ima = Ima(context, testAdTagUrl, viewGroup, fragmentState, this)
        loadAd(AdType.IMA)
    }

    /**
     * Получить рекламу для площадки Google
     */

    private fun getGoogleAd() {
        Log.d(TAG, "getGoogleAd: called")
        google = Google(context, lastAd, fragmentState, adRequestListener!!, adShowListener!!, this)
        loadAd(AdType.Google)
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
            fragmentState.onErrorState(context.resources.getString(R.string.no_ad_found_at_all), AdType.Yandex)
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
            fragmentState.onErrorState(context.resources.getString(R.string.no_ad_found_at_all), AdType.IMADEVICE)
        }else {
            getNextAd(AdType.IMADEVICE.typeSdk)
        }
    }

    //********************************************* GOOGLE INTERSTITIAL TIMER HANDLER ****************************************************** //

    private var googleTimerHandler: Handler = Handler()
    private var timer = 30
    private var isAllowedToRequestGoogleAd = true
    private var googleTimerRunnable: Runnable = object : Runnable {
        override fun run() {
            if (timer > 0) {
                timer--
                Log.d(TAG, timer.toString())
                googleTimerHandler.postDelayed(this, 1000)
            }else{
                isAllowedToRequestGoogleAd = true
            }
        }
    }

}

