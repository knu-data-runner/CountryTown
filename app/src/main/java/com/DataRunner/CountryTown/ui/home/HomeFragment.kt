package com.DataRunner.CountryTown.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.DataRunner.CountryTown.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_home.view.recommend_layout
import org.json.JSONArray
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {

    private lateinit var viewPager: ViewPager
    private lateinit var root: View
    private val utils = Utils()

    private var currentPage = 0
    private var timer: Timer? = null
    private val DELAY_MS: Long = 1500 //delay in milliseconds before task is to be executed
    private val PERIOD_MS: Long = 3000 // time in milliseconds between successive task executions.


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        this.root = inflater.inflate(R.layout.fragment_home, container, false)
        (activity as MainActivity).supportActionBar!!.title = "컨츄리 타운"

        val jsonString = utils.loadData(requireContext(), "home/slide")
        val jArray = JSONArray(jsonString)
        var viewPagerImageUrl = arrayListOf<String>()
        for (i in 0 until jArray.length()) {
            val obj = jArray.getJSONObject(i)
            val keys = obj.keys()
            for (key in keys) {
                viewPagerImageUrl.add(obj.getString(key))
            }
        }

        setPagerImages(viewPagerImageUrl)
        setClickListener()

        return root
    }

    private fun setClickListener() {
        val classifiedLayoutList = arrayOf(
            root.harvest_layout,
            root.makeing_layout,
            root.living_layout,
            root.nature_layout,
            root.tradition_layout,
            root.etc_layout
        )
        for (layout in classifiedLayoutList) {
            layout.setOnClickListener { view ->
                val intent = Intent(activity, ListActivity :: class.java)
                val textView = layout.getChildAt(1) as TextView
                intent.putExtra("query", textView.text)
                intent.putExtra("searchType", "classify")
                startActivity(intent)
            }
        }
        root.recommend_layout.setOnClickListener { view ->
            val intent = Intent(activity, ListActivity :: class.java)
            intent.putExtra("query", "추천")
            intent.putExtra("searchType", "recommend")
            startActivity(intent)
        }
        root.all_layout.setOnClickListener { view ->
            val intent = Intent(activity, SelectLocationActivity :: class.java)
            intent.putExtra("query", "전국")
            startActivity(intent)
        }
    }

    private fun setPagerImages(viewPagerImageUrl: ArrayList<String>) {
        viewPager = root.view_pager
        val scrollAdapter = AutoScrollAdapter(requireContext(), viewPagerImageUrl)
        viewPager.adapter = scrollAdapter

        /*After setting the adapter use the timer */
        val handler = Handler()
        val update = Runnable {
            if (currentPage === viewPagerImageUrl.size) {
                currentPage = 0
            }
            viewPager.setCurrentItem(currentPage++, true)
        }
        timer = Timer() // This will create a new Thread
        timer!!.schedule(object : TimerTask() {
            // task to be scheduled
            override fun run() {
                handler.post(update)
            }
        }, DELAY_MS, PERIOD_MS)
    }
}
