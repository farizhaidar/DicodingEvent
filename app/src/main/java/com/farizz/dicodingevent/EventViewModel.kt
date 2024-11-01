package com.farizz.dicodingevent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.farizz.dicodingevent.data.response.EventResponse
import com.farizz.dicodingevent.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventViewModel : ViewModel() {

    private val _events = MutableLiveData<EventResponse>()
    val events: LiveData<EventResponse> = _events

    fun getEvents(active: Int) {
        val client = ApiConfig.getApiService().getEvents(active)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(
                call: Call<EventResponse>,
                response: Response<EventResponse>
            ) {
                if (response.isSuccessful) {
                    _events.value = response.body()
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
            }
        })
    }
}