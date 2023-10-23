package com.example.earlybirdy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.earlybirdy.data.MyGoal
import com.example.earlybirdy.databinding.FragmentRankingBinding
import com.example.earlybirdy.dto.UserDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class RankingFragment : Fragment() {
    // 파이어스토어
    var auth : FirebaseAuth? = null
    var firestore : FirebaseFirestore? = null
    var user : FirebaseUser? = null

    private var _binding: FragmentRankingBinding? = null
    private val binding get() = _binding!!
    private var userList = ArrayList<UserDto>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRankingBinding.inflate(inflater, container, false)

        // 파이어베이스 인증 객체
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        // 파이어스토어 인스턴스 초기화
        firestore = FirebaseFirestore.getInstance()

        val rankingAdapter = RankingAdapter(userList)
        binding.rvRanking.adapter = rankingAdapter
        binding.rvRanking.layoutManager = LinearLayoutManager(context)

        firestore?.collection("UserDto")
            ?.orderBy("exp", Query.Direction.DESCENDING)
            ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    // 에러 처리
                    return@addSnapshotListener
                }

                val userList = ArrayList<UserDto>()
                for (document in querySnapshot!!) {
                    val uid = document.id
                    val profile = document.get("profile") as? Int ?: R.drawable.img_profile_add
                    val nickname = document.getString("nickname") ?: ""
                    val email = document.getString("email") ?: ""
                    val exp = document.getLong("exp")?.toInt() ?: 0

                    val user = UserDto(uid, profile, nickname, email, exp)
                    userList.add(user)
                }

                // 상위 3명의 사용자를 이미지뷰와 TextView에 설정
                if (userList.size >= 1) {
                    binding.iv1st.setImageResource(userList[0].profile ?: R.drawable.img_profile_add)
                    binding.tv1st.text = userList[0].nickname
                }
                if (userList.size >= 2) {
                    binding.iv2nd.setImageResource(userList[1].profile ?: R.drawable.img_profile_add)
                    binding.tv2nd.text = userList[1].nickname
                }
                if (userList.size >= 3) {
                    binding.iv3rd.setImageResource(userList[2].profile ?: R.drawable.img_profile_add)
                    binding.tv3rd.text = userList[2].nickname
                }

                if (userList.size >= 4) {
                    val rankingAdapter = RankingAdapter(userList.subList(3, userList.size))
                    binding.rvRanking.adapter = rankingAdapter
                } else {
                    // 사용자가 3명 이하일 때 처리
                    val emptyList = ArrayList<UserDto>()
                    val rankingAdapter = RankingAdapter(emptyList)
                    binding.rvRanking.adapter = rankingAdapter
                }
            }
        return binding.root
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance() = RankingFragment()
    }

}

