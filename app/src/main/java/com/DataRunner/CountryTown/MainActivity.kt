package com.DataRunner.CountryTown

import android.os.Bundle
import android.os.StrictMode
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.main_layout.*
import kotlinx.android.synthetic.main.main_toolbar.*
import org.json.JSONArray

class MainActivity : AppCompatActivity() {

    var dataList = arrayListOf<data>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        parsing()


    }

    fun parsing() {
        val DATAAdapter = dataAdapter(this, dataList)
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
                var sido = obj.getString("시도명")
                val addr = obj.getString("소재지도로명주소")
                val master = obj.getString("대표자성명")
                val number = obj.getString("대표전화번호")
                var link = obj.getString("홈페이지주소")

                val listLine = data(title, sido, addr, master, "A", "B")
                dataList.add(listLine)
            }
        } catch (e: Exception) {
            val listLine = data("e" + e.toString(), "오류","오류", "오류", "오류", "오류")
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
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
