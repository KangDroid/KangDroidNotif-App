package com.kangdroid.notification.server

import retrofit2.Call
import retrofit2.http.GET

interface CallAPI {
    @GET("/get/NotifCount")
    fun getNotificationCount(): Call<String>
}