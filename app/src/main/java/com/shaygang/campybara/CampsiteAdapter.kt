package com.shaygang.campybara

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout

class CampsiteAdapter(private val campsiteIdList : ArrayList<String>, val context: Context) : RecyclerView.Adapter<CampsiteAdapter.CampsiteViewHolder>() {
    private var isShimmerVisible = true // boolean to track if shimmer should be visible or not

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CampsiteViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent,false)
        return CampsiteViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return if (isShimmerVisible) 5 else campsiteIdList.size
    }

    override fun onBindViewHolder(holder: CampsiteViewHolder, position: Int) {
        if (isShimmerVisible) { // if shimmer should be visible, set the text to empty string and start shimmer animation
            holder.shimmerLayout.startShimmer()
            Glide.with(holder.itemView).load("").placeholder(R.drawable.capy_loading_image).into(holder.campsiteImage)
        } else {
            var currentItemId = campsiteIdList[position]
            Campsite.getCampsiteFromId(currentItemId) {
                val currentItem = it!!
                Glide.with(holder.itemView).load(currentItem.imageUrl.toString())
                    .placeholder(R.drawable.capy_loading_image).into(holder.campsiteImage)
                holder.campsiteName.text = currentItem.name
                val reviewHelper = ReviewHelper(currentItemId)
                reviewHelper.populateReviewList {
                    holder.campsiteRating.text = String.format("%.1f", reviewHelper.calculateAvg())
                }
                holder.itemView.setOnClickListener {
                    val intent = Intent(context, CampsiteDetailsActivity::class.java)
                    intent.putExtra("campsiteName", currentItem.name)
                    intent.putExtra("imageUrl", currentItem.imageUrl)
                    intent.putExtra("ownerUid", currentItem.ownerUID)
                    intent.putExtra("campsiteId", campsiteMap[currentItem])
                    intent.putExtra("campsiteLocation", currentItem.location)
                    context.startActivity(intent)
                }
             }
           }
        }


    fun setData(campsiteList: ArrayList<Campsite>) {
        isShimmerVisible = false // set shimmer visibility to false
        this.campsiteIdList = campsiteIdList // update data list
        notifyDataSetChanged() // notify adapter of data change
    }

    class CampsiteViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val campsiteImage : ImageView = itemView.findViewById(R.id.campsiteImage)
        val campsiteName : TextView = itemView.findViewById(R.id.campsiteName)
        val shimmerLayout: ShimmerFrameLayout = itemView.findViewById(R.id.shimmerFrameLayout)
        val campsiteRating : TextView = itemView.findViewById(R.id.itemListRatingScore)
    }
}