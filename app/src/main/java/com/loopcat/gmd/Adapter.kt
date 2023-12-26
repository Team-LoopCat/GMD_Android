package com.loopcat.gmd


import android.app.AlertDialog
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.loopcat.gmd.databinding.RecyclerItemBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Adapter(private val dataList : List<Box>) : RecyclerView.Adapter<Adapter.ViewHolder>() {
    inner class ViewHolder(private val binding : RecyclerItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val item = binding.layItem
        fun bind(box : Box) {
            binding.textItemBox.text = "${box.boxID}번"
            Log.d("student", "student id : ${box.stuId}    student Name : ${box.stuName}")
            val student = "${box.stuId} ${box.stuName}"
            binding.textItemStudent.text = student
            if(box.status) {
                binding.textItemStatus.text = "제출 완료"
                binding.textItemStatus.setBackgroundResource(R.drawable.submitted)
            } else {
                binding.textItemStatus.text = "제출 안함"
                binding.textItemStatus.setBackgroundResource(R.drawable.no_submit)
            }
        }
        fun change(box: Box) {
            val apiProvider = ApiProvider.getInstance().create(ServerApi::class.java)
            val accessToken = "bearer " + Token().getToken()
            apiProvider.getBoxInfo(box.boxID, accessToken).enqueue(object : Callback<InfoResponse> {
                override fun onResponse(call: Call<InfoResponse>, response: Response<InfoResponse>) {
                    if (response.isSuccessful) {
                        val statusList = response.body()?.status
                        val device = arrayListOf("휴대폰", "학교 노트북", "개인 노트북", "태블릿")
                        val deviceStatus = arrayListOf<Boolean>()
                        if (statusList != null) {
                            deviceStatus.add(statusList.phone)
                            deviceStatus.add(statusList.schoolLabtob)
                            deviceStatus.add(statusList.personalLabtob)
                            deviceStatus.add(statusList.tablet)
                        }
                        binding.root.setOnClickListener {
                            AlertDialog.Builder(binding.root.context).run {
                                setTitle("${box.boxID}번 사물함")
                                setMultiChoiceItems(device.toTypedArray(), deviceStatus.toBooleanArray()) {_, which, isChecked ->
                                    deviceStatus[which] = isChecked
                                }
                                setPositiveButton("저장") { _, _ ->
                                    changeStatus(box, deviceStatus, binding)
                                }
                                setNegativeButton("취소", null)
                                create().show()
                            }
                        }
                    } else {
                        Log.d("server", response.code().toString())
                    }
                }
                override fun onFailure(call: Call<InfoResponse>, t: Throwable) {
                    Log.e("server", t.message.toString())
                    Toast.makeText(binding.root.context, "서버 연결에 실패하였습니다", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = dataList.size
    override fun onBindViewHolder(holder: Adapter.ViewHolder, position: Int) {
        holder.bind(dataList[position])
        holder.item.setOnClickListener {
            holder.change(dataList[position])
        }
    }

    fun changeStatus(box: Box, deviceStatus: ArrayList<Boolean>, binding: RecyclerItemBinding) {
        val phone = deviceStatus[0]
        val schoolLaptop = deviceStatus[1]
        val personalLaptop = deviceStatus[2]
        val tablet = deviceStatus[3]
        val request = ChangeStatusRequest(phone, personalLaptop, schoolLaptop, tablet)
        val apiProvider = ApiProvider.getInstance().create(ServerApi::class.java)
        val accessToken = "bearer " + Token().getToken()
        apiProvider.changeStatus(box.boxID, accessToken, request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(binding.root.context, "성공하였습니다", Toast.LENGTH_SHORT).show()
                    if (phone && schoolLaptop && personalLaptop && tablet) {
                        binding.textItemStatus.text = "제출 완료"
                        binding.textItemStatus.setTextColor(Color.parseColor("#00FF00"))
                        binding.textItemStatus.setBackgroundResource(R.drawable.submitted)
                    } else {
                        binding.textItemStatus.text = "제출 안함"
                        binding.textItemStatus.setTextColor(Color.parseColor("#FF0000"))
                        binding.textItemStatus.setBackgroundResource(R.drawable.no_submit)
                    }
                } else {
                    Toast.makeText(binding.root.context, "실패하였습니다", Toast.LENGTH_SHORT).show()
                    Log.e("server", response.code().toString())
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("server", t.message.toString())
                Toast.makeText(binding.root.context, "서버 연결에 실패하였습니다", Toast.LENGTH_SHORT).show()
            }
        })
    }
}