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
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_layout.*
import kotlinx.android.synthetic.main.main_toolbar.*
import org.json.JSONArray

class MainActivity : AppCompatActivity() {

    var dataList = arrayListOf<Data>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        setSupportActionBar(toolbar)

        parsings("전체")

        val clickListener = View.OnClickListener { view ->
            when (view.id) {
                R.id.btn -> {
                    showPopup(view)
                }
            }
        }
        btn.setOnClickListener(clickListener)
    }
    fun showPopup(view: View){
        var popupMenu = PopupMenu(this, view)
        var inflater = popupMenu.menuInflater
        inflater.inflate(R.menu.popup_menu, popupMenu.menu)
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.all -> {
                    btn.text = "전체"
                    dataList = arrayListOf<Data>()
                    parsings("전체")
                }
                R.id.incheon -> {
                    btn.text = "인천"
                    dataList = arrayListOf<Data>()
                    parsings("인천광역시")
                }
                R.id.gwangju -> {
                    btn.text = "광주"
                    dataList = arrayListOf<Data>()
                    parsings("광주광역시")
                }
                R.id.daejeon -> {
                    btn.text = "대전"
                    dataList = arrayListOf<Data>()
                    parsings("대전광역시")
                }
                R.id.ulsan -> {
                    btn.text = "울산"
                    dataList = arrayListOf<Data>()
                    parsings("울산광역시")
                }
                R.id.sejong -> {
                    btn.text = "세종"
                    dataList = arrayListOf<Data>()
                    parsings("세종특별자치시")
                }
                R.id.gyeonggi -> {
                    btn.text = "경기"
                    dataList = arrayListOf<Data>()
                    parsings("경기도")
                }
                R.id.gangwon -> {
                    btn.text = "강원"
                    dataList = arrayListOf<Data>()
                    parsings("강원도")
                }
                R.id.chungbuk -> {
                    btn.text = "충북"
                    dataList = arrayListOf<Data>()
                    parsings("충청북도")
                }
                R.id.chungnam -> {
                    btn.text = "충남"
                    dataList = arrayListOf<Data>()
                    parsings("충청남도")
                }
                R.id.jeonbuk -> {
                    btn.text = "전북"
                    dataList = arrayListOf<Data>()
                    parsings("전라북도")
                }
                R.id.jeonnam -> {
                    btn.text = "전남"
                    dataList = arrayListOf<Data>()
                    parsings("전라남도")
                }
                R.id.gyeongbuk -> {
                    btn.text = "경북"
                    dataList = arrayListOf<Data>()
                    parsings("경상북도")
                }
                R.id.gyeongnam -> {
                    btn.text = "경남"
                    dataList = arrayListOf<Data>()
                    parsings("경상남도")
                }
                R.id.jeju -> {
                    btn.text = "제주"
                    dataList = arrayListOf<Data>()
                    parsings("제주특별자치도")
                }


            }
            true
        }
    }


    fun parsings(checkSido : String) {
        val DATAAdapter = DataAdapter(this, dataList)
        result.adapter = DATAAdapter

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
                val title = obj.getString("체험마을명")
                val sido = obj.getString("시도명")
                val addr = obj.getString("소재지도로명주소")
                val master = obj.getString("대표자성명")
                val number = obj.getString("대표전화번호")
                var link = obj.getString("홈페이지주소")
                if(sido.equals(checkSido)) {
                    val listLine = Data(title, sido, addr, master, "A", "B")
                    dataList.add(listLine)
                }
                else if(checkSido.equals("전체")){
                    val listLine = Data(title, sido, addr, master, "A", "B")
                    dataList.add(listLine)
                }
            }
        } catch (e: Exception) {
            val listLine = Data("e" + e.toString(), "오류","오류", "오류", "오류", "오류")
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
