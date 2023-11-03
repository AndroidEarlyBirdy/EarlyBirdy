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
import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Objects

class RankingFragment : Fragment() {
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var currentuser: FirebaseUser? = null
    private var _binding: FragmentRankingBinding? = null
    private val binding get() = _binding!!
    private var userList = ArrayList<UserDto>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRankingBinding.inflate(inflater, container, false)
        val rootView = _binding?.root
        auth = FirebaseAuth.getInstance()
        currentuser = auth?.currentUser
        firestore = FirebaseFirestore.getInstance()
        val rankingAdapter = RankingAdapter(userList)
        _binding?.rvRanking?.adapter = rankingAdapter
        _binding?.rvRanking?.layoutManager = LinearLayoutManager(context)

        firestore?.collection("UserDto")
            ?.orderBy("exp", Query.Direction.DESCENDING)
            ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    return@addSnapshotListener
                }

                val userList = ArrayList<UserDto>()
                for ((index, document) in querySnapshot?.withIndex()!!) {
                    if (!document.exists()) continue
                    val uid = document.id

                    var profileValue = document.get("profile", Int::class.java) ?: 0
                    val profile = getProfileImage(profileValue)
                    Log.d("가져오기", (profileValue.toString()))
                    val nickname = document.getString("nickname") ?: ""
                    val email = document.getString("email") ?: ""
                    val exp = document.getLong("exp")?.toInt() ?: 0

                    Log.d("asdf", uid)
                    val user = UserDto(uid, profile, nickname, email, exp)
                    userList.add(user)
                    Log.d("아아아 이름 되냐고로로", nickname)


                    // 나의 등수를 나타내는 처리
                    if (user != null && user.uid == currentuser?.uid) {
                        binding.tvMyProfile.setImageResource(user.profile ?: 0)
                        Log.d("유저 프로필", user.profile.toString())
                        binding.tvMyExp.text = "${user.exp}"
                        binding.tvMyName.text = user.nickname
                        binding.tvMyRank.text = "${index + 1}등"
                        Log.d("유저 이름", user.nickname)
                    }
                }

                // 상위 3명의 사용자를 이미지뷰와 TextView에 설정
                for (i in 0 until 3) {
                    if (userList.size > i) {
                        when (i) {
                            0 -> {
                                binding.iv1st.setImageResource(userList[i].profile ?: 0)
                                binding.tv1st.text = userList[i].nickname
                                Log.d("프로필 불러오기", userList[i].profile.toString())
                            }

                            1 -> {
                                binding.iv2nd.setImageResource(userList[i].profile ?: 0)
                                binding.tv2nd.text = userList[i].nickname
                                Log.d("2번 프로필 불러오기", userList[i].profile.toString())
                            }

                            2 -> {
                                binding.iv3rd.setImageResource(userList[i].profile ?: 0)
                                binding.tv3rd.text = userList[i].nickname
                            }
                        }
                    }
                }

                // 상위 100명까지만 리사이클러뷰에 표시
                if (userList.size > 3) {
                    val rankingAdapter =
                        RankingAdapter(userList.subList(3, minOf(userList.size, 103)))
                    binding.rvRanking.adapter = rankingAdapter
                } else {
                    // 사용자가 3명 이하일 때 처리
                    val emptyList = ArrayList<UserDto>()
                    val rankingAdapter = RankingAdapter(emptyList)
                    binding.rvRanking.adapter = rankingAdapter
                }
            }

        return rootView
    }

    private fun getProfileImage(profile: Int): Int {
        return when (profile) {
            1 -> R.drawable.img_profile_man1
            2 -> R.drawable.img_profile_woman1
            3 -> R.drawable.img_profile_man2
            4 -> R.drawable.img_profile_woman2
            else -> R.drawable.img_profile_add // 기본값 리소스 ID
        }

    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
    companion object {
        fun newInstance() = RankingFragment()
    }
}