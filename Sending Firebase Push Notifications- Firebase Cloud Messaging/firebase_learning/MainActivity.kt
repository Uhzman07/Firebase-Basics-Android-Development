package com.example.firebase_learning

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebase_learning.databinding.ActivityMainBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.lang.StringBuilder

const val TOPIC = "/topics/myTopic"

class MainActivity : AppCompatActivity() {

    // Note that this won't work on the emulators unless we have enabled the permission for notification and then we must create a code that must allow the user to enable notifications

    // To connect to firestore
    // We go to tools then cloud firestore then we connect
    // Note that firebase also a create a different document with a special id for each document that is saved in the collection

    // When we run this on two emulator at once then we can now copy the token from one to another and then it will appear on the screen of that one with the exact token defined

    val TAG = "MainActivity"

    private lateinit var binding : ActivityMainBinding

    //private val personCollectionRef = Firebase.firestore.collection("persons") // Note that the collection is created automatically later
    // Note that we are using this to send notifications to other devices so what we have to do is that we go to the tool and then "firebase cloud messaging"
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE) // Note that we have to call the shared preference from the other class
        // To get the registration token (This will allow the specific device that we want to send the notification to)
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            FirebaseService.token = it
            binding.etToken.setText(it)


        }


        // To then call the service
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC) // Note that if we launch this on two different emulators then we send a notification, since they have similar "TOPIC" then they both receive it all at once.

        binding.btnSend.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val message = binding.etMessage.text.toString()
            val recepientToken = binding.etToken.text.toString()
            if(title.isNotEmpty() && message.isNotEmpty() && recepientToken.isNotEmpty()){
                PushNotification(
                    NotificationData(title,message),recepientToken // Now we are making use of the token instead of the set "TOPIC"
                ).also{
                    sendNotification(it)
                }
            }
        }

    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful){
                Log.d(TAG,"Response: ${Gson().toJson(response)}") // This is used to convert that response to a JSON file that is understandable
            }
            else{
                Log.d(TAG,response.errorBody().toString())

            }

        } catch (e:Exception){
            Log.e(TAG, e.toString()) // When it is a Toast then we can use "e.message"
        }
    }


}

// Themes.xml
/*
<resources xmlns:tools="http://schemas.android.com/tools">
    <!-- Base application theme. -->
    <style name="Theme.Firebase_learning" parent="Theme.MaterialComponents.DayNight.DarkActionBar">
        <!-- Primary brand color. -->
        <item name="colorPrimary">@color/purple_500</item>
        <item name="colorPrimaryVariant">@color/purple_700</item>
        <item name="colorOnPrimary">@color/white</item>
        <!-- Secondary brand color. -->
        <item name="colorSecondary">@color/teal_200</item>
        <item name="colorSecondaryVariant">@color/teal_700</item>
        <item name="colorOnSecondary">@color/black</item>
        <!-- Status bar color. -->
        <item name="android:statusBarColor">?attr/colorPrimaryVariant</item>
        <!-- Customize your theme here. -->
    </style>
</resources>
 */

// Colours.xml
/*
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="purple_200">#FFBB86FC</color>
    <color name="purple_500">#FF6200EE</color>
    <color name="purple_700">#FF3700B3</color>
    <color name="teal_200">#FF03DAC5</color>
    <color name="teal_700">#FF018786</color>
    <color name="black">#FF000000</color>
    <color name="white">#FFFFFFFF</color>
</resources>
 */