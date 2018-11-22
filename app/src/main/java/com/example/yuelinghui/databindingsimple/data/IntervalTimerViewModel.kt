package com.example.yuelinghui.databindingsimple.data

import android.databinding.Bindable
import android.databinding.ObservableInt
import com.example.yuelinghui.databindingsimple.BR
import com.example.yuelinghui.databindingsimple.util.ObservableViewModel
import com.example.yuelinghui.databindingsimple.util.Timer
import com.example.yuelinghui.databindingsimple.util.cleanSecondsString
import java.util.*
import kotlin.math.round

const val INITIAL_SECONDS_PER_WORK_SET = 5
const val INITIAL_SECONDS_PER_REST_SET = 2
const val INITIAL_NUMBER_OF_SET = 5

class IntervalTimerViewModel(private val timer: Timer) : ObservableViewModel() {

    val timePerWorkSet = ObservableInt(INITIAL_SECONDS_PER_WORK_SET * 10)
    val timePerRestSet = ObservableInt(INITIAL_SECONDS_PER_REST_SET * 10)
    val workTimeLeft = ObservableInt(timePerWorkSet.get())
    val restTimeLeft = ObservableInt(timePerRestSet.get())

    private var mNumberOfSetsTotal = INITIAL_NUMBER_OF_SET
    private var mNumberOfSetsElapsed = 0
    private var mState = TimerStates.STOPPED
    private var mStage = StartedStages.WORKING

    var timerRunning: Boolean
        @Bindable get() {
            return mState == TimerStates.STARTED
        }
        set(value) {
            if (value) startButtonClicked() else pauseButtonClicked()
        }

    var numberOfSets: Array<Int> = emptyArray()
        @Bindable get() {
            return arrayOf(mNumberOfSetsElapsed, mNumberOfSetsTotal)
        }
        set(value) {
            val newTotal = value[1]
            if (newTotal == numberOfSets[1]) return
            if (newTotal != 0 && newTotal > mNumberOfSetsElapsed) {
                field = value
                mNumberOfSetsTotal = newTotal
            }

            notifyPropertyChangeed(BR.numberOfSets)
        }

    val inWorkingStage: Boolean
        @Bindable get() {
            return mStage == StartedStages.WORKING
        }

    fun timePerRestSetChanged(newValue: CharSequence) {
        try {
            timePerRestSet.set(cleanSecondsString(newValue.toString()))
        } catch (e: NumberFormatException) {
            return
        }

        if (!isRestTimeAndRunning()) {
            restTimeLeft.set(timePerRestSet.get())
        }
    }

    fun timePerWorkSetChanged(newValue: CharSequence) {
        try {
            timePerWorkSet.set(cleanSecondsString(newValue.toString()))
        } catch (e: NumberFormatException) {
            return
        }

        if (!timerRunning) {
            workTimeLeft.set(timePerWorkSet.get())
        }
    }

    fun restTimeIncrease() = timePerSetIncrease(timePerRestSet, 1)

    fun workTimeIncrease() = timePerSetIncrease(timePerWorkSet, 1)

    fun setsIncrease() {
        mNumberOfSetsTotal += 1
        notifyPropertyChangeed(BR.numberOfSets)
    }

    fun restTimeDecrease() = timePerSetIncrease(timePerRestSet, -1)

    fun workTimeDecrease() = timePerSetIncrease(timePerWorkSet, -1)

    fun setsDecrease() {
        if (mNumberOfSetsTotal > mNumberOfSetsElapsed + 1) {
            mNumberOfSetsTotal -= 1
            notifyPropertyChangeed(BR.numberOfSets)
        }
    }

    fun stopButtonClicked() {
        resetTimers()
        mNumberOfSetsElapsed = 0
        mState = TimerStates.STOPPED
        mStage = StartedStages.WORKING
        timer.reset()

        notifyPropertyChangeed(BR.timerRunning)
        notifyPropertyChangeed(BR.inWorkingStage)
        notifyPropertyChangeed(BR.numberOfSets)
    }

    private fun timePerSetIncrease(timePerSet: ObservableInt, sign: Int = 1, min: Int = 0) {
        if (timePerSet.get() < 10 && sign < 0) return

        roundTimeIncrease(timePerSet, sign, min)

        if (mState == TimerStates.STOPPED) {
            workTimeLeft.set(timePerWorkSet.get())
            restTimeLeft.set(timePerRestSet.get())
        } else {
            updateCountdowns()
        }
    }

    private fun roundTimeIncrease(timePerSet: ObservableInt, sign: Int, min: Int) {
        val currentValue = timePerSet.get()
        val newValue =
                when {
                    currentValue < 100 -> timePerSet.get() + sign * 10
                    currentValue < 600 -> (round(currentValue / 50.0) * 50 + (50 * sign)).toInt()
                    else -> (round(currentValue / 100.0) * 100 + (100 * sign)).toInt()
                }

        timePerSet.set(newValue.coerceAtLeast(min))
    }

    private fun startButtonClicked() {
        when (mState) {
            TimerStates.PAUSED -> {
                pausedToStarted()
            }
            TimerStates.STOPPED -> {
                stoppedToStarted()
            }
            TimerStates.STARTED -> {

            }
        }

        val task = object : TimerTask() {
            override fun run() {
                if (mState == TimerStates.STARTED) {
                    updateCountdowns()
                }
            }

        }

        timer.start(task)
    }

    private fun pauseButtonClicked() {
        if (mState == TimerStates.STARTED) {
            startedToPaused()
        }

        notifyPropertyChangeed(BR.timerRunning)
    }

    private fun stoppedToStarted() {
        timer.resetStartTime()
        mState = TimerStates.STARTED
        mStage = StartedStages.WORKING

        notifyPropertyChangeed(BR.inWorkingStage)
        notifyPropertyChangeed(BR.timerRunning)
    }

    private fun pausedToStarted() {
        timer.updatePausedTime()
        mState = TimerStates.STARTED

        notifyPropertyChangeed(BR.timerRunning)
    }

    private fun startedToPaused() {
        mState = TimerStates.PAUSED
        timer.resetPauseTime()
    }

    private fun updateCountdowns() {
        if (mState == TimerStates.STOPPED) {
            resetTimers()
            return
        }

        val elapsed = if (mState == TimerStates.PAUSED) {
            timer.getPausedTime()
        } else {
            timer.getElapsedTime()
        }

        if (mState == StartedStages.RESTING) {
            updateRestCountdowns(elapsed)
        } else {
            updateWorkCountdowns(elapsed)
        }
    }

    private fun updateWorkCountdowns(elapsed: Long) {
        mStage = StartedStages.WORKING

        val newTimerLeft = timePerWorkSet.get() - (elapsed / 100).toInt()
        if (newTimerLeft <= 0) {
            workoutFinished()
        }
        workTimeLeft.set(newTimerLeft.coerceAtLeast(0))
    }

    private fun updateRestCountdowns(elapsed: Long) {
        val newRestTimeLeft = timePerRestSet.get() - (elapsed / 100).toInt()
        restTimeLeft.set(newRestTimeLeft.coerceAtLeast(0))

        if (newRestTimeLeft <= 0) {
            mNumberOfSetsElapsed += 1
            resetTimers()

            if (mNumberOfSetsElapsed >= mNumberOfSetsTotal) {
                timerFinished()
            } else {
                setFinish()
            }
        }
    }

    private fun workoutFinished() {
        timer.resetStartTime()
        mStage = StartedStages.RESTING
        notifyPropertyChangeed(BR.inWorkingStage)
    }

    private fun setFinish() {
        timer.resetStartTime()
        mStage = StartedStages.WORKING

        notifyPropertyChangeed(BR.inWorkingStage)
        notifyPropertyChangeed(BR.numberOfSets)
    }

    private fun timerFinished() {
        mState = TimerStates.STOPPED
        mStage = StartedStages.WORKING

        timer.reset()
        notifyPropertyChangeed(BR.timerRunning)
        mNumberOfSetsElapsed = 0
    }

    private fun resetTimers() {
        workTimeLeft.set(timePerWorkSet.get())
        restTimeLeft.set(timePerRestSet.get())
    }

    private operator fun ObservableInt.plusAssign(value: Int) {
        set(get() + value)
    }

    private operator fun ObservableInt.minusAssign(amount: Int) {
        plusAssign(-amount)
    }

    private fun isRestTimeAndRunning():Boolean {
        return (mState == TimerStates.PAUSED || mState == TimerStates.STARTED) && workTimeLeft.get() == 0
    }


}

enum class TimerStates { STOPPED, STARTED, PAUSED }

enum class StartedStages { WORKING, RESTING }