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
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.database.*

class HomeFragment : Fragment() {
    private lateinit var databaseRef: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var adapter: CampsiteAdapter
    private lateinit var recyclerView: RecyclerView
    private var campsiteList : ArrayList<Campsite> = arrayListOf()
    private lateinit var campsiteMap : MutableMap<Campsite,String>
    private lateinit var shimmerFrameLayout : ShimmerFrameLayout

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
        val shimmerFrameLayoutManager = ShimmerFrameLayout(context)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = CampsiteAdapter(campsiteMap, campsiteList, requireContext())
        recyclerView.adapter = adapter
        shimmerFrameLayout = view.findViewById(R.id.shimmerFrameLayout);
        shimmerFrameLayout.startShimmer();
    }

    private fun campsiteInitialize() {
        campsiteMap = mutableMapOf()
        databaseRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get all children of myRef
                shimmerFrameLayout.stopShimmer()
                shimmerFrameLayout.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                for (childSnapshot in dataSnapshot.children) {
                    val imageUrl = childSnapshot.child("imageUrl").value.toString()
                    val campsiteName = childSnapshot.child("name").value.toString()
                    val ownerUid = childSnapshot.child("ownerUID").value.toString()
                    val locationLat = childSnapshot.child("location").child("0").value as Double
                    val locationLng = childSnapshot.child("location").child("1").value  as Double
                    val location = ArrayList<Double>()
                    location.add(locationLat)
                    location.add(locationLng)
                    val campsite = Campsite(campsiteName, " ",-1,imageUrl,3.5,ownerUid, location)
                    Log.d("DB", campsite.name)
                    campsiteList.add(campsite)
                    campsiteMap[campsite] = childSnapshot.key.toString()
                    // Do something with the child key and value
                }
                shimmerFrameLayout.stopShimmer()
                shimmerFrameLayout.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                shimmerFrameLayout.visibility = View.GONE
                Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_LONG).show()            }
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
