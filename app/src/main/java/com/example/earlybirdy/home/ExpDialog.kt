package com.example.earlybirdy.home

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.example.earlybirdy.databinding.DialogExpBinding

class ExpDialog (
    context: Context,
) : Dialog(context) {

    private lateinit var binding : DialogExpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogExpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }
    private fun initViews() = with(binding) {

    }
}
