package com.DataRunner.CountryTown

import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide

class AutoScrollAdapter(context: Context, data: ArrayList<Int>) : PagerAdapter() {
    val context: Context = context
    val data: ArrayList<Int> = data

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater =
            context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.image_container, null)
        val imageContainer: ImageView =
            view.findViewById<View>(R.id.image_container) as ImageView
        Glide.with(context).load(data[position])
            .into(imageContainer)
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