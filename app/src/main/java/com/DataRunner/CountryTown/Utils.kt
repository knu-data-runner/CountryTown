package com.DataRunner.CountryTown

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.StrictMode
import android.util.Log
import android.widget.Toast
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
                    for (property in singleTown.children) {
                        town.put(property.key, property.value.toString())
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

    fun parsing(context: Context, checkSido: String = "전국"): ArrayList<Town> {
        var ret = arrayListOf<Town>()
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
                    val listLine = Town(
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
            val listLine = Town(
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

    //위도, 경도 -> GRID X좌표, Y좌표 변환
    fun convertGpsToGrid(mode: Int, lat_X: Double, lng_Y: Double): LatXLngY {
        val TO_GRID = 0
        val RE = 6371.00877 // 지구 반경(km)
        val GRID = 5.0 // 격자 간격(km)
        val SLAT1 = 30.0 // 투영 위도1(degree)
        val SLAT2 = 60.0 // 투영 위도2(degree)
        val OLON = 126.0 // 기준점 경도(degree)
        val OLAT = 38.0 // 기준점 위도(degree)
        val XO = 43.0 // 기준점 X좌표(GRID)
        val YO = 136.0 // 기준점 Y좌표(GRID)

        // LCC DFS 좌표변환 ( code : "TO_GRID"(위경도->좌표, lat_X:위도,  lng_Y:경도), "TO_GPS"(좌표->위경도,  lat_X:x, lng_Y:y) )
        val DEGRAD = Math.PI / 180.0
        val RADDEG = 180.0 / Math.PI
        val re = RE / GRID
        val slat1 = SLAT1 * DEGRAD
        val slat2 = SLAT2 * DEGRAD
        val olon = OLON * DEGRAD
        val olat = OLAT * DEGRAD
        var sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5)
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn)
        var sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5)
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn
        var ro = Math.tan(Math.PI * 0.25 + olat * 0.5)
        ro = re * sf / Math.pow(ro, sn)
        val rs = LatXLngY()
        if (mode == TO_GRID) {
            rs.lat = lat_X
            rs.lng = lng_Y
            var ra = Math.tan(Math.PI * 0.25 + lat_X * DEGRAD * 0.5)
            ra = re * sf / Math.pow(ra, sn)
            var theta = lng_Y * DEGRAD - olon
            if (theta > Math.PI) theta -= 2.0 * Math.PI
            if (theta < -Math.PI) theta += 2.0 * Math.PI
            theta *= sn
            rs.x = Math.floor(ra * Math.sin(theta) + XO + 0.5)
            rs.y = Math.floor(ro - ra * Math.cos(theta) + YO + 0.5)
        } else {
            rs.x = lat_X
            rs.y = lng_Y
            val xn = lat_X - XO
            val yn = ro - lng_Y + YO
            var ra = Math.sqrt(xn * xn + yn * yn)
            if (sn < 0.0) {
                ra = -ra
            }
            var alat = Math.pow(re * sf / ra, 1.0 / sn)
            alat = 2.0 * Math.atan(alat) - Math.PI * 0.5
            var theta = 0.0
            if (Math.abs(xn) <= 0.0) {
                theta = 0.0
            } else {
                if (Math.abs(yn) <= 0.0) {
                    theta = Math.PI * 0.5
                    if (xn < 0.0) {
                        theta = -theta
                    }
                } else theta = Math.atan2(xn, yn)
            }
            val alon = theta / sn + olon
            rs.lat = alat * RADDEG
            rs.lng = alon * RADDEG
        }
        return rs
    }

    inner class LatXLngY {
        var lat = 0.0
        var lng = 0.0
        var x = 0.0
        var y = 0.0
    }
}