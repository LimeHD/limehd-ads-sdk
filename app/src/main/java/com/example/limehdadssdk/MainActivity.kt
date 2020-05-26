package com.example.limehdadssdk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.limehdadssdk.callback.FragmentState
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val limeAds = LimeAds(this, JSONObject(Constants.json))

        val fragmentState = object : FragmentState {
            override fun onSuccessState(fragment: Fragment) {
                supportFragmentManager.beginTransaction().replace(R.id.main_container, fragment).commit()
            }

            override fun onErrorState(message: String) {
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
            }

            override fun onNoAdState(message: String) {
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
            }
        }
        limeAds.getMyTargetAd(this, fragmentState)
    }
}
