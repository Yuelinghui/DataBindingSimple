package com.example.yuelinghui.databindingsimple.ui

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.Observable
import android.databinding.ObservableInt
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.yuelinghui.databindingsimple.BR
import com.example.yuelinghui.databindingsimple.R
import com.example.yuelinghui.databindingsimple.data.IntervalTimerViewModel
import com.example.yuelinghui.databindingsimple.data.IntervalTimerViewModelFactory
import com.example.yuelinghui.databindingsimple.databinding.ActivityMainBinding


const val SHARED_PREFS_KEY = "timer"

class MainActivity : AppCompatActivity() {

    private val intervalTimerViewModel:IntervalTimerViewModel
    by lazy {
        ViewModelProviders.of(this,IntervalTimerViewModelFactory).get(IntervalTimerViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding:ActivityMainBinding = DataBindingUtil.setContentView<ActivityMainBinding>(this,R.layout.activity_main)

        val viewModel = intervalTimerViewModel
        binding.viewmodel = viewModel

        observeAndSaveTimePerSet(viewModel.timePerWorkSet,R.string.prefs_timePerWorkSet)
        observeAndSaveTimePerSet(viewModel.timePerRestSet,R.string.prefs_timePerRestSet)

        observeAndSaveNumberOfSets(viewModel)

        if (savedInstanceState == null) {
            restorePreferences(viewModel)
            observeAndSaveNumberOfSets(viewModel)
        }
    }

    private fun observeAndSaveTimePerSet(timePerWorkSet:ObservableInt,perfsKey:Int) {
        timePerWorkSet.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                Log.d("saveTimePerWorkSet","Saving time-per-set preference")

                val sharedPref = getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE)?:return
                sharedPref.edit().apply{
                    putInt(getString(perfsKey),(sender as ObservableInt).get())
                    commit()
                }
            }

        })
    }

    private fun restorePreferences(viewModel: IntervalTimerViewModel) {
        val sharedPref = getSharedPreferences(SHARED_PREFS_KEY,Context.MODE_PRIVATE)?:return
        val timePerWorkSetKey = getString(R.string.prefs_timePerWorkSet)
        var wasAnythingRestored = false
        if (sharedPref.contains(timePerWorkSetKey)) {
            viewModel.timePerWorkSet.set(sharedPref.getInt(timePerWorkSetKey,100))
            wasAnythingRestored = true
        }

        val timePerRestSetKey = getString(R.string.prefs_timePerRestSet)
        if (sharedPref.contains(timePerRestSetKey)) {
            viewModel.timePerRestSet.set(sharedPref.getInt(timePerRestSetKey,50))
            wasAnythingRestored = true
        }

        val numberOfSetsKey = getString(R.string.prefs_numberOfSets)
        if (sharedPref.contains(numberOfSetsKey)) {
            viewModel.numberOfSets = arrayOf(0,sharedPref.getInt(numberOfSetsKey,5))
            wasAnythingRestored = true
        }

        if (wasAnythingRestored) Log.d("saveTimePerWorkSet","Preferences restored")

        viewModel.stopButtonClicked()
    }

    private fun observeAndSaveNumberOfSets(viewModel: IntervalTimerViewModel) {
        viewModel.addOnPropertyChangedCallback(object :Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (propertyId == BR.numberOfSets) {
                    val sharedPref = getSharedPreferences(SHARED_PREFS_KEY,Context.MODE_PRIVATE)?:return
                    sharedPref.edit().apply {
                        putInt(getString(R.string.prefs_numberOfSets),viewModel.numberOfSets[1])
                        commit()
                    }
                }
            }

        })
    }

}
