package com.example.disasteralert

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import java.util.Calendar
import android.widget.*

// 프로필 수정

class ProfileEditActivity : BaseActivity() {

    private lateinit var userId: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var name: EditText
    private lateinit var birthdate: EditText
    private lateinit var spinnerGender: Spinner
    private lateinit var finishBtn: Button
    private lateinit var logoutBtn: Button
    private lateinit var cbNaturalDisaster: CheckBox
    private lateinit var cbSocialDisaster: CheckBox
    private lateinit var cbSafetyInfo: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        // UI 요소 초기화
        userId = findViewById(R.id.et_user_id)
        password = findViewById(R.id.et_password)
        confirmPassword = findViewById(R.id.et_confirm_password)
        name = findViewById(R.id.et_name)
        birthdate = findViewById(R.id.et_birthdate)
        spinnerGender = findViewById(R.id.spinner_gender)
        finishBtn = findViewById(R.id.btn_finish)
        logoutBtn = findViewById(R.id.btn_logout)
        cbNaturalDisaster = findViewById(R.id.cb_natural_disaster)
        cbSocialDisaster = findViewById(R.id.cb_social_disaster)
        cbSafetyInfo = findViewById(R.id.cb_safety_info)

        // 📌 성별 선택 스피너 설정
        val genderOptions = arrayOf("선택 안 됨", "남성", "여성")
        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, genderOptions)
        spinnerGender.adapter = genderAdapter

        // 📌 생년월일 선택 기능
        birthdate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                    birthdate.setText(formattedDate)
                }, year, month, day)

            datePickerDialog.show()
        }

        // 📌 회원가입 버튼 클릭 이벤트
//        finishBtn.setOnClickListener {
//            if (finishUser()) { // 회원가입이 성공하면
//                val intent = Intent(this, DisasterSelectionActivity::class.java)
//                startActivity(intent)
//                finish() // 회원가입 화면 종료
//            }
//        }
        // 📌 회원가입 버튼 클릭 이벤트 (성별 선택 여부 체크)
        finishBtn.setOnClickListener {
            if (spinnerGender.selectedItemPosition == 0) {
                // 🚀 성별이 선택되지 않았을 경우 토스트 메시지 띄우기
                Toast.makeText(this, "성별을 선택해 주세요.", Toast.LENGTH_SHORT).show()
            } else {
                // 🚀 성별이 선택되었을 경우 다음 단계로 진행
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
        }

        // 로그아웃 버튼 클릭 이벤트 (로그인 화면으로 이동)
        logoutBtn.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }

//    // 회원가입 유효성 검사 및 처리
//    private fun finishUser(): Boolean {
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