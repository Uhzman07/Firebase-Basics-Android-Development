package com.example.firebase_learning

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebase_learning.databinding.ActivityMainBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {

    // To connect to firestore
    // We go to tools then cloud firestore then we connect
    // Note that firebase also a create a different document with a special id for each document that is saved in the collection

    private lateinit var binding : ActivityMainBinding

    private val personCollectionRef = Firebase.firestore.collection("persons") // Note that the collection is created automatically later
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnUploadData.setOnClickListener {
            val firstName = binding.etFirstName.text.toString()
            val lastName = binding.etLastName.text.toString()
            val age = binding.etAge.text.toString().toInt() // Note that if we are going to be converting to an integer we also have to convert to a string initially
            val person = Person(firstName, lastName, age)
            savePerson(person)
        }

       // subscribeToRealTimeUpdates() // This is triggered when we open the app


        binding.btnRetrieveData.setOnClickListener {
            retrievePersons()
        }



    }

    // This is usually used to save something immediately
    private fun subscribeToRealTimeUpdates(){
        personCollectionRef
            // Note  that we can also add a filter to a snapshot listener
            //.whereEqualTo("firstName","Peter") // Note that this is then only considers a real time update for one whose firstName is only equal to Peter
            .addSnapshotListener { querySnapshot, firebaseFirestoreException -> // Note that the "querySnapshot reps the data from the database while the other one represents the error"
            // To check if the exception is not equal to null
            // Note that "let" here is used to refer to that exception that we are considering
            firebaseFirestoreException?.let {
                Toast.makeText(this,it.message,Toast.LENGTH_LONG).show()
                 return@addSnapshotListener // This is because we do not want to return to our code

            }
            // To check if the query snap shot is not equal to null
            querySnapshot?.let {
                val sb = StringBuilder() // this performs the "toString()" function
                // To loop through
                for(document in it){ // That is each document that is inside the collection
                    val person = document.toObject<Person>()
                    sb.append("$person\n")

                }
                binding.tvPersons.text = sb.toString()

            }

        }
    }

    private fun retrievePersons() = CoroutineScope(Dispatchers.IO).launch {

        val fromAge = binding.etFrom.text.toString().toInt()
        val toAge = binding.etTo.text.toString().toInt()

        try {
            // note that a query snapshot is the result that we get from the firestore
            // Also note that this then is used to derive the data that matches that range alone
            val querySnapshot = personCollectionRef // Now instead of getting all the documents we want to filter
                .whereGreaterThan("age",fromAge) // To only derive the one that is gre
                .whereLessThan("age",toAge)
                //.whereEqualTo("firstName", "Peter") // This is used to get the one whose first name is equal to Peter
                .orderBy("age") // This is used to set it according to the order
                .get()
                .await()

            // What this does is that it goes to the path that we had defined earlier to get the required data\
            // Also note that since we have a similar collection name "persons" as that of the one automatically generated one in our fire base cloud, we should get the data from that specific collection
            val sb = StringBuilder() // this performs the "toString()" function
            // To loop through
            for(document in querySnapshot.documents){ // That is each document that is inside the collection
                val person = document.toObject<Person>()// This is used to take the document and then convert it to an object in the form of the data class "Person" which contains the object
                sb.append("$person\n") // this is used to convert all to a string (This makes it easy to convert all to a string at once) and then store them in it

            }
            withContext(Dispatchers.Main){
                binding.tvPersons.text = sb.toString() // We need to convert it to a string also


            }

        } catch (e:Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_LONG).show()
            }

        }
    }
    private fun savePerson(person: Person) = CoroutineScope(Dispatchers.IO).launch {
        // Note that it is a good skill to always use the try and catch block just in case of any sort of error
        try {
            personCollectionRef.add(person).await()
            withContext(Dispatchers.Main){
                Toast.makeText(this@MainActivity,"Successfully saved data.",Toast.LENGTH_LONG).show()
            }

        }catch (e:Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_LONG).show()
            }
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