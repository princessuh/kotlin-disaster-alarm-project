package com.example.disasteralert

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import java.sql.Date
import java.util.*

class Join : AppCompatActivity() {

    private lateinit var userId: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var name: EditText
    private lateinit var birthdate: EditText
    private lateinit var spinnerGender: Spinner
    private lateinit var joinBtn: Button
    private lateinit var loginBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join1)

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
        val genderOptions = arrayOf("선택 안 됨", "남성", "여성")
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

            // 유효성 검사
            if (id.isEmpty() || pw.isEmpty() || pwConfirm.isEmpty() || userName.isEmpty() || birth.isEmpty()) {
                toast("모든 항목을 입력해주세요.")
                return@setOnClickListener
            }

            if (pw.length < 8) {
                toast("비밀번호는 최소 8자 이상이어야 합니다.")
                return@setOnClickListener
            }

            if (pw != pwConfirm) {
                toast("비밀번호가 일치하지 않습니다.")
                return@setOnClickListener
            }

            if (spinnerGender.selectedItemPosition == 0) {
                toast("성별을 선택해 주세요.")
                return@setOnClickListener
            }

            val db = FirebaseFirestore.getInstance()

            // 🔍 중복 ID 확인
            db.collection("users").document(id).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        toast("이미 사용 중인 아이디입니다.")
                    } else {
                        // 🔐 유저 데이터 저장
                        val user = hashMapOf(
                            "user_pw" to pw, // TODO: 해시 적용 예정
                            "user_name" to userName,
                            "birth_date" to Timestamp(Date.valueOf(birth)),
                            "gender" to gender,
                            "created_at" to Timestamp.now()
                        )

                        db.collection("users").document(id)
                            .set(user)
                            .addOnSuccessListener {
                                toast("회원가입 성공!")
                                startActivity(Intent(this, DisasterSelectionActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener { e ->
                                toast("회원가입 실패: ${e.message}")
                            }
                    }
                }
                .addOnFailureListener { e ->
                    toast("ID 확인 실패: ${e.message}")
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
