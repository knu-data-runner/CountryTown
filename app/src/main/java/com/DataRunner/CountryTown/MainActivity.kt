package com.DataRunner.CountryTown

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.naver.maps.map.NaverMapSdk
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_layout.*
import kotlinx.android.synthetic.main.main_toolbar.*
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    var dataList = arrayListOf<Data>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        setSupportActionBar(toolbar)
        parsing("전체")

        val clickListener = View.OnClickListener { view ->
            when (view.id) {
                R.id.btn -> {
                    showPopup(view)
                }
            }
        }
        btn.setOnClickListener(clickListener)

        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NaverCloudPlatformClient(getSecret("naver", "CLIENT_ID"))
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
        var popupMenu = PopupMenu(this, view)
        var inflater = popupMenu.menuInflater
        inflater.inflate(R.menu.popup_menu, popupMenu.menu)
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.all -> {
                    btn.text = "전체"
                    dataList = arrayListOf<Data>()
                    parsing("전체")
                }
                R.id.incheon -> {
                    btn.text = "인천"
                    dataList = arrayListOf<Data>()
                    parsing("인천광역시")
                }
                R.id.gwangju -> {
                    btn.text = "광주"
                    dataList = arrayListOf<Data>()
                    parsing("광주광역시")
                }
                R.id.daejeon -> {
                    btn.text = "대전"
                    dataList = arrayListOf<Data>()
                    parsing("대전광역시")
                }
                R.id.ulsan -> {
                    btn.text = "울산"
                    dataList = arrayListOf<Data>()
                    parsing("울산광역시")
                }
                R.id.sejong -> {
                    btn.text = "세종"
                    dataList = arrayListOf<Data>()
                    parsing("세종특별자치시")
                }
                R.id.gyeonggi -> {
                    btn.text = "경기"
                    dataList = arrayListOf<Data>()
                    parsing("경기도")
                }
                R.id.gangwon -> {
                    btn.text = "강원"
                    dataList = arrayListOf<Data>()
                    parsing("강원도")
                }
                R.id.chungbuk -> {
                    btn.text = "충북"
                    dataList = arrayListOf<Data>()
                    parsing("충청북도")
                }
                R.id.chungnam -> {
                    btn.text = "충남"
                    dataList = arrayListOf<Data>()
                    parsing("충청남도")
                }
                R.id.jeonbuk -> {
                    btn.text = "전북"
                    dataList = arrayListOf<Data>()
                    parsing("전라북도")
                }
                R.id.jeonnam -> {
                    btn.text = "전남"
                    dataList = arrayListOf<Data>()
                    parsing("전라남도")
                }
                R.id.gyeongbuk -> {
                    btn.text = "경북"
                    dataList = arrayListOf<Data>()
                    parsing("경상북도")
                }
                R.id.gyeongnam -> {
                    btn.text = "경남"
                    dataList = arrayListOf<Data>()
                    parsing("경상남도")
                }
                R.id.jeju -> {
                    btn.text = "제주"
                    dataList = arrayListOf<Data>()
                    parsing("제주특별자치도")
                }
            }
            true
        }
    }

    private fun parsing(checkSido : String) {
        val dataAdapter = DataAdapter(this, dataList) { data ->
            val toDetailIntent = Intent(this, Detail::class.java)
            val b = Bundle()
            b.putParcelable("parceledData", data)
            toDetailIntent.putExtra("bundleData", b)
            startActivity(toDetailIntent)
        }
        result.adapter = dataAdapter

        // LayoutManager 설정. RecyclerView 에서는 필수
        val lm = LinearLayoutManager(this)
        result.layoutManager = lm
        result.setHasFixedSize(true)

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
                if(sido!=checkSido && checkSido!="전체") { continue }

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
                    obj.getString("데이터기준일자")
                )
                dataList.add(listLine)
            }
        } catch (e: Exception) {
            val listLine = Data(e.toString(), "오류","오류", "오류", "오류", "오류", "오류", "오류", "오류", "오류", 0.0, 0.0,  "오류")
            dataList.add(listLine)
        }
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when(item.itemId) {

            R.id.action_main -> {
                var intent = Intent(this, MainActivity :: class.java)
                startActivity(intent)
                return true
            }
            R.id.action_settings -> {
                var intent = Intent(this, MainActivity :: class.java)
                startActivity(intent)
                return true
            }
            R.id.action_license -> {
                var intent = Intent(this, LicenseActivity :: class.java)
                startActivity(intent)
                return true
            }
            R.id.action_maker -> {
                var intent = Intent(this, MadebyActivity :: class.java)
                startActivity(intent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
