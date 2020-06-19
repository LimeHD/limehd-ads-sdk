package tv.limehd.adsmodule.myTarget

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.my.target.instreamads.InstreamAd
import com.my.target.instreamads.InstreamAdPlayer
import tv.limehd.adsmodule.AdType
import tv.limehd.adsmodule.LimeAds
import tv.limehd.adsmodule.R

class MyTargetFragment : Fragment() {

    companion object {
        private const val TAG = "myTargetFragment"
    }

    private lateinit var mInstreamAd: InstreamAd
    private lateinit var mInstreamAdPlayer: InstreamAdPlayer

    private lateinit var videoContainer: RelativeLayout
    private lateinit var rootContainer: RelativeLayout
    private lateinit var buttonSkip: Button

    private lateinit var leftTimeText: TextView

    private var leftTimeDelay = 0f
    private lateinit var leftHandler: Handler

    private var leftRunnable: Runnable = object : Runnable {
        override fun run() {
            if (leftTimeDelay > 0) {
                leftTimeDelay--
                val leftText = "Осталось ${leftTimeDelay.toInt()} сек."
                leftTimeText.text = leftText
                leftHandler.postDelayed(this, 1000)
            }
        }
    }

    fun setInstreamAd(instreamAd: InstreamAd){
        mInstreamAd = instreamAd
        mInstreamAd.useDefaultPlayer()
        setListener()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_instream, container, false)
        rootContainer = rootView.findViewById(R.id.root_container)
        videoContainer = rootView.findViewById(R.id.video_container)
        leftTimeText = rootView.findViewById(R.id.time_left)
        buttonSkip = rootView.findViewById(R.id.btn_skip)
        // Skip button
        buttonSkip.setOnClickListener {
            LimeAds.adShow.onSkip("SKIPPED", AdType.MyTarget)
        }
        // Ad Click
        rootContainer.setOnClickListener {
            LimeAds.adShow.onClick("CLICKED", AdType.MyTarget)
        }
        return rootView
    }

    fun initializePlaying(){
        mInstreamAd.startPreroll()
    }

    private fun setListener() {
        mInstreamAdPlayer = mInstreamAd.player!!
        videoContainer.removeAllViews()
        videoContainer.addView(mInstreamAdPlayer.view)
        mInstreamAd.listener = object : InstreamAd.InstreamAdListener {
            override fun onLoad(p0: InstreamAd) {
                Log.d(TAG, "onLoad called")
            }

            override fun onComplete(message: String, p1: InstreamAd) {
                leftHandler.removeCallbacks(leftRunnable)
                LimeAds.adShow.onComplete("COMPLETED", AdType.MyTarget)
            }

            override fun onBannerPause(p0: InstreamAd, p1: InstreamAd.InstreamAdBanner) {
                Log.d(TAG, "onBannerPause called")
            }

            override fun onBannerStart(instreamAd: InstreamAd, instreamAdBanner: InstreamAd.InstreamAdBanner) {
                LimeAds.adShow.onShow("SHOWING", AdType.MyTarget)
                leftTimeText.visibility = View.VISIBLE
                leftTimeText.bringToFront()
                if(instreamAdBanner.duration > 0) {
                    leftTimeDelay = instreamAdBanner.duration
                    leftHandler = Handler()
                    val leftText = "Осталось ${instreamAdBanner.duration.toInt()} сек."
                    leftTimeText.text = leftText
                    leftHandler.postDelayed(leftRunnable, 1000)
                }
            }

            override fun onNoAd(error: String, p1: InstreamAd) {
                Log.d(TAG, "onNoAd called")
                LimeAds.adShow.onError(error, AdType.MyTarget)
            }

            override fun onBannerResume(p0: InstreamAd, p1: InstreamAd.InstreamAdBanner) {
                Log.d(TAG, "onBannerResume called")
            }

            override fun onBannerTimeLeftChange(p0: Float, p1: Float, p2: InstreamAd) {
                Log.d(TAG, "onBannerTimeLeftChange called")
            }

            override fun onError(error: String, p1: InstreamAd) {
                Log.d(TAG, "onError called")
                LimeAds.adShow.onError(error, AdType.MyTarget)
            }

            override fun onBannerComplete(p0: InstreamAd, p1: InstreamAd.InstreamAdBanner) {
                Log.d(TAG, "onBannerComplete called")
            }

        }
    }

    override fun onResume() {
        super.onResume()
        if(this::mInstreamAd.isInitialized) {
            mInstreamAd.resume()
        }
        if(this::leftHandler.isInitialized) {
            leftHandler.postDelayed(leftRunnable, 1000)
        }
    }

    override fun onPause() {
        super.onPause()
        if(this::mInstreamAd.isInitialized) {
            mInstreamAd.pause()
        }
        if(this::leftHandler.isInitialized) {
            leftHandler.removeCallbacks(leftRunnable)
        }
    }

    override fun onStop() {
        super.onStop()
        if(this::mInstreamAd.isInitialized) {
            mInstreamAd.pause()
        }
        if(this::leftHandler.isInitialized) {
            leftHandler.removeCallbacks(leftRunnable)
        }
    }

}