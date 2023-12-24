package com.loopcat.gmd.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.loopcat.gmd.ApiProvider
import com.loopcat.gmd.LoginRequest
import com.loopcat.gmd.LoginResponse
import com.loopcat.gmd.ServerApi
import com.loopcat.gmd.Token
import com.loopcat.gmd.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val id = binding.editLoginId.text.toString()
            val pw = binding.editLoginPw.text.toString()
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
            login(id, pw)
        }
    }

    private fun login(id: String, pw: String) {
        val apiProvider = ApiProvider.getInstance().create(ServerApi::class.java)
        val loginRequest = LoginRequest(id, pw)
        apiProvider.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    Log.d("token", response.body().toString())
                    Log.d("token", response.body()?.accessToken.toString())
                    Toast.makeText(this@LoginActivity, "로그인 되었습니다", Toast.LENGTH_SHORT).show()
                    val token = response.body()?.accessToken.toString()
                    Token().setToken(token)

                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.putExtra("token", token)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "아이디나 비밀번호가 맞지 않습니다", Toast.LENGTH_SHORT).show()
                    Log.d("server", response.code().toString())
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("server error", t.message.toString())
                Toast.makeText(this@LoginActivity, "서버 연동에 실패하였습니다", Toast.LENGTH_SHORT).show()
            }
        })
    }
}