package com.example.firebase_learning

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebase_learning.databinding.ActivityMainBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.lang.StringBuilder

private const val REQUEST_CODE_IMAGE_PICK =0
class MainActivity : AppCompatActivity() {

    // Note that for this storage project, we also have to set up our storage in the firebase console
    // Note that we also have to add an image folder just with then name of the path that we need in this project
    // Note that here we should name our folder "images" just as we have done
    // Note that we should also try to change the rule so that it doesn't need any form of authentication before it works

    // To connect to firestore
    // We go to tools then cloud firestore then we connect
    // Note that firebase also a create a different document with a special id for each document that is saved in the collection

    // Storing Data in Fire store
    // We need to go to tools -> Firebase -> Storage -> Then connect the storage
    private lateinit var binding : ActivityMainBinding

    // To create the firebase storage reference
    val imageRef = Firebase.storage.reference

    var curFile : Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.ivImage.setOnClickListener{
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*" // this is used to show that the the intent is only meant for images
                startActivityForResult(it, REQUEST_CODE_IMAGE_PICK) // the it here is referring to the intent
            }
        }
        binding.btnUploadImage.setOnClickListener {
            uploadImageToStorage("myImage")
        }
        binding.btnDownloadImage.setOnClickListener {
            downloadImage("myImage")
        }
        binding.btnDeleteImage.setOnClickListener {
            deleteImage("myImage")
        }
    }
    private fun deleteImage(filename: String) = CoroutineScope(Dispatchers.IO).launch {
        try {
            imageRef.child("images/$filename").delete().await() // Note that the await here will allow itt to continue only if it was deleted successfully
            withContext(Dispatchers.Main){
                Toast.makeText(this@MainActivity,"Successfully deleted image",Toast.LENGTH_LONG).show()
            }

        }catch (e:Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun downloadImage(filename: String) = CoroutineScope(Dispatchers.IO).launch {
        try{
            val maxDownloadSize = 5L * 1024 * 1024 // This means 5 mega byte. That is, changing from kb to mb
            val bytes = imageRef.child("images/$filename").getBytes(maxDownloadSize).await() // Note that we are tyring to get the bytes with a set maximum download size
            //                                                     .getFile(destinationUri: File) // Note that we can also use this to download and save directly in Uri or file
            //                                                     .getFile(destinationUri : Uri)
            // Then we use our bytes to generate a bitmap (Also note that the bytes is just like an array)
            val bmp = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
            // then to set the image bitmap we need to use the ui component
            withContext(Dispatchers.Main){
                binding.ivImage.setImageBitmap(bmp) // Note that we are downloading and setting it in an image view here instead of actually downloading it to the phone storage
            }


        } catch (e:Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun uploadImageToStorage(filename: String) = CoroutineScope(Dispatchers.IO).launch {
        try {
            curFile?.let {
                imageRef.child("images/$filename").putFile(it).await()
                Toast.makeText(this@MainActivity,"Successfully uploaded image",Toast.LENGTH_LONG).show()
            }

        }catch (e:Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_IMAGE_PICK){
            data?.data?.let {  // Note that the data here is of type "image"
                curFile = it
                binding.ivImage.setImageURI(it)

            }
        }
    }
}

