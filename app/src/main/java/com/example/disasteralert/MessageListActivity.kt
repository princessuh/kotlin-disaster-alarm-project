package com.example.disasteralert

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MessageListActivity : AppCompatActivity() {

    private lateinit var btnFilter: MaterialButton
    private lateinit var tvSelectedFilters: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_list)

        btnFilter = findViewById(R.id.btn_filter)
        tvSelectedFilters = findViewById(R.id.tv_selected_disasters) // id 수정 반영

        btnFilter.setOnClickListener {
            val dialog = MessageFilterBottomSheetDialog { infoTypes, disasterTypes ->
                val infoText = if (infoTypes.isEmpty()) "정보유형: 없음" else "정보유형: ${infoTypes.joinToString(", ")}"
                val disasterText = if (disasterTypes.isEmpty()) "재난유형: 없음" else "재난유형: ${disasterTypes.joinToString(", ")}"
                tvSelectedFilters.text = "$infoText\n$disasterText"
            }
            dialog.show(supportFragmentManager, "MessageFilterBottomSheet")
        }
    }
}
