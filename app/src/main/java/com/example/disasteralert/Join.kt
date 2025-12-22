package com.example.disasteralert

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.example.disasteralert.api.RetrofitClient
import com.example.disasteralert.api.UserRegisterRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class Join : BaseActivity() {
    private lateinit var userId: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var name: EditText
    private lateinit var birthdate: EditText
    private lateinit var spinnerGender: Spinner
    private lateinit var joinBtn: Button
    private lateinit var loginBtn: Button
    private var fcmToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join1)

        // FCM 토큰 요청
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                fcmToken = task.result
                Log.d("FCM", "FCM 토큰 받아옴: $fcmToken")
            } else {
                Log.w("FCM", "FCM 토큰 못 받아옴", task.exception)
            }
        }

        initViews()
        setupGenderSpinner()
        setupBirthdatePicker()
        setupJoinButton()
        setupLoginButton()
    }

    private fun initViews() {
        userId = findViewById(R.id.et_join_user_id)
        password = findViewById(R.id.et_join_password)
        confirmPassword = findViewById(R.id.et_join_confirm_password)
        name = findViewById(R.id.et_join_name)
        birthdate = findViewById(R.id.et_birthdate)
        spinnerGender = findViewById(R.id.spinner_gender)
        joinBtn = findViewById(R.id.btn_join)
        loginBtn = findViewById(R.id.btn_login)
    }

    private fun setupGenderSpinner() {
        val genderOptions = arrayOf(
            getString(R.string.gender_not_selected),
            getString(R.string.gender_male),
            getString(R.string.gender_female)
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, genderOptions)
        spinnerGender.adapter = adapter
    }

    private fun setupBirthdatePicker() {
        birthdate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, y, m, d ->
                val formatted = String.format("%04d-%02d-%02d", y, m + 1, d)
                birthdate.setText(formatted)
            }, year, month, day).show()
        }
    }

    private fun setupJoinButton() {
        joinBtn.setOnClickListener {
            val id = userId.text.toString().trim()
            val pw = password.text.toString().trim()
            val pwConfirm = confirmPassword.text.toString().trim()
            val userName = name.text.toString().trim()
            val birth = birthdate.text.toString().trim()
            val gender = spinnerGender.selectedItem.toString()

            if (id.isEmpty() || pw.isEmpty() || pwConfirm.isEmpty() || userName.isEmpty() || birth.isEmpty()) {
                toast(getString(R.string.error_all_fields_required))
                return@setOnClickListener
            }

            if (pw.length < 8) {
                toast(getString(R.string.error_password_min_length))
                return@setOnClickListener
            }

            if (pw != pwConfirm) {
                toast(getString(R.string.error_password_mismatch))
                return@setOnClickListener
            }

            if (spinnerGender.selectedItemPosition == 0) {
                toast(getString(R.string.error_select_gender))
                return@setOnClickListener
            }

            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(id).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        toast(getString(R.string.error_id_already_exists))
                    } else {
                        // 🎯 수정된 저장 구조 (Timestamp 제거)
                        val user = hashMapOf(
                            "user_pw" to pw,
                            "user_name" to userName,
                            "birth_date" to birth,
                            "gender" to gender,
                            "created_at" to Date().toString()
                        )

                        db.collection("users").document(id)
                            .set(user)
                            .addOnSuccessListener {
                                Log.d("REGISTER_DEBUG", "Firestore 저장 성공")

                                // ✅ FCM 토큰 서버 전송
                                val registerRequest = UserRegisterRequest(
                                    user_id = id,
                                    device_token = fcmToken ?: "no_token"
                                )

                                Log.d("REGISTER_DEBUG", "전송 URL: http://61.245.248.197:8000/devices/register")
                                Log.d("REGISTER_DEBUG", "user_id = ${registerRequest.user_id}, token = ${registerRequest.device_token}")

                                RetrofitClient.userRegisterService.registerUser(registerRequest)
                                    .enqueue(object : Callback<Void> {
                                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                            if (!response.isSuccessful) {
                                                Log.e("REGISTER", "서버 등록 실패: ${response.code()}")
                                            } else {
                                                Log.d("REGISTER", "서버 등록 성공")
                                            }
                                        }

                                        override fun onFailure(call: Call<Void>, t: Throwable) {
                                            Log.e("REGISTER", "네트워크 오류: ${t.localizedMessage}")
                                        }
                                    })

                                // SharedPreferences 저장
                                val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                                prefs.edit().putString("user_id", id).putString("user_name", userName).apply()

                                // 다음 화면으로 이동
                                startActivity(Intent(this@Join, DisasterSelectionActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener { e ->
                                toast(getString(R.string.error_register_failed, e.message))
                                Log.e("REGISTER_DEBUG", "Firestore 저장 실패: ${e.message}")
                            }
                    }
                }
                .addOnFailureListener { e ->
                    toast(getString(R.string.error_id_check_failed, e.message))
                    Log.e("REGISTER_DEBUG", "Firestore ID 중복 확인 실패: ${e.message}")
                }
        }
    }

    private fun setupLoginButton() {
        loginBtn.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
