package com.DataRunner.CountryTown.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.DataRunner.CountryTown.*
import com.naver.maps.map.NaverMapSdk
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.main_layout.view.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


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
//        homeViewModel =
//            ViewModelProvider(this).get(HomeViewModel::class.java)
//        val textView: TextView = root.findViewById(R.id.text_home)
//        homeViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })

        this.container = container!!
        this.townDataList = parse()


        val viewPagerImages = arrayListOf(
            R.drawable.ic_weather_sun,
            R.drawable.ic_weather_rain,
            R.drawable.ic_weather_snow
        )
        setPagerImages(viewPagerImages)

//        val clickListener = View.OnClickListener { view ->
//            when (view.id) {
//                R.id.btn -> {
//                    showPopup(view)
//                }
//            }
//        }
//        root.btn.setOnClickListener(clickListener)

        root.swipe.setOnRefreshListener {
            root.swipe.isRefreshing = false
        }

        NaverMapSdk.getInstance(container!!.context).client =
            NaverMapSdk.NaverCloudPlatformClient(getSecret("naver", "CLIENT_ID"))
        return root
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

    private fun getSecret(provider:String, keyArg:String): String {
        val assetManager = resources.assets
        val inputStream= assetManager.open("secret.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val obj = JSONObject(jsonString)
        val secret = obj.getJSONObject(provider)
        return secret.getString(keyArg)
    }

//    private fun showPopup(view: View){
//        var popupMenu = PopupMenu(activity, view)   // activity: 부모 Activity(Context)
//        var inflater = popupMenu.menuInflater
//        inflater.inflate(R.menu.popup_menu, popupMenu.menu)
//        townDataList.clear()
//        popupMenu.show()
//        popupMenu.setOnMenuItemClickListener {
//            when(it.itemId) {
//                R.id.all -> {
//                    btn.text = "전국"
//                    townDataList = parse("전국")
//                }
//                R.id.sudo -> {
//                    btn.text = "수도권"
//                    townDataList = parse("수도")
//                }
//                R.id.kangwon -> {
//                    btn.text = "강원권"
//                    townDataList = parse("강원")
//                }
//                R.id.chung -> {
//                    btn.text = "충청권"
//                    townDataList = parse("충청")
//                }
//                R.id.jeon -> {
//                    btn.text = "전라권"
//                    townDataList = parse("전라")
//                }
//                R.id.kyung -> {
//                    btn.text = "경상권"
//                    townDataList = parse("경상")
//                }
//                R.id.jeju -> {
//                    btn.text = "제주도"
//                    townDataList = parse("제주")
//                }
//            }
//            true
//        }
//    }

    private fun parse(checkSido : String = "전국"): ArrayList<TownData> {
        val ret = utils.parsing(container!!.context, checkSido)

        val dataAdapter = DataAdapter(this, ret) { data ->
            val toDetailIntent = Intent(activity, Detail::class.java)   // activity: 부모 Activity(Context)
            val b = Bundle()
            b.putParcelable("parceledData", data)
            toDetailIntent.putExtra("bundleData", b)
            startActivity(toDetailIntent)
        }
        root.result.adapter = dataAdapter

        // LayoutManager 설정. RecyclerView 에서는 필수
        val lm = LinearLayoutManager(activity)  // activity: 부모 Activity(Context)
        root.result.layoutManager = lm
        root.result.setHasFixedSize(true)

        return ret
    }
}
