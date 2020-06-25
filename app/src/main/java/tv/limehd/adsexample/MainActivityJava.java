package tv.limehd.adsexample;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import tv.limehd.adsmodule.LimeAds;

public class MainActivityJava extends AppCompatActivity {

    private static final String TAG = "MainActivityJava";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            LimeAds.init(new JSONObject());
        }catch (IllegalArgumentException e){
            Log.d(TAG, "onCreate: " + e.getMessage());
        }
    }
}
