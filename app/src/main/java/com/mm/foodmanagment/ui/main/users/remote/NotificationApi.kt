package com.mm.foodmanagment.ui.main.users.remote

import com.mm.foodmanagment.ui.main.users.model.PushNotification
import com.mm.foodmanagment.utils.Constants


import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


interface NotificationApi {



    @Headers("Authorization: key= ${Constants.SERVER_KEY}","Content-type:${Constants.CONTENT_TYPE}")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}