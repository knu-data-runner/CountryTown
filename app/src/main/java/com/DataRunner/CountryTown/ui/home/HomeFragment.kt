package com.DataRunner.CountryTown.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.DataRunner.CountryTown.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_home.view.recommend_layout
import kotlinx.android.synthetic.main.main_layout.view.*
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var container: ViewGroup
    private lateinit var townDataList: ArrayList<TownData>
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
//        homeViewModel =
//            ViewModelProvider(this).get(HomeViewModel::class.java)
//        val textView: TextView = root.findViewById(R.id.text_home)
//        homeViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })

//        this.container = container!!

        val viewPagerImages = arrayListOf(
            R.drawable.ic_weather_sun,
            R.drawable.ic_weather_rain,
            R.drawable.ic_weather_snow
        )
        setPagerImages(viewPagerImages)
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

    private fun setPagerImages(viewPagerImages: ArrayList<Int>) {
        viewPager = root.view_pager
        val scrollAdapter = AutoScrollAdapter(requireContext(), viewPagerImages)
        viewPager.adapter = scrollAdapter

        /*After setting the adapter use the timer */
        val handler = Handler()
        val Update = Runnable {
            if (currentPage === viewPagerImages.size) {
                currentPage = 0
            }
            viewPager.setCurrentItem(currentPage++, true)
        }
        timer = Timer() // This will create a new Thread
        timer!!.schedule(object : TimerTask() {
            // task to be scheduled
            override fun run() {
                handler.post(Update)
            }
        }, DELAY_MS, PERIOD_MS)
    }

//    private fun parse(checkSido : String = "전국"): ArrayList<TownData> {
//        val ret = utils.parsing(container!!.context, checkSido)
//
//        val dataAdapter = TownDataAdapter(this, ret) { data ->
//            val toDetailIntent = Intent(activity, Detail::class.java)   // activity: 부모 Activity(Context)
//            val b = Bundle()
//            b.putParcelable("parceledData", data)
//            toDetailIntent.putExtra("bundleData", b)
//            startActivity(toDetailIntent)
//        }
//        root.result.adapter = dataAdapter
//
//        // LayoutManager 설정. RecyclerView 에서는 필수
//        val lm = LinearLayoutManager(activity)  // activity: 부모 Activity(Context)
//        root.result.layoutManager = lm
//        root.result.setHasFixedSize(true)
//
//        return ret
//    }
}
