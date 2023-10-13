package com.example.earlybirdy.edit_profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.example.earlybirdy.R
import com.example.earlybirdy.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvEditSave.setOnClickListener()

        binding.etProfilePassword.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (binding.etProfilePassword.getText().toString().equals(binding.etProfilePasswordCheck.getText().toString())){

                } else {

                }

            }
        })
    }
}