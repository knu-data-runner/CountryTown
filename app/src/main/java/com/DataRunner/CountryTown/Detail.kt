package com.DataRunner.CountryTown

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.naver.maps.map.NaverMapSdk
import kotlinx.android.synthetic.main.detail_layout.*
import org.json.JSONArray
import org.json.JSONObject

class Detail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_layout)

        val bundleData = intent.getBundleExtra("bundleData")
        val parceledData = bundleData.getParcelable<Data>("parceledData")

        val title = parceledData?.sigungu + " " + parceledData?.title
        val programDescription = "[" + parceledData?.programType + "] " + parceledData?.programContent
        val lat = parceledData?.lat
        val lon = parceledData?.lon
        val address = parceledData?.addr

        town_title.text = title
        program_description.text = programDescription
        addr.text = address

        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NaverCloudPlatformClient(getSecret("naver", "CLIENT_ID"))
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)               //toolbar  보이게 하기
//        supportActionBar?.setHomeAsUpIndicator(R.drawable.move_back)    //뒤로가기 아이콘 지정
    }

    private fun getSecret(provider:String, keyArg:String): String {
        val assetManager = resources.assets
        val inputStream= assetManager.open("secret.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val obj = JSONObject(jsonString)
        val secret = obj.getJSONObject(provider)
        return secret.getString(keyArg)
    }

//    fun getMap() {
//        val cameraUpdate = CameraUpdate.scrollTo(LatLng(37.5666102, 126.9783881))
//            .reason(3)
//            .animate(CameraAnimation.Easing, 2000)
//            .finishCallback {
//                Toast.makeText(context, "완료", Toast.LENGTH_SHORT).show()
//            }
//            .cancelCallback {
//                Toast.makeText(context, "취소", Toast.LENGTH_SHORT).show()
//            }
//
//        naverMap.moveCamera(cameraUpdate)
//    }
}