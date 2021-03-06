package tv.limehd.adsexample

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import tv.limehd.adsmodule.AdType
import tv.limehd.adsmodule.Constants
import tv.limehd.adsmodule.LimeAds
import tv.limehd.adsmodule.interfaces.AdRequestListener
import tv.limehd.adsmodule.interfaces.AdShowListener
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
            LimeAds.myTargetBlockId = 9525
            LimeAds.googleUnitId = "ca-app-pub-3940256099942544/1033173712"
        }catch (e: IllegalArgumentException) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }

        openGoogleAd.setOnClickListener {
            try {
                LimeAds.getGoogleInterstitialAd()
            }catch (e: NullPointerException){
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }
        }

        getAd.setOnClickListener {
            try {
                LimeAds.getAd(
                    this,
                    R.id.main_container,
                    fragmentStateCallback,
                    true,
                    adRequestCallback,
                    adShowCallback
                )
            }catch (e: NullPointerException){
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }
        }

        startBackgroundRequests.setOnClickListener {
            try {
                LimeAds.startBackgroundRequests(this, R.id.main_container, fragmentStateCallback, adRequestCallback, adShowCallback)
            }catch (e: NullPointerException){
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }
        }

    }

    private val fragmentStateCallback = object : FragmentState {
        override fun onSuccessState(fragment: Fragment, owner: AdType) {
            Log.d(TAG, "onSuccessState called from ${owner.typeSdk}")
            LimeAds.showAd(fragment)
        }

        override fun onErrorState(message: String, owner: AdType) {
            Log.d(TAG, "onErrorState called: $message from ${owner.typeSdk}")
            Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
        }
    }

    private val adRequestCallback = object : AdRequestListener {
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

    private val adShowCallback = object : AdShowListener {
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
