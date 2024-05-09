package com.example.firebasetutorials

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.firebasetutorials.databinding.ActivityMainBinding
import com.example.firebasetutorials.ui.theme.FirebaseTutorialsTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    // For the firebase
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Firebase
        auth = FirebaseAuth.getInstance()
       // auth.signOut() // This is used to sign out the current user initially whenever the app is run
        binding.btnRegister.setOnClickListener {
            registerUser()
        }
        binding.btnLogin.setOnClickListener {
            loginUser()
        }
        binding.btnUpdate.setOnClickListener {
            updateProfile()
        }

    }
    private fun updateProfile(){
        // Note that we are making use of "let" with the current user because we want to associate that method with it
        auth.currentUser?.let{user-> // This is the name that we have given "current user" just to make it more readable
            val username = binding.etUsername.text.toString()
            // To get the photo
            // But here we will set it manually
            val photoURI = Uri.parse("android.resource://$packageName/${R.drawable.profilepic}")
            val profileUpdates = UserProfileChangeRequest.Builder() // This is the code that is used to set up a profile automatically in android so as to be able to refer to its attributes later on, that is it is used to store profile info into the database
                .setDisplayName(username)
                .setPhotoUri(photoURI)
                .build()

            // To then launch the profile picture
            // We want to make this
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Note that the "let" allows the user to make use of the method "updateProfile()" that had been created
                    user.updateProfile(profileUpdates).await() // Note that this "profileUpdates" does everything about the profile update to the user
                    withContext(Dispatchers.Main){
                        checkLoggedInState()
                        Toast.makeText(this@MainActivity,"Successfully updated user profile",Toast.LENGTH_LONG).show()
                    }

                }catch (e: Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_LONG).show()
                    }
                }

            }


        }
    }

    override fun onStart() { // This is used to reserve the login status even when it is restarted
        super.onStart()
        checkLoggedInState() // This will reserve the state when the app is restarted
    }

    private fun registerUser(){
        val email = binding.etEmailRegister.text.toString()
        val password = binding.etPasswordRegister.text.toString()
        if(email.isNotEmpty() && password.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                try{
                    auth.createUserWithEmailAndPassword(email, password).await() // This is used to create a new user and the await here is a coroutine function that is used to wait to create the user account before any other thing else
                    withContext(Dispatchers.Main){
                        checkLoggedInState()
                    }

                }
                catch (e : Exception){ // Note that since we want to print out an error message, we have to use the main dispatcher
                    // The IO dispatchers will not allow any UI component in it
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_LONG).show()
                    }

                }
            }
        }

    }

    private fun loginUser(){
        val email = binding.etLoginEmail.text.toString()
        val password = binding.etLoginPassword.text.toString()
        if(email.isNotEmpty() && password.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                try{
                    auth.signInWithEmailAndPassword(email, password).await() // This is used to sign in the user and the await here is a coroutine function that is used to wait to create the user account before any other thing else
                    withContext(Dispatchers.Main){
                        checkLoggedInState()
                    }

                }
                catch (e : Exception){ // Note that since we want to print out an error message, we have to use the main dispatcher
                    // The IO dispatchers will not allow any UI component in it
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_LONG).show()
                    }

                }
            }
        }

    }
    private fun checkLoggedInState(){
        val user = auth.currentUser
        if(user == null){ // This is to check if the current user is logged in or not
            binding.tvLoggedIn.text ="You are not logged in"

        }else{
            binding.tvLoggedIn.text = "You are logged in!"
            binding.etUsername.setText(user.displayName)
            binding.ivProfilePicture.setImageURI(user.photoUrl)
        }
    }
}


















/*
// Jetpack Compose

 setContent {
            FirebaseTutorialsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }

    }
    @Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FirebaseTutorialsTheme {
        Greeting("Android")
    }

 */