package com.example.earlybirdy.community

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.earlybirdy.data.CommunityData
import com.example.earlybirdy.data.MyGoal
import com.example.earlybirdy.data.Todo
import com.example.earlybirdy.databinding.FragmentCommunityMainBinding
import com.example.earlybirdy.dto.CommunityDto
import com.example.earlybirdy.home.HomeFragment
import com.example.earlybirdy.util.navigateToCommunityWriteActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson


class CommunityMainFragment : Fragment() {
    private var _binding: FragmentCommunityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    val db = Firebase.firestore

    private val fireStore = FirebaseFirestore.getInstance()

    // RecyclerView 가 불러올 목록
    private var adapter: CommunityAdapter? = null
    private val data: MutableList<CommunityDto> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommunityMainBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        setOnClickListener()

        adapter = CommunityAdapter()
        binding.rvCommunity.adapter = adapter
        binding.rvCommunity.layoutManager = LinearLayoutManager(requireContext())

        loadData()

        return binding.root
    }

    private fun setOnClickListener() {
        binding.fbtnCreateContants.setOnClickListener {
            navigateToCommunityWriteActivity(requireContext())
        }
    }

    private fun loadData() {
        fireStore.collection("CommunityDto").get()
            .addOnSuccessListener { value ->
                adapter?.clearList()
                data.clear()
                for (i in value!!. documents){
                    var item = i.toObject(CommunityDto::class.java)
                    if (item != null) {
                        val communityItam = CommunityDto(
                            item.uid,
                            item.writer,
                            item.contentsTitle,
                            item.contents
                        )
                        data.add(communityItam)
                        Log.d("todo", communityItam.toString())
                    }
                }
                adapter!!.addItems(data)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = CommunityMainFragment()
    }
}