package com.example.earlybirdy.edit_profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.example.earlybirdy.databinding.ActivityEditProfileBinding
import com.example.earlybirdy.util.showToast

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etProfilePassword.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (binding.etProfilePassword.getText().toString().equals(binding.etProfilePasswordCheck.getText().toString())){
                    binding.clProfileSave.isEnabled= true
                } else {
                    binding.clProfileSave.isEnabled=false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                TODO("Not yet implemented")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.etProfilePassword.getText().toString().equals(binding.etProfilePasswordCheck.getText().toString())){
                    binding.clProfileSave.isEnabled= true
                } else {
                    binding.clProfileSave.isEnabled=false
                }
            }
        })
    }
    fun onSaveButtonClick(view: View) {
        val name = binding.etProfileEmail.text.toString()
        val mail = binding.etProfileEmail.text.toString()
        val pw = binding.etProfilePassword.toString()
        val pwc = binding.etProfilePasswordCheck.toString()

        if(name.isBlank() || mail.isBlank() || pw.isBlank() || pwc.isBlank() ) {
            showToast("입력되지 않은 정보가 있습니다.")
        } else {

        }

    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}