package com.example.yuelinghui.databindingsimple.data

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.example.yuelinghui.databindingsimple.util.DefaultTimer

object IntervalTimerViewModelFactory:ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IntervalTimerViewModel::class.java)) {
            return IntervalTimerViewModel(DefaultTimer) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }

}