package com.DataRunner.CountryTown

import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class AutoScrollAdapter(context: Context, data: ArrayList<String>) : PagerAdapter() {
    val context: Context = context
    val data: ArrayList<String> = data

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater =
            context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.image_container, null)
        val imageContainer: ImageView =
            view.findViewById<View>(R.id.image_container) as ImageView

        // Set image
        val storage = Firebase.storage
        val storageRef = storage.reference
        storageRef.child(data[position]).downloadUrl.addOnSuccessListener {
            Glide.with(context)
                .load(it)
                .into(imageContainer)
        }.addOnFailureListener {}

        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {3
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
        return data.size
    }
}