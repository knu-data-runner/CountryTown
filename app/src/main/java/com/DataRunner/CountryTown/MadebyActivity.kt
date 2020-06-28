package com.DataRunner.CountryTown


import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.madeby_layout.*
import org.json.JSONArray


class MadebyActivity : AppCompatActivity() {
    var madeList = arrayListOf<Made_Data>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.madeby_layout)

        parsing()
    }

    private fun parsing() {
        val madeByAdapter =  MadeAdapter(this, madeList)
        madeBy.adapter = madeByAdapter
        //start
        StrictMode.enableDefaults()
        try {
            val assetManager = resources.assets
            val inputStream = assetManager.open("made.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val jArray = JSONArray(jsonString)

            // 모든 공지 noticeList 에 저장
            for (i in 0 until jArray.length()) {

                val obj = jArray.getJSONObject(i)

                val listLine = Made_Data(
                    obj.getString("이름"),
                    obj.getString("이메일"),
                    obj.getString("깃주소"),
                    obj.getString("소속"),
                    obj.getString("사진")
                )
                madeList.add(listLine)
            }
        } catch (e: Exception) {
            val listLine = Made_Data(e.toString(), "오류", "오류", "오류", "오류")
            madeList.add(listLine)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when(item.itemId) {

            R.id.action_main -> {
                var intent = Intent(this, MainActivity :: class.java)
                return true
            }
            R.id.action_settings -> {
                var intent = Intent(this, MainActivity :: class.java)
                return true
            }
            R.id.action_license -> {
                var intent = Intent(this, MainActivity :: class.java)
                return true
            }
            R.id.action_maker -> {
                var intent = Intent(this, MainActivity :: class.java)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
