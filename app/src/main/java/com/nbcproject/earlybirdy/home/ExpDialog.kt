package com.nbcproject.earlybirdy.home

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.nbcproject.earlybirdy.databinding.DialogExpBinding

class ExpDialog (
    context: Context,
) : Dialog(context) {

    private lateinit var binding : DialogExpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogExpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
    private fun initViews() = with(binding) {

        binding.tvFhdExp2.text = ""

        binding.icFhdShare.setOnClickListener {
            val sendText = "share test"
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, sendText)
            sendIntent.type = "text/plain"
            context.startActivity(Intent.createChooser(sendIntent, "Share"))
        }

        binding.btnFhdOk.setOnClickListener {
            dismiss()
        }
    }
}
