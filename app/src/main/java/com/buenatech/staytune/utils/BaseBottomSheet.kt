package com.buenatech.staytune.utils

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import com.buenatech.staytune.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheet(private val manager: FragmentManager)
    : BottomSheetDialogFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.setOnShowListener {
            val bottomSheetDialog = dialog as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout
            with(BottomSheetBehavior.from(bottomSheet)) {
                state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
            = BottomSheetDialog(requireContext(), theme)

    fun show() {
        if (!this.isAdded || !this.isVisible)
            show(manager, this::class.java.name)
    }

    inline fun show(sheet: BaseBottomSheet.() -> Unit) {
        this.sheet()
        this.show()
    }

}