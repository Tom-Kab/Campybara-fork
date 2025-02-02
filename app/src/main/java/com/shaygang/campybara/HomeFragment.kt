package com.shaygang.campybara

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
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.database.*

class HomeFragment : Fragment() {
    private lateinit var adapter: CampsiteAdapter
    private lateinit var recyclerView: RecyclerView
    private var campsiteIdList : ArrayList<String> = arrayListOf()
    private lateinit var viewPager: ViewPager2
    private lateinit var pagerAdapter: FragmentStateAdapter
//    private lateinit var shimmerFrameLayout : ShimmerFrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        shimmerFrameLayout = view.findViewById(R.id.shimmerFrameLayout)
//        shimmerFrameLayout.startShimmer()
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.recyclerView)
        campsiteInitialize()
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = CampsiteAdapter(campsiteIdList, requireContext())
        recyclerView.adapter = adapter
    }

    private fun campsiteInitialize() {
//        shimmerFrameLayout.stopShimmer()
//        shimmerFrameLayout.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        Campsite.getCampsiteIds(campsiteIdList) {
            adapter.notifyDataSetChanged()
        }
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
