package tv.limehd.adsmodule

import android.content.Context
import com.nhaarman.mockitokotlin2.mock
import junit.framework.Assert.assertEquals
import org.json.JSONObject
import org.junit.Before
import org.junit.Test

class LimeAdsTests() {

    private lateinit var context: Context
    private lateinit var jsonObject: JSONObject
    private lateinit var limeAds: LimeAds

    @Before
    fun setUp() {
        context = mock()
        jsonObject = JSONObject(Constants.json)
        limeAds = LimeAds(context, jsonObject)
    }

    @Test
    fun `When init LimeAds should have list of ads`() {
        assertEquals(5, limeAds.adsList.size)
    }

    @Test
    fun `When get last ad should have correct data`() {
        val lastAd = jsonObject.getJSONArray("ads").getJSONObject(4).getString("type_sdk")
        assertEquals(lastAd, limeAds.lastAd)
    }

}