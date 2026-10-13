package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import coil.load
import com.google.android.material.button.MaterialButton

class BaiTapBuoi4Activity : AppCompatActivity() {

    private lateinit var studentListContainer: LinearLayout
    private val studentList = ArrayList<Student>()

    @Suppress("DEPRECATION")
    private val studentFormLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data ?: return@registerForActivityResult
            val action = data.getStringExtra("EXTRA_ACTION") ?: return@registerForActivityResult
            val position = data.getIntExtra("EXTRA_POSITION", -1)
            val student = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                data.getSerializableExtra("EXTRA_STUDENT", Student::class.java)
            } else {
                data.getSerializableExtra("EXTRA_STUDENT") as? Student
            }

            when (action) {
                "ACTION_ADD" -> {
                    if (student != null) {
                        studentList.add(student)
                        renderStudentList()
                    }
                }
                "ACTION_EDIT" -> {
                    if (student != null && position in 0 until studentList.size) {
                        studentList[position] = student
                        renderStudentList()
                    }
                }
                "ACTION_DELETE" -> {
                    if (position in 0 until studentList.size) {
                        studentList.removeAt(position)
                        renderStudentList()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_bai_tap_buoi4)

        studentListContainer = findViewById(R.id.studentListContainer)
        val btnAddStudent = findViewById<MaterialButton>(R.id.btnAddStudent)

        initDefaultStudents()
        renderStudentList()

        btnAddStudent.setOnClickListener {
            val intent = Intent(this, Screen2Activity::class.java)
            studentFormLauncher.launch(intent)
        }
    }

    private fun initDefaultStudents() {
        if (studentList.isEmpty()) {
            studentList.add(
                Student(
                    id = "1",
                    name = "xuanthanhh",
                    code = "ww",
                    email = "dd",
                    avatarUrl = ""
                )
            )
            studentList.add(
                Student(
                    id = "2",
                    name = "tienthanh",
                    code = "bit2345",
                    email = "xuanthanh21022006@gmail.com",
                    avatarUrl = ""
                )
            )
            studentList.add(
                Student(
                    id = "3",
                    name = "vanviet",
                    code = "bit1234",
                    email = "vanviet123@gmail.com",
                    avatarUrl = ""
                )
            )
        }
    }

    private fun renderStudentList() {
        studentListContainer.removeAllViews()

        for ((index, student) in studentList.withIndex()) {
            val cardView = LayoutInflater.from(this).inflate(R.layout.item_student_card, studentListContainer, false)

            val tvName = cardView.findViewById<TextView>(R.id.tvStudentName)
            val tvCode = cardView.findViewById<TextView>(R.id.tvStudentCode)
            val tvEmail = cardView.findViewById<TextView>(R.id.tvStudentEmail)
            val tvAvatar = cardView.findViewById<TextView>(R.id.tvAvatar)
            val ivAvatar = cardView.findViewById<ImageView>(R.id.ivAvatar)
            val btnEditStudent = cardView.findViewById<ImageButton>(R.id.btnEditStudent)
            val btnDeleteStudent = cardView.findViewById<ImageButton>(R.id.btnDeleteStudent)

            tvName.text = student.name
            tvCode.text = student.code
            tvEmail.text = student.email

            if (student.avatarUrl.isNotBlank()) {
                ivAvatar.visibility = View.VISIBLE
                tvAvatar.visibility = View.GONE
                ivAvatar.load(student.avatarUrl) {
                    crossfade(true)
                    error(R.drawable.bg_purple_avatar)
                }
            } else {
                ivAvatar.visibility = View.GONE
                tvAvatar.visibility = View.VISIBLE
                val firstChar = if (student.name.isNotEmpty()) student.name.first().uppercaseChar().toString() else "S"
                tvAvatar.text = firstChar
            }

            val openEdit = View.OnClickListener {
                val intent = Intent(this, Screen2Activity::class.java).apply {
                    putExtra("EXTRA_POSITION", index)
                    putExtra("EXTRA_STUDENT", student)
                }
                studentFormLauncher.launch(intent)
            }

            cardView.setOnClickListener(openEdit)
            btnEditStudent.setOnClickListener(openEdit)

            btnDeleteStudent.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc chắn muốn xóa sinh viên ${student.name}?")
                    .setPositiveButton("Xóa") { _, _ ->
                        studentList.removeAt(index)
                        renderStudentList()
                        Toast.makeText(this, "Đã xóa sinh viên ${student.name}!", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Hủy", null)
                    .show()
            }

            studentListContainer.addView(cardView)
        }
    }
}
