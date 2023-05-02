package com.shaygang.campybara

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.concurrent.CountDownLatch

class Campsite(val name: String, val description: String? = "No description provided", val capacity: Int? = -1, val imageUrl: String?, val rating: Double? = 0.0, val ownerUID: String) {}

fun getCampsiteFromFirebase(campsiteId: String, callback: (campsite: Campsite?) -> Unit) {
    val campsiteRef = Firebase.database.reference.child("campsites").child(campsiteId)
    campsiteRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                val name = snapshot.child("name").value as String
                val description = snapshot.child("description").value as String?
                val capacity = snapshot.child("capacity").value as Long?
                val imageUrl = snapshot.child("imageUrl").value as String?
                val rating = snapshot.child("rating").value as Double?
                val ownerUID = snapshot.child("ownerUID").value as String
                val campsite = Campsite(name, description, capacity?.toInt(), imageUrl, rating, ownerUID)
                callback(campsite)
            } else {
                callback(null)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w(TAG, "Failed to read value.", error.toException())
            callback(null)
        }
    })
}

fun getAllCampsitesFromFirebase(callback: (campsites: List<Campsite>?) -> Unit) {
    val campsiteIds = mutableListOf<String>()
    val campsites = mutableListOf<Campsite>()
    val campsiteRef = Firebase.database.reference.child("campsites")
    campsiteRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                for (childSnapshot in snapshot.children) {
                    val campsiteId = childSnapshot.key as String
                    campsiteIds.add(campsiteId)
                }
                val fetchCountdown = CountDownLatch(campsiteIds.size)
                for (campsiteId in campsiteIds) {
                    getCampsiteFromFirebase(campsiteId) { campsite ->
                        if (campsite != null) {
                            campsites.add(campsite)
                        }
                        fetchCountdown.countDown()
                    }
                }
                fetchCountdown.await()
                callback(campsites)
            } else {
                callback(null)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w(TAG, "Failed to read value.", error.toException())
            callback(null)
        }
    })
}