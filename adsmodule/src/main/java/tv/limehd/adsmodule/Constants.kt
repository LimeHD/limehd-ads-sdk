package tv.limehd.adsmodule

import org.json.JSONObject

/**
 * Класс со вссеми нужными и ненужными константами
 */

class Constants {

    companion object {
        const val json = "{\"ads\": [\n" +
                "        {\n" +
                "            \"id\": 4,\n" +
                "            \"url\": \"\",\n" +
                "            \"is_onl\": 1,\n" +
                "            \"is_arh\": 1,\n" +
                "            \"type_sdk\": \"google\",\n" +
                "            \"type_identity\": \"googleinterstitial\",\n" +
                "            \"type_block\": 10,\n" +
                "            \"type_device\": 10,\n" +
                "            \"orientation\": 10,\n" +
                "            \"code\": \"\",\n" +
                "            \"enable_cache\": true,\n" +
                "            \"window\": 99\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 25,\n" +
                "            \"url\": \"\",\n" +
                "            \"is_onl\": 1,\n" +
                "            \"is_arh\": 1,\n" +
                "            \"type_sdk\": \"mytarget\",\n" +
                "            \"type_identity\": \"MyTarget\",\n" +
                "            \"type_block\": 10,\n" +
                "            \"type_device\": 10,\n" +
                "            \"orientation\": 10,\n" +
                "            \"code\": \"\",\n" +
                "            \"enable_cache\": true,\n" +
                "            \"window\": 99\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 6,\n" +
                "            \"url\": \"https://data.videonow.ru/?profile_id=2392203&format=vast&container=preroll\",\n" +
                "            \"is_onl\": 1,\n" +
                "            \"is_arh\": 1,\n" +
                "            \"type_sdk\": \"ima-device\",\n" +
                "            \"type_identity\": \"videonow\",\n" +
                "            \"type_block\": 10,\n" +
                "            \"type_device\": 10,\n" +
                "            \"orientation\": 10,\n" +
                "            \"code\": \"\",\n" +
                "            \"enable_cache\": false,\n" +
                "            \"window\": 99\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 26,\n" +
                "            \"url\": \"https://ads.hyperadx.com/v2/vast/b3Qn1BKM?v=3.0&device.ua={UA}&device.ip={IP}&device.ifa={GAID}&app.bundle={app.bundle}&app.cat={app.cat}&app.name={app.name}&app.storeurl={app.storeurl}&imp.video.w={player.widht}&imp.video.h={player.height}&device.geo.lat={device.geo.lat}&device.geo.lon={device.geo.lon}&regs.ext.coppa={COPPA}&regs.ext.gdpr={GDPR}&device.connectiontype={ConnectionType}&cb={cb}&device.os=(device.os}\",\n" +
                "            \"is_onl\": 1,\n" +
                "            \"is_arh\": 1,\n" +
                "            \"type_sdk\": \"ima\",\n" +
                "            \"type_identity\": \"hyperaudience\",\n" +
                "            \"type_block\": 10,\n" +
                "            \"type_device\": 10,\n" +
                "            \"orientation\": 10,\n" +
                "            \"code\": \"\",\n" +
                "            \"enable_cache\": true,\n" +
                "            \"window\": 99\n" +
                "        }\n" +
                "    ]," +
                "    \"ads_global\": {\n" +
                "        \"preroll\": {\n" +
                "            \"enabled\": true,\n" +
                "            \"epg_interval\": 1000,\n" +
                "            \"epg_timer\": 30,\n" +
                "            \"skip_first\": true,\n" +
                "            \"skip_count\": \"1\",\n" +
                "            \"tv_mode\": true,\n" +
                "            \"skip_timer\": 10\n" +
                "        },\n" +
                "        \"preload_ads\": {\n" +
                "            \"timeout\": 1200,\n" +
                "            \"count\": 2,\n" +
                "            \"block_timeout\": 1200\n" +
                "        },\n" +
                "        \"yandex_min_api\": 19,\n" +
                "        \"interstitial\": {\n" +
                "            \"timer\": \"1\",\n" +
                "            \"interval\": \"1\",\n" +
                "            \"show_skip\": true\n" +
                "        }\n" +
                "    }" +
                "}"
    }

    val jsonObject = JSONObject(json)

}