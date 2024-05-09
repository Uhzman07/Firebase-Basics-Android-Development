package com.example.firebase_learning

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

private const val CHANNEL_ID = "my_channel"


// Note that we must always add our service to the manifest file under "application"
// Also note that the permission "<uses-permission android:name="com.google.android.c2dm.permission.RECEIVE"/>" is not suggested but still, it is essential for this project and it should be added inside the service tag also
// Note that we also have to add an intent filter to that services class
// Please check the manifest file for this project
// Inside the intent filter of the services tag then we will have the actions that will allow us to be able to send and receive


class FirebaseService : FirebaseMessagingService(){
    // To have access to the token as it changes
    companion object{
        var sharedPref: SharedPreferences? = null
        var token : String?
        // Using getters and setters
            get(){
                return sharedPref?.getString("token", "") // Note that "token" is the key while an empty string is the default
            }
            // Note that "value" here represents the value that is being assigned to the token property
            set(value){
                sharedPref?.edit()?.putString("token",value)?.apply() // Note that some people use ".commit()" which is synchronous by ".apply()" is asynchronous
            }


    }

    // To then save our token into our shared Preference
    override fun onNewToken(newToken: String) { // This is a built in function
        super.onNewToken(newToken)
        token = newToken // Then we set out "token" to the new Token that will be generated
    }

    // This class is going to be used to receive the notifications that we desire
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // To create the intent that we are going to use to open our main activity
        val intent = Intent(this, MainActivity::class.java) // here we are creating an intent that is targeting our main activity
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID =  Random.nextInt() // Since we do not want a notification to override another but we want to display different ones at the same time, we need to use a different notification id

        // To then check if we have android oreo and above
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // This is used to make sure that all other activities are cleared until the main activity is at the top of our stack
        // This is very useful when we have a project with a lot of different activities

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE // This is used instead as a newer way
        )  // "FLAG_ONE_SHOT" here is used to tell that we just want to use the pending intent just once and then we want to terminate it after that

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                // Note that the message here is from the remote message which had been derived from the other source and then we want to transfer it
            // Also note that ".data" here is used like an hash map which is used to map out the required part of that message
            .setContentTitle(message.data["title"]) // This is used like an hashmap to get the title of the desired message
            .setContentText(message.data["message"])
            .setSmallIcon(R.drawable.ic_android_black)
            .setAutoCancel(true) // This makes the notification to be deleted when we click on it
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationID,notification)

    }
    // We also have to create the notification channel
    // Note that the notification channel is the one that we see when we open the settings
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channelName = "channelName"
        val channel = NotificationChannel(CHANNEL_ID,channelName,IMPORTANCE_HIGH).apply{ // "Note that IMPORTANCE_HIGH is imported"
            description = "My channel description"
            enableLights(true) // This is to enable the flashing of light
            lightColor = Color.GREEN // This is used to define the colour
        }
        notificationManager.createNotificationChannel(channel) // Then we finally need to create the notification
    }

}