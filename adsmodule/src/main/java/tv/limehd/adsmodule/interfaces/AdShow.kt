package tv.limehd.adsmodule.interfaces

/**
 * Callback for showing ad. Used for showing application what
 * is going on with AD SHOWING PROCESS
 */

interface AdShow {

    fun onShow()

    fun onError()

    fun onComplete()

    fun onSkip()

    fun onClick()
}