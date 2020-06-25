package com.DataRunner.CountryTown

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.detail_layout.*
import kotlinx.android.synthetic.main.main_toolbar.*

class Detail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_layout)

        val title = intent.getStringExtra("sigungu") + " " + intent.getStringExtra("title")
        val programDescription = "[" + intent.getStringExtra("programType") + "] " + intent.getStringExtra("programContent")
        val lat = intent.getDoubleArrayExtra("lat")
        val lon = intent.getDoubleArrayExtra("lon")
        val address = intent.getStringExtra("addr")


        town_title.text = title
        program_description.text = programDescription
        addr.text = address
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)               //toolbar  보이게 하기
//        supportActionBar?.setHomeAsUpIndicator(R.drawable.move_back)    //뒤로가기 아이콘 지정
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