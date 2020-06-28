package com.DataRunner.CountryTown

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import androidx.annotation.RequiresApi
import androidx.annotation.UiThread
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.google.gson.JsonArray
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import kotlinx.android.synthetic.main.detail_layout.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class Detail : FragmentActivity(), OnMapReadyCallback {
    private var latlan: LatLng = LatLng(0.0, 0.0)
    var TO_GRID = 0
    var TO_GPS = 1

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_layout)

        // Get variables
        val bundleData = intent.getBundleExtra("bundleData")
        val parceledData = bundleData.getParcelable<Data>("parceledData")
        val sigungu = parceledData?.sigungu
        val town = parceledData?.title
        val type = parceledData?.programType
        val content = parceledData?.programContent
        val lat = parceledData!!.lat
        val lon = parceledData!!.lon
        val address = parceledData?.addr
        val number = parceledData?.number
        val dataImgUrl2 = parceledData?.imgUrl2
        val link = parceledData?.link

        // Set variables and processing
        title_sigungu.text = sigungu
        title_town.text = town
        program_type.text = type
        program_content.text = content
        addr.text = address
        latlan = LatLng(lat, lon)
        call.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:" + number)
            startActivity(intent)
        }
        like.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:" + number)
            startActivity(intent)
        }
        share.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:" + number)
            startActivity(intent)
        }
        Glide.with(this).load(dataImgUrl2).into(detail_img)
        registration_button.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(link)
            startActivity(intent)
        }

        // Map
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment.getMapAsync(this)

        // Weather
        val grid = convertGpsToGrid(TO_GRID, lat, lon)
        val gridX = grid.x.toInt()
        val gridY = grid.y.toInt()
        val wt = parsing(gridX, gridY)
        val weathers = wt.weathers // 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
        val temperatures = wt.temperatures
        var weatherDescription = "이 마을의 현재 날씨는\n"

        if (weathers=="0") {
            weatherDescription += "맑음, " + temperatures + "℃ 입니다."
            weather_img.setImageResource(R.drawable.ic_sun)
        } else if (weathers=="1") {
            weatherDescription += "비, " + temperatures + "℃ 입니다."
            weather_img.setImageResource(R.drawable.ic_rain)
        } else if (weathers=="2") {
            weatherDescription += "비와 눈, " + temperatures + "℃ 입니다."
            weather_img.setImageResource(R.drawable.ic_snow_rain)
        } else if (weathers=="3") {
            weatherDescription += "눈, " + temperatures + "℃ 입니다."
            weather_img.setImageResource(R.drawable.ic_snow)
        } else if (weathers=="4") {
            weatherDescription += "소나기, " + temperatures + "℃ 입니다."
            weather_img.setImageResource(R.drawable.ic_sonagi)
        }
        weather_description.text = weatherDescription
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        naverMap.cameraPosition = CameraPosition(latlan, 6.0)

        val marker = Marker()
        marker.position = latlan
        marker.map = naverMap
    }

    //위도, 경도 -> GRID X좌표, Y좌표 변환
    private fun convertGpsToGrid(mode: Int, lat_X: Double, lng_Y: Double): LatXLngY {
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

    internal class LatXLngY {
        var lat = 0.0
        var lng = 0.0
        var x = 0.0
        var y = 0.0
    }

    /**
     * 파싱하는 함수
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun parsing(gridX: Int, gridY: Int): WeathersTemperatures {

        // Web 통신
        StrictMode.enableDefaults()

        val current = LocalDateTime.now()
        val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val timeFormatter = DateTimeFormatter.ofPattern("HHMM")
        val dateFormatted = current.format(dateFormatter)
        val timeFormatted = current.format(timeFormatter)

        val loadPreferences = getSharedPreferences("pref", Context.MODE_PRIVATE)
        val keyUrl ="http://apis.data.go.kr/1360000/VilageFcstInfoService/getUltraSrtNcst?serviceKey="
        val dataUrl = "&pageNo=1&numOfRows=20&dataType=json&base_date="
        val timeUrl = "&base_time="
        val xUrl = "&nx="
        val yUrl = "&ny="

        val allUrl = keyUrl + getSecret("weather", "KEY") +
                            dataUrl + dateFormatted + timeUrl + timeFormatted + xUrl + gridX.toString() + yUrl + gridY.toString()
        val weatherStream = URL(allUrl).openConnection() as HttpURLConnection
        var weatherRead = BufferedReader(InputStreamReader(weatherStream.inputStream, "UTF-8"))
        val weatherResponse = weatherRead.readLine()
        val response = JSONObject(JSONObject(weatherResponse).getString("response"))
        val items = JSONArray(JSONObject(JSONObject(response.getString("body")).getString("items")).getString("item"))
        val wt = WeathersTemperatures()

        for (i in 0 until items.length()) {
            val obj = items.getJSONObject(i)
            val categorys = obj.getString("category")
            if(categorys.equals("PTY"))
                wt.weathers = obj.getString("obsrValue")
            if(categorys.equals("T1H"))
                wt.temperatures = obj.getString("obsrValue")
        }

        return wt
    }

    internal class WeathersTemperatures {
        var weathers = ""
        var temperatures = ""
    }

    private fun getSecret(provider:String, keyArg:String): String {
        val assetManager = resources.assets
        val inputStream= assetManager.open("secret.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val obj = JSONObject(jsonString)
        val secret = obj.getJSONObject(provider)
        return secret.getString(keyArg)
    }
}