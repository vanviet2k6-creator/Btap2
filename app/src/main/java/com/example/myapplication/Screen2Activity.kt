package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.button.MaterialButton

class Screen2Activity : AppCompatActivity() {

    private var editPosition: Int = -1
    private var isEditMode: Boolean = false

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_screen2)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val tvTitleScreen2 = findViewById<TextView>(R.id.tvTitleScreen2)
        val tvSubtitleScreen2 = findViewById<TextView>(R.id.tvSubtitleScreen2)
        val etName = findViewById<EditText>(R.id.etName)
        val etCode = findViewById<EditText>(R.id.etCode)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etAvatarUrl = findViewById<EditText>(R.id.etAvatarUrl)
        val btnSaveStudent = findViewById<MaterialButton>(R.id.btnSaveStudent)
        val btnDeleteForm = findViewById<MaterialButton>(R.id.btnDeleteForm)

        editPosition = intent.getIntExtra("EXTRA_POSITION", -1)
        val existingStudent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("EXTRA_STUDENT", Student::class.java)
        } else {
            intent.getSerializableExtra("EXTRA_STUDENT") as? Student
        }

        if (editPosition != -1 && existingStudent != null) {
            isEditMode = true
            tvTitleScreen2.text = "Edit Student Profile"
            tvSubtitleScreen2.text = "Update profile details"

            etName.setText(existingStudent.name)
            etCode.setText(existingStudent.code)
            etEmail.setText(existingStudent.email)
            etAvatarUrl.setText(existingStudent.avatarUrl)

            btnDeleteForm.visibility = View.VISIBLE
        } else {
            isEditMode = false
            btnDeleteForm.visibility = View.GONE
        }

        btnBack.setOnClickListener {
            finish()
        }

        btnSaveStudent.setOnClickListener {
            val name = etName.text.toString().trim()
            val code = etCode.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val avatarUrl = etAvatarUrl.text.toString().trim()

            var isValid = true

            if (name.isEmpty()) {
                etName.error = "Vui lòng nhập tên"
                isValid = false
            }

            if (code.isEmpty()) {
                etCode.error = "Vui lòng nhập mã sinh viên"
                isValid = false
            }

            if (email.isEmpty()) {
                etEmail.error = "Vui lòng nhập email"
                isValid = false
            }

            if (isValid) {
                val action = if (isEditMode) "ACTION_EDIT" else "ACTION_ADD"
                val student = Student(
                    id = if (isEditMode && existingStudent != null) existingStudent.id else System.currentTimeMillis().toString(),
                    name = name,
                    code = code,
                    email = email,
                    avatarUrl = avatarUrl
                )

                val resultIntent = Intent().apply {
                    putExtra("EXTRA_ACTION", action)
                    putExtra("EXTRA_POSITION", editPosition)
                    putExtra("EXTRA_STUDENT", student)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                Toast.makeText(this, if (isEditMode) "Đã cập nhật sinh viên!" else "Đã thêm sinh viên mới!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        btnDeleteForm.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa sinh viên ${existingStudent?.name}?")
                .setPositiveButton("Xóa") { _, _ ->
                    val resultIntent = Intent().apply {
                        putExtra("EXTRA_ACTION", "ACTION_DELETE")
                        putExtra("EXTRA_POSITION", editPosition)
                    }
                    setResult(Activity.RESULT_OK, resultIntent)
                    Toast.makeText(this, "Đã xóa sinh viên!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .setNegativeButton("Hủy", null)
                .show()
        }
    }
}
