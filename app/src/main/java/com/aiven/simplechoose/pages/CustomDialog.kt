package com.aiven.simplechoose.pages

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import com.aiven.simplechoose.R

open class CustomDialog<VB: ViewBinding>(
    context: Context,
    inflate: (layout: LayoutInflater) -> VB,
    style: Int = R.style.mydialog
) {
    private val dialog: AlertDialog
    var binding: VB = inflate(LayoutInflater.from(context))

    init {
        dialog = AlertDialog.Builder(context, style).setView(binding.root).create()
        dialog.window?.setWindowAnimations(R.style.DialogShowHideStyle)
    }

    fun setCancelable(flag: Boolean) = apply {
        dialog.setCancelable(flag)
    }

    fun show(flag: Boolean) {
        if (!dialog.isShowing) {
            dialog.setCancelable(flag)
            dialog.show()
        }
    }

    fun show() {
        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    fun hide() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }
}