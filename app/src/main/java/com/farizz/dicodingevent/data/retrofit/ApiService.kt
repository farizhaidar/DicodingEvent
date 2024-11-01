package com.farizz.dicodingevent.data.retrofit

import com.farizz.dicodingevent.data.response.DetailResponse
import com.farizz.dicodingevent.data.response.EventResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    fun getEvents(@Query("active") active: Int): Call<EventResponse>

    @GET("events/{id}")
    fun getEventDetails(@Path("id") id: Int): Call<DetailResponse   >
}