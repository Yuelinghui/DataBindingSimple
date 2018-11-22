package com.example.yuelinghui.databindingsimple.ui

import android.content.Context
import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter
import android.databinding.InverseBindingListener
import android.databinding.InverseMethod
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.example.yuelinghui.databindingsimple.R

object NumberOfSetsBindingAdapter {

    @BindingAdapter("numberOfSets")
    @JvmStatic
    fun setNumberOfSets(view: EditText, value: String) {
        view.setText(value)
    }

    @InverseBindingAdapter(attribute = "numberOfSets")
    @JvmStatic
    fun getNumberOfSets(view: EditText): String {
        return view.text.toString()
    }

    @BindingAdapter("numberOfSetsAttrChanged")
    @JvmStatic
    fun setListener(view: EditText, listener: InverseBindingListener?) {
        view.onFocusChangeListener = View.OnFocusChangeListener { focusView, hasFocus ->

            val textView = focusView as TextView

            if (hasFocus) {
                textView.text = ""
            } else {
                listener?.onChange()
            }

        }
    }

    @BindingAdapter("numberOfSets_alternative")
    @JvmStatic
    fun setNumberOfSets_alternative(view: EditText, value: Array<Int>) {
        view.setText(String.format(view.resources.getString(R.string.sets_format), value[0] + 1, value[1]))
    }

    @InverseBindingAdapter(attribute = "numberOfSets_alternative")
    @JvmStatic
    fun getNumberOfSets_alternative(editText: EditText): Array<Int> {
        if (editText.text.isEmpty()) {
            return arrayOf(0, 0)
        }
        return try {
            arrayOf(0, editText.text.toString().toInt())
        } catch (e: NumberFormatException) {
            arrayOf(0, 0)
        }
    }
}

object NumberOfSetsConverters {

    @InverseMethod("stringToSetArray")
    @JvmStatic
    fun setArrayToString(context: Context, value: Array<Int>): String {
        return context.getString(R.string.sets_format, value[0] + 1, value[1])
    }

    @JvmStatic
    fun stringToSetArray(context: Context, value: String): Array<Int> {
        if (value.isEmpty()) {
            return arrayOf(0, 0)
        }
        return try {
            arrayOf(0, value.toInt())
        } catch (e: NumberFormatException) {
            arrayOf(0, 0)
        }
    }


}