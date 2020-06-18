package tv.limehd.adsmodule.interfaces

/**
 * Callback for loading ad. Used for showing application what
 * is going on with AD LOADING PROCESS
 */

interface AdRequest {

    fun onRequest()

    fun onLoaded()

    fun onError()

    fun onNoAd()

}