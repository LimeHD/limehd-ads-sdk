package tv.limehd.adsmodule

import org.json.JSONObject

/**
 * Класс со вссеми нужными и ненужными константами
 */

class Constants {

    companion object {
        const val json = "{\"ads\": [\n" +
                "        {\n" +
                "            \"id\": 2,\n" +
                "            \"url\": \"https://an.yandex.ru/meta/347075?imp-id=2&charset=UTF-8&target-ref=http%3A%2F%2Flimehd.ru&page-ref=http%3A%2F%2Flimehd.ru&rnd=3132518945\",\n" +
                "            \"is_onl\": 1,\n" +
                "            \"is_arh\": 1,\n" +
                "            \"type_sdk\": \"yandex\",\n" +
                "            \"type_identity\": \"yandex\",\n" +
                "            \"type_block\": 10,\n" +
                "            \"type_device\": 10,\n" +
                "            \"orientation\": 10,\n" +
                "            \"code\": \"\",\n" +
                "            \"enable_cache\": true,\n" +
                "            \"window\": 99\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 16,\n" +
                "            \"url\": \"https://px.adhigh.net/p/direct_vast?pid=172&tid=instream_spbtv\",\n" +
                "            \"is_onl\": 1,\n" +
                "            \"is_arh\": 1,\n" +
                "            \"type_sdk\": \"ima-device\",\n" +
                "            \"type_identity\": \"getintent\",\n" +
                "            \"type_block\": 10,\n" +
                "            \"type_device\": 10,\n" +
                "            \"orientation\": 10,\n" +
                "            \"code\": \"\",\n" +
                "            \"enable_cache\": true,\n" +
                "            \"window\": 99\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 1,\n" +
                "            \"url\": \"https://px.adhigh.net/p/direct_vast?pid=172&tid=instream_spbtv\",\n" +
                "            \"is_onl\": 1,\n" +
                "            \"is_arh\": 1,\n" +
                "            \"type_sdk\": \"ima\",\n" +
                "            \"type_identity\": \"mytarget\",\n" +
                "            \"type_block\": 10,\n" +
                "            \"type_device\": 10,\n" +
                "            \"orientation\": 10,\n" +
                "            \"code\": \"\",\n" +
                "            \"enable_cache\": true,\n" +
                "            \"window\": 99\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 4,\n" +
                "            \"url\": \"https://px.adhigh.net/p/direct_vast?pid=172&tid=instream_spbtv\",\n" +
                "            \"is_onl\": 1,\n" +
                "            \"is_arh\": 1,\n" +
                "            \"type_sdk\": \"google\",\n" +
                "            \"type_identity\": \"google\",\n" +
                "            \"type_block\": 10,\n" +
                "            \"type_device\": 10,\n" +
                "            \"orientation\": 10,\n" +
                "            \"code\": \"\",\n" +
                "            \"enable_cache\": true,\n" +
                "            \"window\": 99\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 3,\n" +
                "            \"url\": \"https://data.videonow.ru/?profile_id=2535934&format=vast&container=preroll\",\n" +
                "            \"is_onl\": 1,\n" +
                "            \"is_arh\": 1,\n" +
                "            \"type_sdk\": \"mytarget\",\n" +
                "            \"type_identity\": \"videonow\",\n" +
                "            \"type_block\": 10,\n" +
                "            \"type_device\": 10,\n" +
                "            \"orientation\": 10,\n" +
                "            \"code\": \"\",\n" +
                "            \"enable_cache\": true,\n" +
                "            \"window\": 99\n" +
                "        }\n" +
                "    ],\n" +
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