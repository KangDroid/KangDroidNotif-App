package com.kangdroid.notification.server

import com.kangdroid.notification.dto.NotificationData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CallAPI {
    @GET("/get/NotifCount")
    fun getNotificationCount(): Call<String>

    @POST("/post/notifPost")
    fun postTestValue(@Body param: HashMap<String, Any>): Call<NotificationData>
}