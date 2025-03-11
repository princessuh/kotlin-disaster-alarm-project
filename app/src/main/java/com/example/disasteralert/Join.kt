package com.example.disasteralert

import android.content.Intent
import android.os.Bundle
//import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class Join : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var name: EditText
    private lateinit var joinBtn: Button
    private lateinit var loginBtn: Button
    private lateinit var cbNaturalDisaster: CheckBox
    private lateinit var cbSocialDisaster: CheckBox
    private lateinit var cbSafetyInfo: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        // UI 요소 초기화
        email = findViewById(R.id.et_join_email)
        password = findViewById(R.id.et_join_password)
        confirmPassword = findViewById(R.id.et_join_confirm_password)
        name = findViewById(R.id.et_join_name)
        joinBtn = findViewById(R.id.btn_join)
        loginBtn = findViewById(R.id.btn_login)
        cbNaturalDisaster = findViewById(R.id.cb_natural_disaster)
        cbSocialDisaster = findViewById(R.id.cb_social_disaster)
        cbSafetyInfo = findViewById(R.id.cb_safety_info)

        // 📌 회원가입 버튼 클릭 이벤트
//        joinBtn.setOnClickListener {
//            if (joinUser()) { // 회원가입이 성공하면
//                val intent = Intent(this, DisasterSelectionActivity::class.java)
//                startActivity(intent)
//                finish() // 회원가입 화면 종료
//            }
//        }
        joinBtn.setOnClickListener {
            val intent = Intent(this, DisasterSelectionActivity::class.java)
            startActivity(intent)
            finish() // 회원가입 화면 종료
        }

        // 로그인 버튼 클릭 이벤트 (로그인 화면으로 이동)
        loginBtn.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }

//    // 회원가입 유효성 검사 및 처리
//    private fun joinUser(): Boolean {
//        val emailInput = email.text.toString().trim()
//        val passwordInput = password.text.toString().trim()
//        val confirmPasswordInput = confirmPassword.text.toString().trim()
//        val nameInput = name.text.toString().trim()
//        val nicknameInput = nickname.text.toString().trim()
//        val addressInput = address.text.toString().trim()
//
//        // 모든 필드 입력 확인
//        if (emailInput.isEmpty() || passwordInput.isEmpty() || confirmPasswordInput.isEmpty()
//            || nameInput.isEmpty() || nicknameInput.isEmpty() || addressInput.isEmpty()) {
//            showToast("모든 필드를 입력하세요.")
//            return false
//        }
//
//        // 이메일 형식 확인
//        if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
//            showToast("유효한 이메일 주소를 입력하세요.")
//            return false
//        }
//
//        // 비밀번호 길이 확인 (최소 8자)
//        if (passwordInput.length < 8) {
//            showToast("비밀번호는 최소 8자 이상이어야 합니다.")
//            return false
//        }
//
//        // 비밀번호 일치 여부 확인
//        if (passwordInput != confirmPasswordInput) {
//            showToast("비밀번호가 일치하지 않습니다.")
//            return false
//        }
//
//        // 최소 하나의 재난정보 수신 유형이 선택되었는지 확인
//        if (!cbNaturalDisaster.isChecked && !cbSocialDisaster.isChecked && !cbSafetyInfo.isChecked) {
//            showToast("적어도 하나의 재난정보 수신 유형을 선택해야 합니다.")
//            return false
//        }
//
//        // 회원가입 성공 처리
//        showToast("회원가입 성공! 재난 유형 선택 화면으로 이동합니다.")
//        return true
//    }
//
//    // Toast 메시지 출력 함수
//    private fun showToast(message: String) {
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//    }
}
