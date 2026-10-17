package com.example.ergo_work

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

class StretchingGuideDialog : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_stretching_guide, container, false)

        val btnStartStretch = view.findViewById<MaterialButton>(R.id.btnStartStretch)
        btnStartStretch.setOnClickListener {
            Toast.makeText(requireContext(), "Peregangan dimulai... Tahan 30 detik.", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        return view
    }
}
