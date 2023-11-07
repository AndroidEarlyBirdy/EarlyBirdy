package com.example.earlybirdy.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.earlybirdy.R
import com.example.earlybirdy.board.board_main.BoardAdapter
import com.example.earlybirdy.board.board_read.BoardReadActivity

import com.example.earlybirdy.create_plan.CreatePlanActivity
import com.example.earlybirdy.data.MyGoal
import com.example.earlybirdy.databinding.FragmentHomeBinding
import com.example.earlybirdy.databinding.ItemTodoMainBinding
import com.example.earlybirdy.dto.BoardDto
import com.example.earlybirdy.setting.SettingDeleteDialog
import com.example.earlybirdy.util.navigateToAlarmActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID


class HomeFragment : Fragment() {

    // 파이어스토어
    private var firestore: FirebaseFirestore? = null

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var alarmDialog: AlarmDialog

    private val dateFormat = SimpleDateFormat("h:mm a", Locale.US)
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var adapter: HomeFragmentAdapter
    private var completedGoals: Int = 0 // 완료된 목표 수를 추적
    private var completedAttendances: Int = 0 // 완료된 출석 수를 추적
    private var totalGoals: Int = 0 // 전체 목록 수를 추적

    var attendindex: String? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    @RequiresApi(Build.VERSION_CODES.O)
    val now = LocalDateTime.now()
    @RequiresApi(Build.VERSION_CODES.O)
    private val startOfDay = now.with(LocalTime.MIN)
    @RequiresApi(Build.VERSION_CODES.O)
    private val endOfDay = now.with(LocalTime.MAX)

    private lateinit var mLocationManager: LocationManager
    private lateinit var mLocationListener: LocationListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("이곳은", "onCreateView")
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        // 파이어스토어 인스턴스 초기화
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        alarmDialog = AlarmDialog(requireContext())

        adapter = HomeFragmentAdapter()
        binding.rvTodoMain.adapter = adapter
        binding.rvTodoMain.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false) // 리사이클러뷰 가로로

        // 데이터를 불러오는 코드를 onCreateView 내에서 실행
        loadDataFromFirestore()
        loadTimeDate()

        alarmDialog.loadTime = object : AlarmDialog.LoadTimeData {
            override fun loadTimeData() {
                updateAlarmTime()
            }
        }

        binding.ivGoAlarm.setOnClickListener {
            alarmDialog.show()
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

        inner class ViewHolder(private val binding: ItemTodoMainBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun bind(item: MyGoal) = with(binding) {
                tvTodo.text = item.title
                checkBox.isChecked = item.check

                // 체크박스 클릭 시 상태 업데이트
                binding.checkBox.setOnClickListener {
                    val checked = checkBox.isChecked
                    firestore?.collection("UserDto")?.document(user.uid)
                        ?.collection("MyGoal")?.document("${item.goalId}")
                        ?.update("check", checked)
                        ?.addOnSuccessListener {
                        }
                        ?.addOnFailureListener {
                        }

                    // 경험치 업데이트
                    val experienceChange = if (checked) 10 else -10
                    firestore?.collection("UserDto")?.document(user.uid)
                        ?.update("exp", FieldValue.increment(experienceChange.toLong()))
                        ?.addOnSuccessListener {
                        }
                        ?.addOnFailureListener {
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

        val timeFormatter = SimpleDateFormat("yyyy.MM.dd")
        val dateTime = timeFormatter.format(today.time)

        firestore?.collection("UserDto")?.document(user.uid)
            ?.collection("Attendance")
            ?.whereGreaterThanOrEqualTo("date", startOfDay)
            ?.whereLessThanOrEqualTo("date", endOfDay)
            ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                val hasAttended = querySnapshot?.documents?.isNotEmpty()
                if (hasAttended != null && hasAttended) {
                    completedAttendances = 1
                    binding.btnAttend.isEnabled = false
                } else {
                    completedAttendances = 0
                    binding.btnAttend.isEnabled = true
                }
                updateProgress()
                adapter.notifyDataSetChanged()
            }

        firestore?.collection("UserDto")?.document(user.uid)
            ?.collection("MyGoal")
            ?.whereGreaterThanOrEqualTo("date", startOfDay)
            ?.whereLessThanOrEqualTo("date", endOfDay)
            ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                // ArrayList 비워줌
                adapter.clearList()

                completedGoals = 0
                totalGoals = 0
                for (snapshot in querySnapshot!!.documents) {
                    var item = snapshot.toObject(MyGoal::class.java)
                    if (item != null) {
                        adapter.addToList(item)
                        if (item.check) {
                            completedGoals++
                        }
                        totalGoals++
                    }
                }

                if (totalGoals == 0) {
                    // RecyclerView 내용이 없을 때 화살표를 숨김
                    binding.btnLeftArrow.visibility = View.GONE
                    binding.btnRightArrow.visibility = View.GONE
                } else {
                    // RecyclerView 내용이 있을 때 화살표를 표시
                    binding.btnLeftArrow.visibility = View.VISIBLE
                    binding.btnRightArrow.visibility = View.VISIBLE
                }

                updateProgress()
                adapter.notifyDataSetChanged()
            }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

        val currentDate = getCurrentDate()
        val lastShownDate = sharedPreferences.getString("last_shown_date", "")

        // 현재 날짜와 마지막 표시 날짜를 비교하여, 다른 날에만 새 명언 표시
        if (currentDate != lastShownDate) {
            showRandomQuote()
            // 마지막 표시 날짜를 업데이트
            sharedPreferences.edit().putString("last_shown_date", currentDate).apply()
        }

        val leftArrowButton = binding.btnLeftArrow
        val rightArrowButton = binding.btnRightArrow
        val recyclerView = binding.rvTodoMain

        val layoutManager = recyclerView.layoutManager as LinearLayoutManager

        // 좌측 화살표 클릭 시 RecyclerView를 좌측으로 스크롤
        leftArrowButton.setOnClickListener {
            val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
            if (firstVisibleItem > 0) {
                recyclerView.smoothScrollToPosition(firstVisibleItem - 1)
            }
        }

        // 우측 화살표 클릭 시 RecyclerView를 우측으로 스크롤
        rightArrowButton.setOnClickListener {
            val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
            if (lastVisibleItem < adapter.itemCount - 1) {
                recyclerView.smoothScrollToPosition(lastVisibleItem + 1)
            }
        }

        // RecyclerView 스크롤 리스너를 추가하여 화살표 가시성을 업데이트
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // 처음 또는 끝에 도달하면 화살표 가시성 업데이트
                leftArrowButton.visibility = if (layoutManager.findFirstVisibleItemPosition() > 0) {
                    View.VISIBLE
                } else {
                    View.INVISIBLE
                }

                rightArrowButton.visibility = if (layoutManager.findLastVisibleItemPosition() < adapter.itemCount - 1) {
                    View.VISIBLE
                } else {
                    View.INVISIBLE
                }
            }
        })

        binding.btnAttend.setOnClickListener {
            val alarmTime = loadTimeDate()
            if (alarmTime != null) {
                val currentTime = getCurrentTime()
                val currentMinutes = convertToMinutes(currentTime)
                val alarmMinutes = convertToMinutes(alarmTime)
                val data = calculateTime(alarmMinutes, currentMinutes)

                firestore?.collection("UserDto")?.document(user.uid)
                    ?.update("exp", FieldValue.increment(data.toLong()))
                    ?.addOnSuccessListener {
                    }
                    ?.addOnFailureListener { }

                // homeViewModel.setSharedData(data)

                attendindex = UUID.randomUUID().toString()

                firestore?.collection("UserDto")?.document(user.uid)
                    ?.collection("Attendance")?.document("$attendindex")
                    ?.set(
                        hashMapOf(
                            "AttendanceId" to attendindex,
                            "date" to Timestamp.now(),
                        )
                    )
                // 출석 버튼을 비활성화
                binding.btnAttend.isEnabled = false
            }
        }
        // 버튼을 비활성화
        binding.btnAttend.isEnabled = false
    }

    private fun getCurrentDate(): String {
        val nowTime = System.currentTimeMillis()
        val timeZone = TimeZone.getTimeZone("GMT+9") // UTC+9
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일 a hh시 mm분 ss초 'UTC'Z", Locale.getDefault())
        dateFormat.timeZone = timeZone
        return dateFormat.format(Date(nowTime))
    }


    private fun showRandomQuote() {
        val quotes = resources.getStringArray(R.array.quotes)
        val randomIndex = (0 until quotes.size).random()
        val randomQuote = quotes[randomIndex]
        binding.tvFamous.text = randomQuote
    }

    @SuppressLint("SetTextI18n")
    private fun loadTimeDate(): String? {
        val pref: SharedPreferences =
            requireContext().getSharedPreferences("alarmSetting", Context.MODE_PRIVATE)
        val hour = pref.getInt("hour", 4)
        val minute = pref.getInt("minute", 0)
        val isAM = hour < 12

        val formattedHour = if (isAM) hour else hour - 12
        return String.format(Locale.US, "%d:%02d %s", formattedHour, minute, if (isAM) "AM" else "")
    }

    @SuppressLint("SetTextI18n")
    private fun updateProgress() {
        val progress = calculateProgress()

        if (binding != null) {
            binding.progressBar.progress = progress
            binding.tvProgress.text = "$progress%"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateAlarmTime() {
        val alarmTime = loadTimeDate()
        if (alarmTime != null) {
            binding.tvAlarm.text = alarmTime
        }
    }

    private fun getCurrentTime(): String {
        val calendar = Calendar.getInstance()
        return dateFormat.format(calendar.time)
    }

    private fun calculateTime(alarmMinutes: Int, currentMinutes: Int): Int {
        val timeDifference = currentMinutes - alarmMinutes
        return when {
            timeDifference in 0..5 -> 30
            timeDifference in 6..10 -> 20
            timeDifference in 11..15 -> 10
            else -> 1
        }
    }

    private fun convertToMinutes(time: String): Int {
        val date = dateFormat.parse(time)
        val calendar = Calendar.getInstance()
        date?.let { calendar.time = it }
        return calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
    }

    private fun calculateProgress(): Int {
        val totalCompleted = completedAttendances + completedGoals
        return (totalCompleted / (1 + this.totalGoals).toFloat() * 100).toInt()
    }

    override fun onResume() {
        super.onResume()
        updateAlarmTime()
        getWeatherInCurrentLocation()
    }

    private fun getWeatherInCurrentLocation() {
        mLocationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mLocationListener = LocationListener { p0 ->
            val params: RequestParams = RequestParams()
            params.put("lat", p0.latitude)
            params.put("lon", p0.longitude)
            params.put("appid", Companion.API_KEY)
            params.put("query", "Seoul")
            doNetworking(params)
        }

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 권한이 없는 경우 권한 요청
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                WEATHER_REQUEST
            )
        } else {
            // 권한이 있는 경우 위치 업데이트 요청
            mLocationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                MIN_TIME,
                MIN_DISTANCE,
                mLocationListener
            )
            mLocationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME,
                MIN_DISTANCE,
                mLocationListener
            )
        }
    }

    private fun doNetworking(params: RequestParams) {
        val weatherstackEndpoint = "http://api.weatherstack.com/current"
        params.put("access_key", Companion.API_KEY)

        val client = AsyncHttpClient()
        client.get(weatherstackEndpoint, params, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                val weatherData = WeatherData().fromJson(response)
                if (weatherData != null) {
                    updateWeather(weatherData)
//                    val iconUrl = weatherData.iconUrl // 날씨 아이콘 URL 가져오기
//                    // Glide를 사용하여 이미지 로딩
//                    Glide.with(this@HomeFragment)
//                        .load(iconUrl)
//                        .into(binding.icWeather)
//                    Log.d("아이콘", binding.icWeather.toString())
                    // 날씨 정보 및 온도 업데이트
                    //binding.tvTemperature.text = weatherData.tempString + " ℃"
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable?, errorResponse: JSONObject?) {
                super.onFailure(statusCode, headers, throwable, errorResponse)

            }
        })
    }

    private fun updateWeather(weather: WeatherData) {
        binding.tvTemperature.setText(weather.tempString+" ℃")
        val resourceID = resources.getIdentifier(weather.icon, "drawable", activity?.packageName)
        binding.icWeather.setImageResource(resourceID)
    }

    override fun onPause() {
        super.onPause()
        if(mLocationManager!=null){
            mLocationManager.removeUpdates(mLocationListener)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance() = HomeFragment()
        const val API_KEY: String = "fdc4d1525e506a285b467adc61c77416"
        const val WEATHER_URL: String = "https://api.weatherstack.com/current"
        const val MIN_TIME: Long = 5000
        const val MIN_DISTANCE: Float = 1000F
        const val WEATHER_REQUEST: Int = 102
    }

}