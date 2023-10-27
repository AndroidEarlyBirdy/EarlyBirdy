package com.example.earlybirdy.board.board_main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.earlybirdy.databinding.FragmentBoardBinding
import com.example.earlybirdy.dto.BoardDto
import com.example.earlybirdy.util.navigateToBoardWriteActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BoardFragment : Fragment() {

    private var _binding: FragmentBoardBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    val db = Firebase.firestore

    private val fireStore = FirebaseFirestore.getInstance()

    // RecyclerView 가 불러올 목록
    private var adapter: BoardAdapter? = null
    private val data: MutableList<BoardDto> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBoardBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        setOnClickListener()

        adapter = BoardAdapter()
        binding.rvCommunity.adapter = adapter
        binding.rvCommunity.layoutManager = LinearLayoutManager(requireContext())

        loadData()

        return binding.root
    }


    private fun setOnClickListener() {
        binding.fbtnCreateContants.setOnClickListener {
            navigateToBoardWriteActivity(requireContext())
        }
    }

    private fun loadData() {
        fireStore.collection("BoardDto").get()
            .addOnSuccessListener { value ->
                adapter?.clearList()
                data.clear()
                for (i in value!!. documents){
                    var item = i.toObject(BoardDto::class.java)
                    if (item != null) {
                        val boardItam = BoardDto(
                            item.uid,
                            item.writer,
                            item.contentsTitle,
                            item.contents
                        )
                        data.add(boardItam)
                        Log.d("board", boardItam.toString())
                    }
                }
                adapter!!.addItems(data)
            }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance() = BoardFragment()
    }
}

