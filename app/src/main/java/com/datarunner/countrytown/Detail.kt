package com.datarunner.countrytown

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.UiThread
import androidx.fragment.app.FragmentActivity
import com.datarunner.countrytown.WeatherRecieveData.Item
import com.datarunner.countrytown.WeatherRecieveData.Result
import com.datarunner.countrytown.WeatherRecieveData.WeatherAPI
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.GsonBuilder
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import kotlinx.android.synthetic.main.detail_layout.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration
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
        val link = parceledData?.link
        val townId = parceledData?.townId

        // Set variables and processing
        title_sigungu.text = sigungu
        title_town.text = town
        program_type.text = type
        program_content.text = content
        addr.text = address
        latlan = LatLng(lat, lon)
        setClickListener(number, link)
        loadImage(townId)

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
        weather(gridX, gridY)
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        naverMap.cameraPosition = CameraPosition(latlan, 6.0)

        val marker = Marker()
        marker.position = latlan
        marker.map = naverMap
    }

    private fun loadImage(townId:String) {
        val storage = Firebase.storage
        var storageRef = storage.reference
        storageRef.child("img/town/" + townId + "_2.png").downloadUrl.addOnSuccessListener {
            // Got the download URL for 'users/me/profile.png'
            Glide.with(this)
                .load(it)
                .into(detail_img)
        }.addOnFailureListener {
            // Handle any errors
            storageRef.child("img/town/" + townId + "_2.PNG").downloadUrl.addOnSuccessListener {
                Glide.with(this)
                    .load(it)
                    .into(detail_img)
            }
        }
    }

    private fun setClickListener(number:String, link:String) {
        val call = fun() {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:" + number)
            startActivity(intent)
        }
        val share = fun() {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, "이 체험 마을 어때?\n");
            intent.putExtra(Intent.EXTRA_TEXT, link);
            startActivity(Intent.createChooser(intent, "이 체험 마을 어때?\n"))
        }
        call_layout.setOnClickListener {call()}
        call_button.setOnClickListener {call()}
        share_layout.setOnClickListener {share()}
        share_button.setOnClickListener {share()}
        registration_button.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(link)
            startActivity(intent)
        }
    }

    private fun setWeather(wt:WeathersTemperatures) {
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun weather(gridX: Int, gridY: Int) {
        fun createOkHttpClient(): OkHttpClient? {
            val builder = OkHttpClient.Builder()
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(interceptor)
            return builder.build()
        }

        val current = LocalDateTime.now().minus(Duration.ofHours(1))
        val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val timeFormatter = DateTimeFormatter.ofPattern("HHMM")
        val dateFormatted = current.format(dateFormatter)
        val timeFormatted = current.format(timeFormatter)
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://apis.data.go.kr/")
            .client(createOkHttpClient()) // debug
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val weatherApi = retrofit.create(WeatherAPI::class.java)
        val callApi = weatherApi.getResponse(
            getSecret("weather", "KEY"),
            dateFormatted,
            timeFormatted,
            gridX.toString(),
            gridY.toString()
        )

        val wt = WeathersTemperatures()
        callApi.enqueue(object: Callback<Result> {
            override fun onResponse(call: Call<Result>, response: Response<Result>) {
                val itemList: List<Item> = response.body()!!.response!!.body!!.items!!.item!!
                for (item in itemList) {
                    val categorys = item.category
                    if(categorys.equals("PTY"))
                        wt.weathers = item.obsrValue.toString()
                    if(categorys.equals("T1H"))
                        wt.temperatures = item.obsrValue.toString()
                }
                setWeather(wt)
            }
            override fun onFailure(call: Call<Result>, t: Throwable) {
                Log.d("Call Failed", t.toString())
                setWeather(wt)
            }
        })
    }

    internal class WeathersTemperatures {
        var weathers = "0"
        var temperatures = "18"
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