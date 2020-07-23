package com.DataRunner.CountryTown

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class Utils() {

    fun getJsonStrFromURL(uri: Uri?): String {
        val sb = StringBuilder()
        try {
            val url = URL(uri.toString())
            val conn = url.openConnection() as HttpURLConnection
            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                val br =
                    BufferedReader(InputStreamReader(conn.inputStream), 1024)
                var line: String? = null
                while (br.readLine().also { line = it } != null) {
                    sb.append(line)
                }
                br.close()
            }
        } catch (ex: IOException) {
            Log.d("app", ex.toString())
        }
        return sb.toString()
    }

    fun saveData(context: Context, key: String) {
        val rootRef = FirebaseDatabase.getInstance().reference
        val myRef = rootRef.child(key)
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(dataSnapshot: DatabaseError) {
                val t = Toast.makeText(context, "인터넷 접속이 원활하지 않습니다.", Toast.LENGTH_LONG)
                t.show()
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var jsonArray = JSONArray()
                for (singleTown in dataSnapshot.children) {
                    var town = JSONObject()
                    if (singleTown.hasChildren()) {
                        for (property in singleTown.children) {
                            town.put(property.key, property.value.toString())
                        }
                    } else {
                        town.put(singleTown.key, singleTown.value.toString())
                    }
                    jsonArray.put(town)
                }

                val sharedPreferences: SharedPreferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString(key, jsonArray.toString()).apply()
            }
        })
    }

    fun loadData(context: Context, key: String): String? {
        val sharedPreferences =
            context.getSharedPreferences("preferences", Context.MODE_PRIVATE)
        val data = sharedPreferences.getString(key, "")
        return data
    }

    fun setAdapter(context: Context, view: RecyclerView, townDataList: ArrayList<TownData>) {
        view.adapter = TownDataAdapter(this, townDataList) { data ->
            val toDetailIntent = Intent(context, Detail::class.java)
            val b = Bundle()
            b.putParcelable("parceledData", data)
            toDetailIntent.putExtra("bundleData", b)
            context.startActivity(toDetailIntent)
        }
        view.layoutManager = LinearLayoutManager(context)
        view.setHasFixedSize(true)
    }

    fun addDistance(
        townDataList: ArrayList<TownData>,
        myLoc: Location
    ) {
        for (townData in townDataList) {
            val townLoc = Location("townLoc")
            townLoc.latitude = townData.lat
            townLoc.longitude = townData.lon
            val distance = myLoc.distanceTo(townLoc)
            if (distance < 10000) {
                townData.distance = String.format("%.1f", distance/1000)
            } else {
                townData.distance = (distance/1000).toInt().toString()
            }
        }
        townDataList.sortWith(
            Comparator {
                    data1, data2 -> data1.distance!!.toFloat().toInt() - data2.distance!!.toFloat().toInt()
            }
        )
    }

    fun search(townDataList: ArrayList<TownData>, charArray: Array<String>, searchType: String="user"): ArrayList<TownData> {
        var searchDataList: ArrayList<TownData> = ArrayList()
        if (searchType == "location" || charArray[0] == "전국") {
            searchDataList = townDataList
        } else if (searchType == "user" && charArray[0] != "") {
            for (i in townDataList.indices) {
                if (townDataList[i].addr.contains(charArray[0]) ||
                    townDataList[i].title.contains(charArray[0]) ||
                    townDataList[i].programType.contains(charArray[0]) ||
                    townDataList[i].programContent.contains(charArray[0]))
                    searchDataList.add(townDataList[i])
            }
        } else if (searchType == "recommend") {
            for (i in townDataList.indices) {
                for (char in charArray) {
                    if (townDataList[i].townId == char) {
                        searchDataList.add(townDataList[i])
                        break
                    }
                }
            }
        } else if (searchType == "classify") {
            for (i in townDataList.indices) {
                for (char in charArray) {
                    if (townDataList[i].programType.contains(char)) {
                        searchDataList.add(townDataList[i])
                        break
                    }
                }
            }
        }
        return searchDataList
    }

    fun getTownDataList(context: Context, checkSido : String = "전국"): ArrayList<TownData> {
        return parsing(context, checkSido)
    }

    fun parsing(context: Context, checkSido: String = "전국"): ArrayList<TownData> {
        var ret = arrayListOf<TownData>()
        val jsonString = loadData(context, "town")
        val jArray = JSONArray(jsonString)

        //start
        StrictMode.enableDefaults()
        try {
            // 모든 공지 noticeList 에 저장
            for (i in 0 until jArray.length()) {

                val obj = jArray.getJSONObject(i)
                val sido = obj.getString("시도명")
                if (checkSido == "전국" ||
                    sido.contains(checkSido) ||
                    (sido == "경기도" && checkSido == "수도") ||
                    (sido == "인천광역시" && checkSido == "수도") ||
                    (sido == "세종특별자치시" && checkSido == "충청") ||
                    (sido == "대전광역시" && checkSido == "충청") ||
                    (sido == "광주광역시" && checkSido == "광주") ||
                    (sido == "울산광역시" && checkSido == "경상")
                ) {
                    val listLine = TownData(
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
                        obj.getString("일련번호"),null,
                        obj.getString("추가내용")
                    )
                    ret.add(listLine)
                }
            }
        } catch (e: Exception) {
            val listLine = TownData(
                e.toString(),
                "오류",
                "오류",
                "오류",
                "오류",
                "오류",
                "오류",
                "오류",
                "오류",
                "오류",
                0.0,
                0.0,
                "오류",
                "오류",
                "오류",
                "오류"
            )
            ret.add(listLine)
        }
        return ret
    }

    fun getJsonDataFromAsset(context: Context, fileName: String): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }
}