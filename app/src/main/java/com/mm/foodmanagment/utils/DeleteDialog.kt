package com.mm.foodmanagment.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil.inflate
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mm.foodmanagment.R
import kotlinx.android.synthetic.main.dialog_delete.*


fun Context.showDeleteDialog(
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
) {
    val dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
    dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.window?.setLayout(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.MATCH_PARENT
    )
//    dialog.window?.setContentView(R.layout.dialog_delete)
//    val binding = LayoutInflater.from(this).inflate(
//        LayoutInflater.from(this), R.layout.dialog_delete, null,
//        false
//    )
//    dialog.setContentView(binding.root)
    dialog.setCancelable(true)
//    val bottomSheetInternal: View = dialog.findViewById()
//    val view: View = inflate.inflate(R.layout.dialog_delete, null)
    dialog.setContentView(R.layout.dialog_delete)

//    val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(view.parent as View)
//    val behavior = BottomSheetBehavior.from(dialog.parent as View)
//    behavior.state = BottomSheetBehavior.STATE_EXPANDED
    dialog.show()
    dialog.setOnDismissListener { onDismiss() }
    dialog.delete?.setOnClickListener {
        onDelete()
        dialog.dismiss()
    }
    dialog.cancel?.setOnClickListener {
        onDismiss()
        dialog.dismiss()
    }
}