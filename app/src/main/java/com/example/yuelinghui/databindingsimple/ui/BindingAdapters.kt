package com.example.yuelinghui.databindingsimple.ui

import android.content.Context
import android.databinding.BindingAdapter
import android.os.Build
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import com.example.yuelinghui.databindingsimple.R

object BindingAdapters {


    @BindingAdapter("clearOnFocusAndDispatch")
    @JvmStatic fun clearOnFocusAndDispatch(view:EditText,listener: View.OnFocusChangeListener?) {
        view.onFocusChangeListener = View.OnFocusChangeListener{focusedView,hasFocus ->
            val textView = focusedView as TextView

            if (hasFocus) {
                view.setTag(R.id.previous_value,textView.text)
                textView.text = ""
            } else {
                if (textView.text.isEmpty()) {
                    val tag = textView.getTag(R.id.previous_value) as CharSequence?
                    textView.text = tag?:""
                }

                listener?.onFocusChange(focusedView,hasFocus)
            }
        }
    }

    @BindingAdapter("clearTextOnFocus")
    @JvmStatic fun EditText.clearTextOnFocus(enable:Boolean) {
        if (enable) {
            clearOnFocusAndDispatch(this,null)
        } else {
            this.onFocusChangeListener = null
        }
    }

    @BindingAdapter("hideKeyboardOnInputDone")
    @JvmStatic fun hideKeyboardOnInputDone(view: EditText,enable: Boolean) {
        if (!enable) return

        val listener = TextView.OnEditorActionListener{_,actionId,_->

            if (actionId == EditorInfo.IME_ACTION_DONE) {
                view.clearFocus()
                val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken,0)
            }
            false
        }
        view.setOnEditorActionListener(listener)
    }

    @BindingAdapter("invisibleUnless")
    @JvmStatic fun invisibleUnless(view:View,visible:Boolean) {

        view.visibility = if (visible)View.VISIBLE else View.INVISIBLE
    }

    @BindingAdapter("goneUnless")
    @JvmStatic fun goneUnless(view:View,visible: Boolean) {
        view.visibility = if (visible)View.VISIBLE else View.GONE
    }

    @BindingAdapter(value = ["android:max","android:progress"],requireAll = true)
    @JvmStatic fun updateProgress(progressBar: ProgressBar,max:Int,progress:Int) {
        progressBar.max = max
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            progressBar.setProgress(progress,false)
        } else {
            progressBar.progress = progress
        }
    }

    @BindingAdapter("loseFocusWhen")
    @JvmStatic fun loseFocusWhen(view: EditText,condition:Boolean) {
        if (condition)view.clearFocus()
    }
}