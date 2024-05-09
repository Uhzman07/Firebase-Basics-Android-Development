package com.example.firebase_learning

//import com.google.android.gms.common.api.Response
import com.example.firebase_learning.Constants.Companion.CONTENT_TYPE
import com.example.firebase_learning.Constants.Companion.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.Headers

interface NotificationAPI{
    @Headers("Authorization:key =$SERVER_KEY", "Content-Type:$CONTENT_TYPE") // Note that we are adding the server key and the content type to this
    @POST("fcm/send") // This can be found in the documentation and it is used to refer to the API that we are sending to
    suspend fun postNotification(
        @Body notification: PushNotification // This is to show that the content is our Push notification

    ) : Response<ResponseBody> // This is because we expect it to return a boolean value "Response"
}