package com.example.disasteralert

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore

class Login : AppCompatActivity() {

    /** UI 요소 정의 */
    private lateinit var tvTitle: TextView
    private lateinit var etUserId: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var cbKeepLogin: CheckBox
    private lateinit var tvFindId: TextView
    private lateinit var tvFindPw: TextView
    private lateinit var tvJoin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // UI 초기화
        tvTitle = findViewById(R.id.tv_title)
        etUserId = findViewById(R.id.et_user_id)
        etPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_login)
        cbKeepLogin = findViewById(R.id.cb_keep_login)
        tvFindId = findViewById(R.id.tv_find_id)
        tvFindPw = findViewById(R.id.tv_find_pw)
        tvJoin = findViewById(R.id.tv_join)

        // 제목 텍스트 꾸미기
        val titleText = "원활한 이용을 위해\n로그인 해주세요"
        val spannable = SpannableString(titleText)
        val colorBlue90 = ContextCompat.getColor(this, R.color.blue_90)
        val colorBlue60 = ContextCompat.getColor(this, R.color.blue_60)

        spannable.setSpan(ForegroundColorSpan(colorBlue90), 0, titleText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val loginStart = titleText.indexOf("로그인")
        val loginEnd = loginStart + "로그인".length
        spannable.setSpan(ForegroundColorSpan(colorBlue60), loginStart, loginEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        tvTitle.setText(spannable, TextView.BufferType.SPANNABLE)

        // 🔐 로그인 버튼 이벤트
        btnLogin.setOnClickListener {
            val id = etUserId.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (id.isEmpty()) {
                etUserId.error = "아이디를 입력하세요."
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                etPassword.error = "비밀번호를 입력하세요."
                return@setOnClickListener
            }

            // Firestore에서 해당 사용자 문서 가져오기
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(id).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val savedPw = document.getString("user_pw")
                        if (savedPw == password) {
                            Toast.makeText(this, "로그인 성공!", Toast.LENGTH_SHORT).show()
                            val userName = document.getString("user_name") ?: ""

                            val intent = Intent(this, ProfileActivity::class.java)
                            intent.putExtra("user_id", id)
                            intent.putExtra("user_name", userName)
                            startActivity(intent)
                            finish() // 로그인 화면 종료 (뒤로가기 방지)
                        } else {
                            etPassword.error = "비밀번호가 일치하지 않습니다."
                        }
                    } else {
                        etUserId.error = "존재하지 않는 아이디입니다."
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "로그인 중 오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
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
            val intent = Intent(this, Join::class.java)
            startActivity(intent)
        }
    }
}
