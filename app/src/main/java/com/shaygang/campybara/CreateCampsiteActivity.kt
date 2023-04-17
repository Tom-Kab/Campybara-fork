package com.shaygang.campybara

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.shaygang.campybara.databinding.ActivityCreateCampsiteBinding
import java.io.IOException
import java.lang.Integer.parseInt
import java.util.*


class CreateCampsiteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateCampsiteBinding
    private var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_campsite)
        binding = ActivityCreateCampsiteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnExit.setOnClickListener{
            exitCampsiteCreation()
        }

        binding.confirmBtn.setOnClickListener {
            uploadImageToFirebaseStorage()
        }

        binding.selectPhoto.setOnClickListener {
            chooseImage()
        }
    }

    private fun exitCampsiteCreation() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
    private fun chooseImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                binding.selectPhotoView.setImageBitmap(bitmap)
                binding.selectPhoto.alpha = 0f
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    private fun uploadImageToFirebaseStorage() {
        if (imageUri != null) {
            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images_campsite/$filename")
            ref.putFile(imageUri!!)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener {
                        confirmCampsiteCreation(it.toString())
                    }
                }
                .addOnFailureListener {
                    Log.d("CreateCampsiteActivity", "Image could not be uploaded !!")
                }
        } else {
            confirmCampsiteCreation("gs://campybara-f185f.appspot.com/images_campsite/campy.jpg")
        }
    }
    private fun confirmCampsiteCreation(imageUrl: String) {
        var name = binding.csNameET.text.toString()
        var description = binding.csDescET.text.toString()
        var capacity : Int
        try {
             capacity = parseInt(binding.csCapET.text.toString()) }
        catch (e: NumberFormatException) {
            Toast.makeText(this, "Enter a value for capacity!", Toast.LENGTH_SHORT).show()
            return
        }
        if (name.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Name and description cannot be blank!", Toast.LENGTH_SHORT).show()
        } else
        if (capacity < 1) {
            Toast.makeText(this, "Capacity must be greater than 0!", Toast.LENGTH_SHORT).show()
        } else {

            val ref = FirebaseDatabase.getInstance().getReference("campsites")
            val campsite = Campsite(name, description, capacity, imageUrl)
            ref.push().setValue(campsite).addOnSuccessListener {
                Toast.makeText(this,"Successfully added campsite!", Toast.LENGTH_SHORT).show()
                exitCampsiteCreation()
            }
        }
    }
}

class Campsite(val name: String, val description: String, val capacity : Int, val imageUrl : String)