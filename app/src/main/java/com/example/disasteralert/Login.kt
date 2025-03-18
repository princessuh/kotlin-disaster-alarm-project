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

class Login : AppCompatActivity() {

    // 📌 UI 요소를 `lateinit`으로 선언 (가독성 향상)
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

        // 📌 UI 요소 초기화
        tvTitle = findViewById(R.id.tv_title)
        etUserId = findViewById(R.id.et_user_id)
        etPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_login)
        cbKeepLogin = findViewById(R.id.cb_keep_login)
        tvFindId = findViewById(R.id.tv_find_id)
        tvFindPw = findViewById(R.id.tv_find_pw)
        tvJoin = findViewById(R.id.tv_join)

        // 📌 제목 텍스트 스타일 적용 (로그인 부분만 blue_60, 나머지는 blue_90)
        val titleText = "원활한 이용을 위해\n로그인 해주세요"
        val spannable = SpannableString(titleText)
        val colorBlue90 = ContextCompat.getColor(this, R.color.blue_90) // 기본 텍스트 색상
        val colorBlue60 = ContextCompat.getColor(this, R.color.blue_60) // "로그인" 강조 색상

        // 기본 텍스트 전체를 blue_90 적용
        spannable.setSpan(ForegroundColorSpan(colorBlue90), 0, titleText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        // "로그인" 부분만 blue_60 적용
        val loginStart = titleText.indexOf("로그인")
        val loginEnd = loginStart + "로그인".length
        spannable.setSpan(ForegroundColorSpan(colorBlue60), loginStart, loginEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        // TextView에 Spannable 적용 (BufferType 설정)
        tvTitle.setText(spannable, TextView.BufferType.SPANNABLE)

        // 로그인 버튼 클릭 이벤트
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

            // 로그인 성공 시 (🩷 추후 변경 필요 - 실제 인증 로직을 추가)
            Toast.makeText(this, "로그인 성공!", Toast.LENGTH_SHORT).show()
        }

        // 아이디 찾기 클릭 이벤트 (🩷 추후 변경 필요)
        tvFindId.setOnClickListener {
            Toast.makeText(this, "아이디 찾기 기능 준비 중", Toast.LENGTH_SHORT).show()
        }

        // 비밀번호 찾기 클릭 이벤트 (🩷 추후 변경 필요)
        tvFindPw.setOnClickListener {
            Toast.makeText(this, "비밀번호 찾기 기능 준비 중", Toast.LENGTH_SHORT).show()
        }

        // 회원가입 클릭 이벤트 (회원가입 화면으로 이동)
        tvJoin.setOnClickListener {
            val intent = Intent(this, Join::class.java)
            startActivity(intent)
        }

    }
}
