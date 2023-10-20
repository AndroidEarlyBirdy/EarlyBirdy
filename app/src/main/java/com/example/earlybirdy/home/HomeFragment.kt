package com.example.earlybirdy.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.earlybirdy.create_plan.CreatePlanActivity
import com.example.earlybirdy.data.MyGoal
import com.example.earlybirdy.databinding.FragmentHomeBinding
import com.example.earlybirdy.databinding.ItemTodoMainBinding
import com.example.earlybirdy.util.navigateToAlarmActivity
import com.example.earlybirdy.main.MainActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.UUID


class HomeFragment : Fragment() {

    //파이어스토어
    var firestore: FirebaseFirestore? = null
    private val currentUser = com.google.firebase.ktx.Firebase.auth.currentUser?.uid


    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val dateFormat = SimpleDateFormat("h:mm a", Locale.US)

    var attendindex: String? = null
    //private lateinit var homeViewModel: HomeViewModel

    // SharedPreferences를 사용하여 출석 여부를 저장하는 상수
    private val PREF_NAME = "ButtonPress"
    private val PREF_KEY_HAS_ATTENDED = "HasAttended"


    private lateinit var adapter: HomeFragmentAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        // 파이어스토어 인스턴스 초기화
        firestore = FirebaseFirestore.getInstance()

        adapter = HomeFragmentAdapter()
        binding.rvTodoMain.adapter = adapter
        binding.rvTodoMain.layoutManager =
            LinearLayoutManager(requireContext()).also { it.orientation = LinearLayoutManager.HORIZONTAL } //리사이클러뷰 가로로

        // 데이터를 불러오는 코드를 onCreateView 내에서 실행
        loadDataFromFirestore()
        loadTimeDate()

        binding.ivGoAlarm.setOnClickListener {
            navigateToAlarmActivity(this.requireActivity())
        }

        binding.btnCreate.setOnClickListener {
            val intent = Intent(requireContext(), CreatePlanActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }
    inner class HomeFragmentAdapter : RecyclerView.Adapter<HomeFragmentAdapter.ViewHolder>() {
        private var list = ArrayList<MyGoal>()

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): HomeFragmentAdapter.ViewHolder {
            return ViewHolder(
                ItemTodoMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }

        override fun onBindViewHolder(holder: HomeFragmentAdapter.ViewHolder, position: Int) {
            val item = list[position]
            holder.bind(item)
        }

        override fun getItemCount(): Int {
            val itemCount = list.size
            binding.tvEmptyListMessage.visibility = if (itemCount == 0) View.VISIBLE else View.GONE
            return itemCount
        }

        inner class ViewHolder(private val binding : ItemTodoMainBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(item: MyGoal) = with(binding) {
                tvTodo.text = item.title
                checkBox.isChecked= item.check

                // 체크박스 클릭 시 상태 업데이트
                binding.checkBox.setOnClickListener {
                    val checked = checkBox.isChecked
                    firestore?.collection("UserDto")?.document("vlKOuWtxe1b6flDCwHoPRwOYsWt2")
                        ?.collection("MyGoal")?.document("${item.goalId}")
                        ?.update("check", checked)
                        ?.addOnSuccessListener {
                            Log.d("체크 업데이트", "성공")
                        }
                        ?.addOnFailureListener {
                            Log.d("체크 업데이트", "실패")
                        }

                    // 경험치 업데이트
                    val experienceChange = if (checked) 10 else -10
                    firestore?.collection("UserDto")?.document("vlKOuWtxe1b6flDCwHoPRwOYsWt2")
                        ?.update("experience", FieldValue.increment(experienceChange.toLong()))
                        ?.addOnSuccessListener {
                            Log.d("경험치 업데이트", "성공")
                        }
                        ?.addOnFailureListener {
                            Log.d("경험치 업데이트", "실패")
                        }

                }
            }
        }

        fun addToList(item: MyGoal) {
            this.list.add(item)
        }
        fun clearList() {
            this.list.clear()
        }

    }
    private fun loadDataFromFirestore() {
        // 오늘의 날짜를 구합니다.
        val today = Calendar.getInstance()
        today.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        val year = today.get(Calendar.YEAR)
        val month = today.get(Calendar.MONTH)
        val day = today.get(Calendar.DAY_OF_MONTH)
        today.set(year, month, day, 0, 0, 0) // 날짜의 시작 부분 설정
        val startOfDay = today.time
        today.set(year, month, day, 23, 59, 59) // 날짜의 끝 부분 설정
        val endOfDay = today.time

        firestore?.collection("UserDto")?.document("vlKOuWtxe1b6flDCwHoPRwOYsWt2")
            ?.collection("MyGoal")
            ?.whereGreaterThanOrEqualTo("date", startOfDay)
            ?.whereLessThanOrEqualTo("date", endOfDay)
            ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                // ArrayList 비워줌
                adapter.clearList()

                for (snapshot in querySnapshot!!.documents) {
                    var item = snapshot.toObject(MyGoal::class.java)
                    if (item != null) {
                        adapter.addToList(item)
                    }
                }
                adapter.notifyDataSetChanged()
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("확인", binding.btnAttend.isEnabled.toString())
        // SharedPreferences를 사용하여 출석 여부를 확인하고 처리
        //val hasAttended = loadHasAttended()
       // if (!hasAttended) {
            binding.btnAttend.setOnClickListener {
                val alarmTime = loadTimeDate()
                if (alarmTime != null) {
                    val currentTime = getCurrentTime()
                    val currentMinutes = convertToMinutes(currentTime)
                    val alarmMinutes = convertToMinutes(alarmTime)

                    val data = calculateProgress(alarmMinutes, currentMinutes)
                    firestore?.collection("UserDto")?.document("vlKOuWtxe1b6flDCwHoPRwOYsWt2")
                        ?.update("experience", FieldValue.increment(data.toLong()))
                        ?.addOnSuccessListener {
                            Log.d("성공", data.toString())
                        }
                        ?.addOnFailureListener { }


                    // homeViewModel.setSharedData(data)

                    val nowTime = System.currentTimeMillis()
                    val timeFormatter = SimpleDateFormat("yyyy.MM.dd")
                    val dateTime = timeFormatter.format(nowTime)
                    attendindex = UUID.randomUUID().toString()

                    firestore?.collection("UserDto")?.document("vlKOuWtxe1b6flDCwHoPRwOYsWt2")
                        ?.collection("Attendance")?.document("$attendindex")
                        ?.set(
                            hashMapOf(
                                "AttendanceId" to attendindex,
                                "Date" to dateTime.toString()
                            )
                        )

                    saveHasAttended(true)
                }
            }




    }
    private fun loadHasAttended(): Boolean {
        val preferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return preferences.getBoolean(PREF_KEY_HAS_ATTENDED, false)
    }

    private fun saveHasAttended(hasAttended: Boolean) {
        val preferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putBoolean(PREF_KEY_HAS_ATTENDED, hasAttended)
        editor.apply()
    }

    @SuppressLint("SetTextI18n")
    private fun loadTimeDate(): String? {
        val pref: SharedPreferences = requireContext().getSharedPreferences("alarmTime", Context.MODE_PRIVATE)
        val hour = pref.getInt("hour", 0)
        val minute = pref.getInt("min", 0)
        val isAM = hour < 12

        val formattedHour = if (isAM) hour else hour - 12
        return String.format(Locale.US, "%d:%02d %s", formattedHour, minute, if (isAM) "AM" else "")
    }

    @SuppressLint("SetTextI18n")
    private fun updateAlarmTime() {
        val alarmTime = loadTimeDate()

        if (alarmTime != null) {
            binding.tvAlarm.text = alarmTime
            Log.d("AlarmTime", alarmTime)
        }
    }

    private fun getCurrentTime(): String {
        val calendar = Calendar.getInstance()
        return dateFormat.format(calendar.time)
    }

    private fun convertToMinutes(time: String): Int {
        val date = dateFormat.parse(time)
        val calendar = Calendar.getInstance()
        date?.let { calendar.time = it }
        return calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
    }

    private fun calculateProgress(alarmMinutes: Int, currentMinutes: Int): Int {
        val timeDifference = currentMinutes - alarmMinutes

        return when {
            timeDifference in 0..5 -> 30
            timeDifference in 6..10 -> 20
            timeDifference in 11..15 -> 10
            else -> 1
        }
    }

    override fun onResume() {
        super.onResume()
        updateAlarmTime()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}
