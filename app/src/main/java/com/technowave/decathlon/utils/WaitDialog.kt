package com.technowave.decathlon.utils
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.technowave.decathlon.R
import kotlin.concurrent.thread

class WaitDialog(context: Context) : MaterialAlertDialogBuilder(context) {
    private var waitDialogBox: Dialog

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_wait, null)
        setView(view)
        background = ColorDrawable(Color.TRANSPARENT)
        setCancelable(false)
        waitDialogBox = create()
    }

    fun showDialog() {
        waitDialogBox.show()
    }

    fun dismiss() {
        thread {
            Thread.sleep(500)
            waitDialogBox.dismiss()
        }
    }
}