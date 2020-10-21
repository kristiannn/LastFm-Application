package com.neno.lastfmapp.modules.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.neno.lastfmapp.R

class NotifyDialog(private val message: String) : DialogFragment()
{
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.dialog_layout, container, false)

        val dialog = dialog!!
        val button = view.findViewById<Button>(R.id.dialog_button)
        val textView = view.findViewById<TextView>(R.id.dialog_textView)

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        textView.text = message

        button.setOnClickListener {
            dialog.dismiss()
        }

        return view
    }
}