package com.example.earlybirdy.board

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.earlybirdy.R
import com.example.earlybirdy.databinding.FragmentBoardBinding
import com.example.earlybirdy.my_page.MyPageFragment
import com.example.earlybirdy.util.navigateToCreatePlanActivity
import com.example.earlybirdy.util.navigateToEditProfileActivity
import com.example.earlybirdy.util.navigateToSigninActivity
import com.example.earlybirdy.util.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class BoardFragment : Fragment() {

    private var _binding: FragmentBoardBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBoardBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        val currentUser = auth.currentUser
        Log.d("user","${currentUser}")
        if(currentUser != null) {
            binding.tvTestUid.setText("${currentUser.uid}")

            binding.btnSignoutTest.setOnClickListener {
                signOut()
            }
        }else{
            binding.btnSigninTest.setOnClickListener {
                signin()
            }
        }
        binding.btnTest.setOnClickListener {
            navigateToSigninActivity(this.requireActivity())
        }


        binding.btnEditprofileTest.setOnClickListener {
            navigateToEditProfileActivity(this.requireActivity())
        }

        binding.btnCreateplanTest.setOnClickListener{
            navigateToCreatePlanActivity(this.requireActivity())
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        Log.d("resume","${auth.currentUser}")
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance() = BoardFragment()
    }

    private fun signin() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
        } else {
            navigateToSigninActivity(this.requireActivity())
        }
    }

    private fun signOut() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            auth.signOut()
            navigateToSigninActivity(this.requireActivity())
        }
    }
}

