package com.loopcat.gmd.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.loopcat.gmd.Adapter
import com.loopcat.gmd.ApiProvider
import com.loopcat.gmd.Box
import com.loopcat.gmd.BoxResponse
import com.loopcat.gmd.ServerApi
import com.loopcat.gmd.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private val dataList = mutableListOf<Box>()
    private val adapter = Adapter(dataList)
    private var grade : Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getBox(grade)

        binding.recyclerMain.adapter = adapter
        binding.recyclerMain.layoutManager = LinearLayoutManager(this)

        binding.imgMainMenu.setOnClickListener {
            binding.layMainDrawer.openDrawer(GravityCompat.START)
        }

        binding.textSideFirst.setOnClickListener {
            sideSelect(binding.textSideFirst)
            sideUnselect(binding.textSideSecond)
            sideUnselect(binding.textSideThird)
            sideUnselect(binding.textSideNoSubmit)
            grade = 1
            binding.textMainGrade.text = "1학년"
            select(grade)
        }
        binding.textSideSecond.setOnClickListener {
            sideSelect(binding.textSideSecond)
            sideUnselect(binding.textSideFirst)
            sideUnselect(binding.textSideThird)
            sideUnselect(binding.textSideNoSubmit)
            grade = 2
            binding.textMainGrade.text = "2학년"
            select(grade)
        }
        binding.textSideThird.setOnClickListener {
            sideSelect(binding.textSideThird)
            sideUnselect(binding.textSideFirst)
            sideUnselect(binding.textSideSecond)
            sideUnselect(binding.textSideNoSubmit)
            grade = 3
            binding.textMainGrade.text = "3학년"
            select(grade)
        }
        binding.textSideNoSubmit.setOnClickListener {
            sideSelect(binding.textSideNoSubmit)
            sideUnselect(binding.textSideFirst)
            sideUnselect(binding.textSideSecond)
            sideUnselect(binding.textSideThird)
            grade = 4
            binding.textMainGrade.text = "미제출"
            select(grade)
        }

        binding.imgMainLogout.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("로그아웃 하시겠습니까?")
                .setPositiveButton("YES", DialogInterface.OnClickListener {_, _->
                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                })
                .setNegativeButton("NO", null)
            builder.show()
        }
    }

    private fun sideSelect(textView : TextView) {
        textView.setTextColor(Color.parseColor("#FFFFFF"))
        textView.setBackgroundColor(Color.parseColor("#0041E6"))
    }
    private fun sideUnselect(textView : TextView) {
        textView.setTextColor(Color.parseColor("#000000"))
        textView.setBackgroundColor(Color.parseColor("#F0F0F0"))
    }
    private fun select(grade: Int) {
        binding.layMainDrawer.closeDrawers()
        getBox(grade)
    }

    private fun getBox(grade : Int) {
        val apiProvider = ApiProvider.getInstance().create(ServerApi::class.java)
        val intent = intent
        val accessToken = "bearer " + intent.getStringExtra("token")
        Log.d("token", accessToken)
        Log.d("grade", grade.toString())
        apiProvider.getList(grade, accessToken).enqueue(object : Callback<BoxResponse> {
            override fun onResponse(call: Call<BoxResponse>, response: Response<BoxResponse>) {
                if (response.isSuccessful) {
                    Log.d("server", "박스 조회 성공")
                    Log.d("data", response.body().toString())
                    val boxResponse = response.body()?.boxs
                    boxResponse?.let { setBox(it) }
                } else {
                    Log.d("server", response.code().toString())
                }
            }
            override fun onFailure(call: Call<BoxResponse>, t: Throwable) {
                Log.e("server", t.message.toString())
                Toast.makeText(this@MainActivity, "서버 연결에 실패하였습니다", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun setBox(response: List<Box>) {
        dataList.clear()
        val total = response.size
        for (i in 0..<total) {
            val boxObject = response[i]
            val boxId = boxObject.boxID
            val studentId = boxObject.stuId
            val studentName = boxObject.stuName
            val status = boxObject.status
            val box = Box(boxId, studentId, studentName, status)
            dataList.add(box)
        }
        adapter.notifyDataSetChanged()
    }
}