package com.messenger

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btn_signup.setOnClickListener {
            signup()
        }

        btn_to_signin.setOnClickListener {
            Log.d("MainActivity", "Try to show login activity")

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        btn_img.setOnClickListener {
            Toast.makeText(this, "Your image", Toast.LENGTH_SHORT).show()
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Toast.makeText(this, "Photo was Selected", Toast.LENGTH_SHORT).show()
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            profile_View.setImageBitmap(bitmap)

            btn_img.alpha = 0f

        }
    }

    private fun signup() {
        Log.d("signup", "d5all lel fn signup")
        val email = email_id.text.toString()
        val pass = pass_id.text.toString()
        if (email.isEmpty() || pass.isEmpty() || (btn_img.drawableState==null)) {
            Toast.makeText(this, "Please Fill out your Username & Email & Password & select a photo", Toast.LENGTH_SHORT).show()
            return

        }

        //BD Auth
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener {
                if (!it.isComplete) return@addOnCompleteListener
                Toast.makeText(this, "Signed Up Successfully", Toast.LENGTH_SHORT).show()
                uplaodImage()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "The Email/Password are badly formatted or the Email already exist ",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun uplaodImage() {
        if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference(("/image/$filename"))
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Image Uplaoded Successffully ${it.metadata?.path}",
                    Toast.LENGTH_SHORT
                ).show()
                ref.downloadUrl.addOnSuccessListener {
                    it.toString()
                    Log.d("SignUpActivity", "File Location $it")
                    saveToFireBase(it.toString())

                }
            }
            .addOnFailureListener {
                //do some logging
            }
    }

    private fun saveToFireBase(imgUrl: String) {
        Log.d("signup", "entered save to fire base")
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid, username_id.text.toString(), imgUrl)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("SignUpActivity", "user has been save to fire base")
                val intent = Intent(this, LastMessages::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
    }
}
@Parcelize
data class User(val uid: String="", val username: String="", val imgUrl: String=""): Parcelable