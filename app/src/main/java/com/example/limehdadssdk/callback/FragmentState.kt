package com.example.limehdadssdk.callback

import androidx.fragment.app.Fragment

interface FragmentState {
    fun onSuccessState(fragment: Fragment)
    fun onErrorState(message: String)
    fun onNoAdState(message: String)
}