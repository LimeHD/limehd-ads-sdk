package com.example.limehdadssdk

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import com.my.target.instreamads.InstreamAd
import org.json.JSONObject

/**
 * Класс для работы с рекламой
 *
 * @param context   Context приложения
 * @param json      Json, который даёт сервер. Со всеми необходимыми объектами
 */

class LimeAds constructor(private val context: Context, private val json: JSONObject) {

    companion object {
        private const val TAG = "LimeAds"
    }

    /**
     * Получить рекламу от площадки myTarget
     *
     * @param context     Context приложения
     */

    fun getMyTargetAd(context: Context, fragmentState: FragmentState) : Fragment {
        val myTargetFragment = MyTargetFragment()
        val myTargetLoader = MyTargetLoader(context)
        myTargetLoader.loadAd()
        myTargetLoader.setAdLoader(object : AdLoader {
            override fun onLoaded(instreamAd: InstreamAd) {
                myTargetFragment.setInstreamAd(instreamAd)
                myTargetFragment.initializePlaying()
                fragmentState.onSuccessState(myTargetFragment)
            }

            override fun onError() {
                fragmentState.onErrorState("Во время рекламы произошла ошибка")
            }

            override fun onNoAd() {
                fragmentState.onNoAdState("Мы не смогли найти рекламу. Попробуйте позже")
            }
        })
        return myTargetFragment
    }
}

