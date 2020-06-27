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
        dataList = arrayListOf<Data>()
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.all -> {
                    btn.text = "전체"
                    parsing("전체")
                }
                R.id.incheon -> {
                    btn.text = "수도권"
                    parsing("수도권")
                }
                R.id.gwangju -> {
                    btn.text = "강원권"
                    parsing("강원권")
                }
                R.id.daejeon -> {
                    btn.text = "충청권"
                    parsing("충청권")
                }
                R.id.ulsan -> {
                    btn.text = "전라권"
                    parsing("전라권")
                }
                R.id.sejong -> {
                    btn.text = "제주권"
                    parsing("제주권")
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
                if(checkSido == "수도권" && sido == "경기도" || sido == "인천광역시")




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
                    obj.getString("데이터기준일자"),
                    obj.getString("체험마을사진"),
                    obj.getString("보유시설정보")
                )
                dataList.add(listLine)
            }
        } catch (e: Exception) {
            val listLine = Data(e.toString(), "오류","오류", "오류", "오류", "오류", "오류", "오류", "오류", "오류", 0.0, 0.0,  "오류", "오류", "오류")
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
