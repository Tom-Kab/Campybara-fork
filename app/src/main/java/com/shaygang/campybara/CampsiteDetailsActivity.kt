package com.shaygang.campybara

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.RatingBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.shaygang.campybara.User.Companion.loadUserFromUid
import com.shaygang.campybara.databinding.ActivityCampsiteDetailsBinding
import org.w3c.dom.Text

class CampsiteDetailsActivity : AppCompatActivity() {

    private lateinit var campsiteName : String
    private lateinit var campsiteImageUrl : String
    private lateinit var binding: ActivityCampsiteDetailsBinding
    private lateinit var campsiteOwnerUid : String;
    private lateinit var campsiteOwner : User;
    private lateinit var campsiteId : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_campsite_details)
        binding = ActivityCampsiteDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val extras = intent.extras;
        campsiteName = extras!!.getString("campsiteName")!!
        campsiteImageUrl = extras.getString("imageUrl")!!
        campsiteOwnerUid = extras.getString("ownerUid")!!
        campsiteId = extras.getString("campsiteId")!!
        supportActionBar?.title = campsiteName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        val titleTextView = binding.campsiteName
        Glide.with(this).load(campsiteImageUrl).placeholder(R.drawable.capy_loading_image).into(binding.campsiteImage)
        titleTextView.text = campsiteName
        loadUserFromUid(campsiteOwnerUid) { user ->
            if (user != null) {
                // TODO: Add a shimmer effect loading
                campsiteOwner = user
                val ownerFullName = user.firstName + " " + user.lastName
                binding.ownerName.text = ownerFullName
                binding.ownerEmail.text = user.email
                Glide.with(this).load(user.profileImageUrl).placeholder(R.drawable.capy_loading_image).into(binding.ownerProfilePic)
            } else {
                // Handle the error
            }
        }
        val reviewHelper = ReviewHelper(campsiteId)
        reviewHelper.populateReviewList {
            val avg = reviewHelper.calculateAvg()
            findViewById<TextView>(R.id.ratingScore).text = avg.toString()
            findViewById<RatingBar>(R.id.ratingBar).rating = avg
            findViewById<TextView>(R.id.ratingText).text = "Based on ${reviewHelper.getReviewCount()} reviews"
        }
        binding.ratingLayout.setOnClickListener{
            val intent = Intent(this, ReviewActivity::class.java)
            intent.putExtra("campsiteId",campsiteId)
            intent.putExtra("campsiteName",campsiteName)
            startActivity(intent)
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}