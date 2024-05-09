package com.example.firebasetutorials

import android.content.Intent
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

const val REQUEST_CODE_SIGN_IN = 0

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    // For the firebase
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        // To get the token for this, we have to change the level "android" of our project to "projects" so that we can easily get the "google-services.json" file in the app folder
        // Then we go back to the android view after copying the client id and pasting it in the  "strings.xml" file
        binding.btnGoogleSignIn.setOnClickListener {
            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestIdToken(getString(R.string.webclient_id)) // This is the id that we got previously
                .requestEmail()
                //.requestProfile() // Note that we can request profile also
                .build()
            // To create  the sign clients
            val signInClient = GoogleSignIn.getClient(this,options)
            signInClient.signInIntent.also {  // This is used to get a new intent for it
                startActivityForResult(it, REQUEST_CODE_SIGN_IN)
            }


        }
    }
    private fun googleAuthForFirebase(account: GoogleSignInAccount){
        val credentials = GoogleAuthProvider.getCredential(account.idToken,null)
        CoroutineScope(Dispatchers.IO).launch{
            try {
                // Note that when we are signing it with google, we have to make use of the credentials instead of using just the email and password
                auth.signInWithCredential(credentials).await()
                withContext(Dispatchers.Main){
                    Toast.makeText(this@MainActivity,"Successfully logged in",Toast.LENGTH_LONG).show()
                }


            }
            catch (e:Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Note that this function is useful whenever we start another activity in our code
    // This is also used to get the client
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Note that we can only make use of the request code here and not the result code since it is always negative and will never work
        if(requestCode == REQUEST_CODE_SIGN_IN){
            val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
            // To check if the account is not null
            account?.let{
                googleAuthForFirebase(it)
                // Note that for this, to work...
                // we have to go to the right hand side of the page and then click on gradle then we see our project name then app then tasks then android then signingReport then we click on it to generate the "SHA-1" code
                // We then copy this code then go to the firebase console through google
                // We then go to project settings on it then choose the project then we go to add fingerprints where we paste the sha1 key code and then it works
                // Note that we also have to enable google in the authentication also
                // Note that to get how to get the signingReport, we can just check on youtube
            }
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