package com.example.yuelinghui.databindingsimple.util

import java.util.*

interface Timer {
    fun reset()
    fun start(task:TimerTask)
    fun getElapsedTime():Long
    fun updatePausedTime()
    fun getPausedTime():Long
    fun resetStartTime()
    fun resetPauseTime()
}

object DefaultTimer:Timer {

    private const val TIMER_PERIOD_MS = 100L

    private var mStartTime = System.currentTimeMillis()

    private var mPauseTime = 0L

    private var timer = java.util.Timer()

    override fun reset() {
        timer.cancel()
    }

    override fun start(task: TimerTask) {
        timer = java.util.Timer()
        timer.scheduleAtFixedRate(task,0, TIMER_PERIOD_MS)
    }

    override fun getElapsedTime(): Long = System.currentTimeMillis() - mStartTime


    override fun updatePausedTime() {
        mStartTime+=System.currentTimeMillis() - mPauseTime
    }

    override fun getPausedTime(): Long = mPauseTime - mStartTime

    override fun resetStartTime() {
        mStartTime = System.currentTimeMillis()
    }

    override fun resetPauseTime() {
        mPauseTime = System.currentTimeMillis()
    }

}