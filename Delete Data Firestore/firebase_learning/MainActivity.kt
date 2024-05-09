package com.example.firebase_learning

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebase_learning.databinding.ActivityMainBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
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
            val person = getOldPerson()
            savePerson(person)
        }

        //subscribeToRealTimeUpdates() // This is triggered when we open the app


        binding.btnRetrieveData.setOnClickListener {
            retrievePersons()
        }

        binding.btnDeletePerson.setOnClickListener {
            val person = getOldPerson()
            deletePerson(person)
        }

        binding.btnUpdatePerson.setOnClickListener {
            val oldPerson = getOldPerson()
            val newPersonMap = getNewPersonMap()
            updatePerson(oldPerson,newPersonMap)
        }



    }
    private fun getOldPerson(): Person{
        val firstName = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()
        val age = binding.etAge.text.toString().toInt()
        return Person(firstName, lastName, age)
    }
    // A map has to have a key value which is a string and then the value can bve anything
    private fun getNewPersonMap() :Map<String,Any>{ // Note that the string here is the key while the "Any" here the variable type
        // Note that the key will represent something like the prefix like the firstName, lastName and then the age
        val firstName = binding.etNewFirstName.text.toString()
        val lastName = binding.etNewLastName.text.toString()
        val age = binding.etNewAge.text.toString() // We won't convert to an integer because there is a possibility that we might want to leave it empty so that it does not crash
        // Then we create a mutable map
        val map = mutableMapOf<String,Any>() // Note that this expects a key which is the String and then an input which is "Any" integer
        if(firstName.isNotEmpty()){
            map["firstName"] = firstName // Note that what is inside the braces like "[]" represents the key
        }
        if(lastName.isNotEmpty()){
            map["lastName"] = lastName
        }
        if(age.isNotEmpty()){
            map["age"] = age.toInt()
        }

        return map

    }

    private fun deletePerson(person: Person) = CoroutineScope(Dispatchers.IO).launch {
        val personQuery = personCollectionRef
            .whereEqualTo("firstName", person.firstName)
            .whereEqualTo("lastName", person.lastName)
            .whereEqualTo("age", person.age)
            .get()
            .await()
        if(personQuery.documents.isNotEmpty()) {
            for(document in personQuery) {
                try {
                   personCollectionRef.document(document.id).delete().await() // This deletes all the data from the person

                    /*
                    personCollectionRef.document(document.id).update(
                        mapOf(
                            "firstName" to FieldValue.delete() // This is to tell fire base to update it but we want to delete that particular field, that is this deletes the firstName
                        )

                    )

                     */
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "No persons matched the query.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updatePerson(person: Person,newPersonMap: Map<String,Any>)= CoroutineScope(Dispatchers.IO).launch {
        // We are going to use a query
        val personQuery = personCollectionRef // Note that the query can easily represent the entire folder itself
            .whereEqualTo("firstName",person.firstName)
            .whereEqualTo("lastName",person.lastName)
            .whereEqualTo("age",person.age)
            .get()
            .await() // What this does it that it gets the Persons that has the given first Name ,the given last name and the given age

        if(personQuery.documents.isNotEmpty()){
            for(document in personQuery){ // Just in case there are more than two documents that match the query
                try {
                    // To just update a part of it, like to only update the firstName field for example
                    //personCollectionRef.document(document.id).update("firstName",person.firstName) // This is not advisable tho
                    // Note that the "document.id" is the id of the document from the query
                    personCollectionRef.document(document.id).set(
                        // Note that we can only set a map with a document in the database
                        newPersonMap, // If we leave the new map alone then it will delete the data that aren't on the map like if age is not on the map it will update it based on the firstName and lastName that is on the map and then delete the age that had been stored on the firebase
                        SetOptions.merge() // Note that we must always put this option so that it doesn't delete the other data that doesn't match the map that we want to put in.


                    ).await()



                } catch(e:Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_LONG).show()
                    }
                }

            }


        } else{
            withContext(Dispatchers.Main){
                Toast.makeText(this@MainActivity, "No person matched the query",Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun subscribeToRealTimeUpdates(){
        personCollectionRef.addSnapshotListener { querySnapshot, firebaseFirestoreException -> // Note that the "querySnapshot reps the data from the database while the other one represents the error"
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
        try {
            // note that a query snapshot is the result that we get from the firestore
            val querySnapshot = personCollectionRef.get().await() // What this does is that it goes to the path that we had defined earlier to get the required data
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