package tv.limehd.adsmodule

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.google.gson.GsonBuilder
import com.my.target.instreamads.InstreamAd
import org.json.JSONObject
import tv.limehd.adsmodule.ima.ImaLoader
import tv.limehd.adsmodule.interfaces.AdLoader
import tv.limehd.adsmodule.interfaces.FragmentState
import tv.limehd.adsmodule.model.Ad
import tv.limehd.adsmodule.myTarget.MyTargetFragment
import tv.limehd.adsmodule.myTarget.MyTargetLoader

/**
 * Класс для работы с рекламой
 *
 * @param context   Context приложения
 * @param json      Json, который даёт сервер. Со всеми необходимыми объектами
 */

class LimeAds constructor(private val context: Context, private val json: JSONObject) {

    companion object {
        private const val TAG = "LimeAds"
        const val testAdTagUrl = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator="
        var adsList = listOf<Ad>()
    }

    private var myTargetFragment = MyTargetFragment()
    private lateinit var viewGroup: ViewGroup

    init {
        getAdsList()
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

    fun getAd(resId: Int, fragmentState: FragmentState, viewGroup: ViewGroup) {
        // 1. Делаем запрос сначала на myTarget. Просим там рекламу.
        // 2. Если ошибка, то идём на IMA и грузим рекламу там
        this.viewGroup = viewGroup
        getMyTargetAd(context, resId, fragmentState)
    }

    /**
     * Получить рекламу от площадки myTarget
     *
     * @param context     Context приложения
     * @param resId       Id контейнера, куда нужно будет поместить фрагмент
     * @param fragmentState     callback
     */

    private fun getMyTargetAd(context: Context, resId: Int, fragmentState: FragmentState) {
        val myTargetLoader = MyTargetLoader(context)
        val activity = context as FragmentActivity
        val fragmentManager = activity.supportFragmentManager
        fragmentManager.beginTransaction().replace(resId, myTargetFragment).commit()
        myTargetLoader.loadAd()
        myTargetLoader.setAdLoader(object : AdLoader {
            override fun onLoaded(instreamAd: InstreamAd) {
                myTargetFragment.setInstreamAd(instreamAd)
                myTargetFragment.initializePlaying()
                fragmentState.onSuccessState(myTargetFragment)
            }

            override fun onError() {
                TODO("Not yet implemented")
            }

            override fun onNoAd() {
                Log.d(TAG, "onNoAd called")
                fragmentManager.beginTransaction().remove(myTargetFragment).commit()
                getImaAd(context, testAdTagUrl, viewGroup, fragmentState)
            }
        })
    }

    /**
     * Получить рекламу от площадки IMA
     *
     * @param context     Context приложения
     * @param atTagUrl    Url для показа рекламы
     * @param container       контейнер, куда нужно будет поместить фрагмент
     * @param fragmentState     callback
     */

    private fun getImaAd(context: Context, atTagUrl: String, container: ViewGroup, fragmentState: FragmentState) {
        val imaLoader = ImaLoader(context, atTagUrl, container)
        imaLoader.loadImaAd(fragmentState)
    }

}

