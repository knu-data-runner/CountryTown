package com.DataRunner.CountryTown.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.DataRunner.CountryTown.Data
import com.DataRunner.CountryTown.DataAdapter
import com.DataRunner.CountryTown.Detail
import com.DataRunner.CountryTown.R
import com.naver.maps.map.NaverMapSdk
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.main_layout.view.*
import org.json.JSONArray
import org.json.JSONObject

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var dataList = arrayListOf<Data>()
    private lateinit var root: View

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_home, container, false)
//        homeViewModel =
//            ViewModelProvider(this).get(HomeViewModel::class.java)
//        val textView: TextView = root.findViewById(R.id.text_home)
//        homeViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })

        dataList = parsing()

        val clickListener = View.OnClickListener { view ->
            when (view.id) {
                R.id.btn -> {
                    showPopup(view)
                }
            }
        }
        root.btn.setOnClickListener(clickListener)

        NaverMapSdk.getInstance(container!!.context).client =
            NaverMapSdk.NaverCloudPlatformClient(getSecret("naver", "CLIENT_ID"))
        return root
    }

    private fun getSecret(provider:String, keyArg:String): String {
        val assetManager = resources.assets
        val inputStream= assetManager.open("secret.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val obj = JSONObject(jsonString)
        val secret = obj.getJSONObject(provider)
        return secret.getString(keyArg)
    }

    private fun showPopup(view: View){
        var popupMenu = PopupMenu(activity, view)   // activity: 부모 Activity(Context)
        var inflater = popupMenu.menuInflater
        inflater.inflate(R.menu.popup_menu, popupMenu.menu)
        dataList.clear()
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.all -> {
                    btn.text = "전국"
                    dataList = parsing("전국")
                }
                R.id.sudo -> {
                    btn.text = "수도권"
                    dataList = parsing("수도")
                }
                R.id.kangwon -> {
                    btn.text = "강원권"
                    dataList = parsing("강원")
                }
                R.id.chung -> {
                    btn.text = "충청권"
                    dataList = parsing("충청")
                }
                R.id.jeon -> {
                    btn.text = "전라권"
                    dataList = parsing("전라")
                }
                R.id.kyung -> {
                    btn.text = "경상권"
                    dataList = parsing("경상")
                }
                R.id.jeju -> {
                    btn.text = "제주도"
                    dataList = parsing("제주")
                }
            }
            true
        }
    }

    private fun parsing(checkSido : String = "전국"): ArrayList<Data> {
        var ret = arrayListOf<Data>()
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

        //start
        StrictMode.enableDefaults()
        try {
            val assetManager = resources.assets
            val inputStream= assetManager.open("data.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val jArray = JSONArray(jsonString)

            // 모든 공지 noticeList 에 저장
            for (i in 0 until jArray.length()) {

                val obj = jArray.getJSONObject(i)
                val sido = obj.getString("시도명")
                if (checkSido=="전국" ||
                    sido.contains(checkSido) ||
                    (sido=="경기도" && checkSido=="수도") ||
                    (sido=="인천광역시" && checkSido=="수도") ||
                    (sido=="세종특별자치시" && checkSido=="충청") ||
                    (sido=="대전광역시" && checkSido=="충청") ||
                    (sido=="광주광역시" && checkSido=="광주") ||
                    (sido=="울산광역시" && checkSido=="경상")) {
                    val listLine = Data(
                        obj.getString("체험마을명"),
                        obj.getString("시도명"),
                        obj.getString("시군구명"),
                        obj.getString("체험프로그램구분"),
                        obj.getString("체험내용"),
                        obj.getString("소재지도로명주소"),
                        obj.getString("대표자성명"),
                        obj.getString("대표전화번호"),
                        obj.getString("홈페이지주소"),
                        obj.getString("관리기관명"),
                        obj.getDouble("위도"),
                        obj.getDouble("경도"),
                        obj.getString("데이터기준일자"),
                        obj.getString("일련번호")
                    )
                    ret.add(listLine)
                }
            }
        } catch (e: Exception) {
            val listLine = Data(e.toString(), "오류", "오류","오류", "오류", "오류", "오류", "오류", "오류", "오류", 0.0, 0.0,  "오류", "오류")
            ret.add(listLine)
        }
        return ret
    }
}
