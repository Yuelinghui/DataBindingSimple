package com.example.yuelinghui.databindingsimple.util

import android.arch.lifecycle.ViewModel
import android.databinding.Observable
import android.databinding.PropertyChangeRegistry

open class ObservableViewModel : ViewModel(),Observable {

    private val callbacks:PropertyChangeRegistry by lazy { PropertyChangeRegistry() }



    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.remove(callback)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.add(callback)
    }

    fun notifyChange() {
        callbacks.notifyCallbacks(this,0,null)
    }

    fun notifyPropertyChangeed(fieldId:Int) {
        callbacks.notifyCallbacks(this,fieldId,null)
    }

}