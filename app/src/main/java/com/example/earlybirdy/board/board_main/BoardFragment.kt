package com.example.earlybirdy.board.board_main

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.earlybirdy.board.board_read.BoardReadActivity
import com.example.earlybirdy.databinding.FragmentBoardBinding
import com.example.earlybirdy.dto.BoardDto
import com.example.earlybirdy.util.navigateToBoardWriteActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class BoardFragment : Fragment() {

    private var _binding: FragmentBoardBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private val fireStore = FirebaseFirestore.getInstance()

    private val data: MutableList<BoardDto> = mutableListOf()

    private val boardAdapter by lazy {
        BoardAdapter(bContext)
    }

    private lateinit var bContext: Context
    private lateinit var bmanager: LinearLayoutManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        bContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBoardBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        initView()
        setOnClickListener()

        return binding.root
    }

    private fun initView() = with(binding) {

        loadData()

        bmanager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rvCommunity.layoutManager = bmanager
        rvCommunity.adapter = boardAdapter

        boardAdapter.itemClick = object : BoardAdapter.ItemClick {
            override fun onClick(view: View, data: BoardDto) {
                startActivity(BoardReadActivity.BoardReadIntent(context, data))
            }

            override fun deleteItem(view: View, boardData: BoardDto) {
                deleteData(boardData)
                loadData()
            }
        }

    }

    private fun setOnClickListener() {
        binding.btnCreatContents.setOnClickListener {
            navigateToBoardWriteActivity(requireContext())
        }

        binding.icReload.setOnClickListener {
            loadData()
        }
    }

    private fun loadData() {
        fireStore.collection("BoardDto").get()
            .addOnSuccessListener { value ->
                boardAdapter.clearList()
                data.clear()
                for (i in value!!.documents) {
                    var item = i.toObject(BoardDto::class.java)
                    if (item != null) {
                        val boardItam = BoardDto(
                            item.bid,
                            item.uid,
                            item.writer,
                            item.createdTime,
                            item.contentsTitle,
                            item.contents,
                            item.contentsPoto
                        )
                        data.add(boardItam)
                        Log.d("board", boardItam.toString())
                    }
                }
                boardAdapter.addItems(data)
            }
    }

    private fun deleteData(boardData: BoardDto) {
        val user = auth.currentUser
        if (user!!.uid == boardData.uid){
            fireStore.collection("BoardDto").document(boardData.bid).delete()
                .addOnSuccessListener {
                    data.remove(boardData)
                }
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

