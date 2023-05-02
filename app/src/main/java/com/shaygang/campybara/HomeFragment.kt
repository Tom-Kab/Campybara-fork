package com.shaygang.campybara

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Outline
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class HomeFragment : Fragment() {
    private lateinit var databaseRef: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var adapter: CampsiteAdapter
    private lateinit var recyclerView: RecyclerView
    private var campsiteList : ArrayList<Campsite> = arrayListOf()
    private lateinit var campsiteIdArrayList: ArrayList<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseRef = firebaseDatabase.getReference("campsites")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseRef = firebaseDatabase.getReference("campsites")
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        campsiteList.clear()
        campsiteInitialize()
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = CampsiteAdapter(campsiteIdArrayList, campsiteList, requireContext())
        recyclerView.adapter = adapter
    }

    private fun campsiteInitialize() {
        campsiteIdArrayList = arrayListOf()
        databaseRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get all children of myRef
                for (childSnapshot in dataSnapshot.children) {
                    val campsiteId = childSnapshot.key!!
                    val imageUrl = childSnapshot.child("imageUrl").value.toString()
                    val campsiteName = childSnapshot.child("name").value.toString()
                    val ownerUid = childSnapshot.child("ownerUID").value.toString()
<<<<<<< HEAD
                    campsiteIdArrayList.add(campsiteId)
                    val locationLat = childSnapshot.child("location").child("latitude").value as Double
                    val locationLng = childSnapshot.child("location").child("longitude").value  as Double
=======
                    val locationLat = childSnapshot.child("location").child("0").value as Double
                    val locationLng = childSnapshot.child("location").child("1").value  as Double
>>>>>>> upstream/main
                    val location = ArrayList<Double>()
                    val campsite = Campsite(campsiteName, " ",-1,imageUrl,3.5,ownerUid, location)
                    location.add(locationLat)
                    location.add(locationLng)
                    Log.d("DB", campsite.name)
                    campsiteList.add(campsite)
                    // Do something with the child key and value
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any errors here
            }
        })
    }
}

class RoundedImageView(context: Context, attrs: AttributeSet) : AppCompatImageView(context, attrs) {
    private var cornerRadius = 10f

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundedImageView)
        cornerRadius = typedArray.getDimension(R.styleable.RoundedImageView_cornerRadius, 10f)
        typedArray.recycle()

        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View?, outline: Outline?) {
                outline?.setRoundRect(0, 0, view!!.width, view.height, cornerRadius)
            }
        }
        clipToOutline = true
    }
}
