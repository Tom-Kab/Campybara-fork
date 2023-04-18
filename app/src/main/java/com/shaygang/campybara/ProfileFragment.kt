package com.shaygang.campybara

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class ProfileFragment : Fragment() {

    private var selectedPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val firstNameField = view?.findViewById<TextView>(R.id.firstName)
        val lastNameField = view?.findViewById<TextView>(R.id.lastName)
        val emailField = view?.findViewById<TextView>(R.id.emailEt)
        val phoneNbField = view?.findViewById<TextView>(R.id.phoneNumber)
        val dateOfBirthField = view?.findViewById<TextView>(R.id.dateOfBirth)
        val photoField = view?.findViewById<Button>(R.id.updatePhoto)
        val photoView = view?.findViewById<ImageView>(R.id.updatePhotoView)

        firstNameField?.text = firstName
        lastNameField?.text = lastName
        emailField?.text = email
        phoneNbField?.text = phoneNb
        dateOfBirthField?.text = dateOfBirth
        if (photoView != null) {
            Glide.with(this)
                .load(profileImageUrl)
                .into(photoView)
            photoField?.alpha = 0f
        }

        photoField?.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        val updateProfileBtn = view?.findViewById<Button>(R.id.updateProfileBtn)
        updateProfileBtn?.setOnClickListener {
            context?.let { it1 ->
                MaterialAlertDialogBuilder(it1)
                    .setMessage("Are you sure?")
                    .setNegativeButton("No") { _, _ -> }
                    .setPositiveButton("Yes") { _, _ ->
                        if (firstNameField?.text.toString().isNotEmpty() && lastNameField?.text.toString()
                                .isNotEmpty() && phoneNbField?.text.toString().isNotEmpty()
                        ) {
                            uploadImageToFirebaseStorage(firstNameField, lastNameField, phoneNbField)
                        } else {
                            firstNameField?.text = firstName
                            lastNameField?.text = lastName
                            phoneNbField?.text = phoneNb
                            if (photoView != null) {
                                Glide.with(this)
                                    .load(profileImageUrl)
                                    .into(photoView)
                                photoField?.alpha = 0f
                            }
                            Toast.makeText(context, "Empty Fields Are Not Allowed !!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .show()
            }
        }

        return view
    }

    private fun deleteOldImageFromFirebaseStorage(profileImageUrl: String) {
        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(profileImageUrl)
        storageRef.delete()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val photoField = view?.findViewById<Button>(R.id.updatePhoto)
        val photoView = view?.findViewById<ImageView>(R.id.updatePhotoView)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data
            if (photoView != null) {
                Glide.with(this)
                    .load(selectedPhotoUri)
                    .into(photoView)
                photoField?.alpha = 0f
            }
        }
    }

    private fun uploadImageToFirebaseStorage(firstNameField: TextView?, lastNameField: TextView?, phoneNbField: TextView?) {
        if (selectedPhotoUri != null) {
            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images_profile/$filename")
            ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener {
                        profileImageUrl?.let { it1 -> deleteOldImageFromFirebaseStorage(it1) }
                        updateProfile(firstNameField?.text.toString(), lastNameField?.text.toString(), phoneNbField?.text.toString(), it.toString())
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Image could not be uploaded !!", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updateProfile(
        newFirstName: String,
        newLastName: String,
        newPhoneNb: String,
        newProfileImageUrl: String
    ) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("users")

        val user =
            dateOfBirth?.let { email?.let { it1 ->
                User(uid, newFirstName, newLastName, newPhoneNb, it, it1, newProfileImageUrl)
            } }
        ref.child(uid).setValue(user)
            .addOnSuccessListener {
                firstName = newFirstName
                lastName = newLastName
                phoneNb = newPhoneNb
                profileImageUrl = newProfileImageUrl
                Toast.makeText(context, "Profile Updated Successfully !!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Could Not Update Profile !!", Toast.LENGTH_SHORT).show()
            }
    }
}