package com.example.earlybirdy

import android.os.Bundle
import android.util.Log
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
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var user: FirebaseUser? = null

    private var _binding: FragmentRankingBinding? = null
    private val binding get() = _binding!!
    private var userList = ArrayList<UserDto>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRankingBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        firestore = FirebaseFirestore.getInstance()

        val rankingAdapter = RankingAdapter(userList)
        binding.rvRanking.adapter = rankingAdapter
        binding.rvRanking.layoutManager = LinearLayoutManager(context)

        firestore?.collection("UserDto")
            ?.orderBy("exp", Query.Direction.DESCENDING)
            ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    return@addSnapshotListener
                }

                val userList = ArrayList<UserDto>()
                for ((index, document) in querySnapshot?.withIndex()!!) {
                    val uid = document.id
                    val profile = document.get("profile") as? Int ?: R.drawable.img_profile_add
                    val nickname = document.getString("nickname") ?: ""
                    val email = document.getString("email") ?: ""
                    val exp = document.getLong("exp")?.toInt() ?: 0

                    val user = UserDto(uid, profile, nickname, email, exp)
                    userList.add(user)
                    Log.d("아아아 이름 되냐고로로",nickname)


                    val currentUserUid = user?.uid
                    // 나의 등수를 나타내는 처리
                    if (currentUserUid == uid)  {
                        binding.tvMyProfile.setImageResource(user.profile ?: R.drawable.img_profile_add)
                        binding.tvMyExp.text = "${user.exp}"
                        binding.tvMyName.text = user.nickname
                        binding.tvMyRank.text = "${index + 1}등"
                        Log.d("유저 이름",user.nickname)
                    }
                }

                // 상위 3명의 사용자를 이미지뷰와 TextView에 설정
                for (i in 0 until 3) {
                    if (userList.size > i) {
                        when (i) {
                            0 -> {
                                binding.iv1st.setImageResource(userList[i].profile ?: R.drawable.img_profile_add)
                                binding.tv1st.text = userList[i].nickname
                            }
                            1 -> {
                                binding.iv2nd.setImageResource(userList[i].profile ?: R.drawable.img_profile_add)
                                binding.tv2nd.text = userList[i].nickname
                            }
                            2 -> {
                                binding.iv3rd.setImageResource(userList[i].profile ?: R.drawable.img_profile_add)
                                binding.tv3rd.text = userList[i].nickname
                            }
                        }
                    }
                }

                // 상위 100명까지만 리사이클러뷰에 표시
                if (userList.size > 3) {
                    val rankingAdapter = RankingAdapter(userList.subList(3, minOf(userList.size, 103)))
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
