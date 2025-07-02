package com.example.disasteralert

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore

// 로그인 화면
class Login : BaseActivity() {

    private lateinit var etUserId: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var cbKeepLogin: CheckBox
    private lateinit var tvFindId: TextView
    private lateinit var tvFindPw: TextView
    private lateinit var tvJoin: TextView

    private lateinit var loginPrefs: SharedPreferences
    private lateinit var userPrefs: SharedPreferences

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // SharedPreferences 초기화
        loginPrefs = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        userPrefs = applicationContext.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        // ✅ 자동 로그인 처리
        val keepLogin = loginPrefs.getBoolean("keep_login", false)
        val userId    = loginPrefs.getString("user_id", null)

        if (keepLogin && userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { doc ->
                    if (doc != null && doc.exists()) {
                        val userName = doc.getString("user_name") ?: ""

                        userPrefs.edit()
                            .putString("user_id", userId)
                            .putString("user_name", userName)
                            .apply()

                        val intent = Intent(this, MainMapActivity::class.java).apply {
                            putExtra("user_id", userId)
                            putExtra("user_name", userName)
                        }
                        startActivity(intent)
                        finish()
                    }
                }
            return  // 로그인 화면은 띄우지 않음
        }

        // UI 요소 초기화
        etUserId    = findViewById(R.id.et_user_id)
        etPassword  = findViewById(R.id.et_password)
        btnLogin    = findViewById(R.id.btn_login)
        cbKeepLogin = findViewById(R.id.cb_keep_login)
        tvFindId    = findViewById(R.id.tv_find_id)
        tvFindPw    = findViewById(R.id.tv_find_pw)
        tvJoin      = findViewById(R.id.tv_join)

        // 로그인 버튼 클릭
        btnLogin.setOnClickListener {
            val id       = etUserId.text.toString().trim()
            val password = etPassword.text.toString()

            if (id.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "아이디와 비밀번호를 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Firestore에서 사용자 문서 조회
            db.collection("users").document(id).get()
                .addOnSuccessListener { doc ->
                    if (doc != null && doc.exists()) {
                        val savedPw   = doc.getString("user_pw")
                        val userName  = doc.getString("user_name") ?: ""

                        if (savedPw == password) {
                            Toast.makeText(this, "로그인 성공!", Toast.LENGTH_SHORT).show()

                            // (1) 기존 login_prefs에 저장 (로그인 유지용)
                            if (cbKeepLogin.isChecked) {
                                loginPrefs.edit()
                                    .putBoolean("keep_login", true)
                                    .putString("user_id", id)
                                    .apply()
                            }

                            // (2) ProfileActivity가 읽는 user_prefs에도 저장
                            Log.d("LOGIN_DEBUG", "user_id saved: $id")
                            userPrefs.edit()
                                .putString("user_id",   id)
                                .putString("user_name", userName)
                                .apply()

                            // 메인맵 화면으로 이동
                            val intent = Intent(this, MainMapActivity::class.java).apply {
                                putExtra("user_id",   id)
                                putExtra("user_name", userName)
                            }
                            startActivity(intent)
                            finish()

                        } else {
                            etPassword.error = "비밀번호가 일치하지 않습니다."
                        }
                    } else {
                        etUserId.error = "존재하지 않는 아이디입니다."
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "로그인 오류: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // 기타 버튼 이벤트
        tvFindId.setOnClickListener {
            Toast.makeText(this, "아이디 찾기 기능 준비 중", Toast.LENGTH_SHORT).show()
        }
        tvFindPw.setOnClickListener {
            Toast.makeText(this, "비밀번호 찾기 기능 준비 중", Toast.LENGTH_SHORT).show()
        }
        tvJoin.setOnClickListener {
            startActivity(Intent(this, Join::class.java))
        }
    }
}
