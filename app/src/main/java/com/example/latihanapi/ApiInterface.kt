package com.example.latihanapi

import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {
    //show all records
    @GET("/latihanAPI/public/api/ceo")
    fun getData(): Call<ArrayList<RecordModel>>

    //add a new record
    @POST("/latihanAPI/public/api/ceo")
    fun setData(@Body newRecordModel: RecordModel) : Call<RecordModel>

    //delete a existing record
    @DELETE("/latihanAPI/public/api/ceo/{id}") //kalo delete, masukin ID variabelnya
    fun deleteData(@Path("id") id:Int) : Call<RecordModel>

    //update new values from existing record
//    @FormUrlEncoded
//    @PUT("/latihanAPI/public/api/ceo/{id}")
//    fun updateData(@Path("id") id:Int,
//                   @Field("name") name:String,
//                   @Field("company_name") company_name:String) : Call <RecordModel>

    @PATCH("/latihanAPI/public/api/ceo/{id}")
    fun updateData(@Body newRecordModel: RecordModel, @Path("id") id:Int) : Call<RecordModel>
}