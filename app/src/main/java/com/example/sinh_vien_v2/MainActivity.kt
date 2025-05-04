package com.example.sinh_vien_v2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private val studentList = mutableListOf<Student>()
    private lateinit var studentAdapter: StudentAdapter
    private val ADD_STUDENT_REQUEST = 100
    private val UPDATE_STUDENT_REQUEST = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.title = "Danh sách sinh viên"

        val recyclerView = findViewById<RecyclerView>(R.id.list_students)
        studentAdapter = StudentAdapter(studentList) { student, position, anchorView ->
            showPopupMenu(student, position, anchorView)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = studentAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_add_student) {
            val intent = Intent(this, AddStudentActivity::class.java)
            startActivityForResult(intent, ADD_STUDENT_REQUEST)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showPopupMenu(student: Student, position: Int, anchorView: View) {
        val popup = PopupMenu(this, anchorView)
        popup.menuInflater.inflate(R.menu.menu_student, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_update -> {
                    val intent = Intent(this, UpdateStudentActivity::class.java).apply {
                        putExtra("student_name", student.name)
                        putExtra("student_mssv", student.mssv)
                        putExtra("student_phone", student.phone)
                        putExtra("student_email", student.email)
                        putExtra("position", position)
                    }
                    startActivityForResult(intent, UPDATE_STUDENT_REQUEST)
                }

                R.id.menu_delete -> {
                    AlertDialog.Builder(this)
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc muốn xóa sinh viên này?")
                        .setPositiveButton("Xóa") { _, _ ->
                            studentAdapter.removeStudent(position)
                        }
                        .setNegativeButton("Hủy", null)
                        .show()
                }

                R.id.menu_call -> {
                    if (student.phone.isNotEmpty()) {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${student.phone}")
                        }
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Không có số điện thoại", Toast.LENGTH_SHORT).show()
                    }
                }

                R.id.menu_email -> {
                    if (student.email.isNotEmpty()) {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:${student.email}")
                        }
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Không có email", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            true
        }

        popup.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            val name = data.getStringExtra("student_name") ?: return
            val mssv = data.getStringExtra("student_mssv") ?: return
            val phone = data.getStringExtra("student_phone") ?: ""
            val email = data.getStringExtra("student_email") ?: ""
            val newStudent = Student(name, mssv, phone, email)

            when (requestCode) {
                ADD_STUDENT_REQUEST -> studentAdapter.addStudent(newStudent)
                UPDATE_STUDENT_REQUEST -> {
                    val position = data.getIntExtra("position", -1)
                    if (position != -1) {
                        studentAdapter.updateStudent(position, newStudent)
                    }
                }
            }
        }
    }
}
