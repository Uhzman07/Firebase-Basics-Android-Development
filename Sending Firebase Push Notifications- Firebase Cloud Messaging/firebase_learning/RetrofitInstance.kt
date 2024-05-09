package com.example.firebase_learning

import com.example.firebase_learning.Constants.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object{
        // Note that lazy here means that it will only be initialized if it is needed
        private val retrofit by lazy{
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        // To then create the "api"
        val api by lazy {
            retrofit.create(NotificationAPI::class.java)
        }
    }
}