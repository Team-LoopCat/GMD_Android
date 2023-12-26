package com.loopcat.gmd

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ServerApi {
    @POST("/user/login")
    fun login(@Body request : LoginRequest) : Call<LoginResponse>

    @GET("/chack/list/{grade}")
    fun getList(
        @Path("grade") grade : Int,
        @Header("access_token") token : String,
    ) : Call<BoxResponse>

    @GET("/chack/info/{boxID}")
    fun getBoxInfo(
        @Path("boxID") boxId : Int,
        @Header("acccess_token") token: String
    ) : Call<InfoResponse>

    @PATCH("/chack/{boxID}")
    fun changeStatus(
        @Path("boxID") boxId: Int,
        @Header("access_token") token: String,
        @Body request: ChangeStatusRequest
    ): Call<Void>
}