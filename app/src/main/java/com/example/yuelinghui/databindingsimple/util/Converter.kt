@file:JvmName("Converter")

package com.example.yuelinghui.databindingsimple.util

import kotlin.math.round

fun fromTenthsToSeconds(tenths: Int): String {
    return if (tenths < 600) {
        String.format("%.1f", tenths / 10.0)
    } else {
        val minutes = (tenths / 10) / 60
        val second = (tenths / 10) % 60
        String.format("%d:%02d",minutes,second)
    }
}

fun cleanSecondsString(seconds:String):Int {
    val fileteredValue = seconds.replace(Regex("""[^\d:.]"""),"")

    if (fileteredValue.isEmpty()) return 0

    val elements:List<Int> = fileteredValue.split(":").map {
        round(it.toDouble()).toInt()
    }

    var result:Int

    return when {
        elements.size > 2 -> 0
        elements.size > 1 -> {
            result = elements[0] * 60
            result+= elements[1]
            result * 10
        }
        else -> elements[0]*10
    }
}