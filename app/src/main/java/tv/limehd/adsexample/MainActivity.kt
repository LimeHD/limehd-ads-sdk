package tv.limehd.adsexample

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import org.json.JSONObject
import tv.limehd.adsmodule.AdType
import tv.limehd.adsmodule.Constants
import tv.limehd.adsmodule.LimeAds
import tv.limehd.adsmodule.interfaces.AdRequest
import tv.limehd.adsmodule.interfaces.AdShow
import tv.limehd.adsmodule.interfaces.FragmentState

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        try {
            LimeAds.init(JSONObject(Constants.json))
            LimeAds.getAd(this, R.id.main_container, fragmentStateCallback, adRequestCallback, adShowCallback)
        }catch (e: IllegalArgumentException) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }

    private val fragmentStateCallback = object : FragmentState {
        override fun onSuccessState(fragment: Fragment) {
            Log.d(TAG, "onSuccessState called")
            supportFragmentManager.beginTransaction().replace(R.id.main_container, fragment).commit()
        }

        override fun onErrorState(message: String) {
            Log.d(TAG, "onErrorState called: $message")
            Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
        }
    }

    private val adRequestCallback = object : AdRequest {
        override fun onRequest(message: String, owner: AdType) {
            Log.d(TAG, "$message from ${owner.typeSdk}")
        }

        override fun onLoaded(message: String, owner: AdType) {
            Log.d(TAG, "$message from ${owner.typeSdk}")
        }

        override fun onError(message: String, owner: AdType) {
            Log.d(TAG, "$message from ${owner.typeSdk}")
        }

        override fun onNoAd(message: String, owner: AdType) {
            Log.d(TAG, "$message from ${owner.typeSdk}")
        }

   }

    private val adShowCallback = object : AdShow {
        override fun onShow(message: String, owner: AdType) {
            Log.d(TAG, "$message from ${owner.typeSdk}")
        }

        override fun onError(message: String, owner: AdType) {
            Log.d(TAG, "$message from ${owner.typeSdk}")
        }

        override fun onComplete(message: String, owner: AdType) {
            Log.d(TAG, "$message from ${owner.typeSdk}")
        }

        override fun onSkip(message: String, owner: AdType) {
            Log.d(TAG, "$message from ${owner.typeSdk}")
        }

        override fun onClick(message: String, owner: AdType) {
            Log.d(TAG, "$message from ${owner.typeSdk}")
        }

    }

}
