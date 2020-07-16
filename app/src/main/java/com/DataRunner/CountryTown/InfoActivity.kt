package com.DataRunner.CountryTown

import android.os.Bundle
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker

class InfoActivity : AppCompatActivity(), OnMapReadyCallback {
    private var dataList = arrayListOf<TownData>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.info_layout)

        dataList = intent.getParcelableArrayListExtra("dataList")

        // Map
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment.getMapAsync(this)
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        val latlng = LatLng(35.956874, 127.733705) // 계룡리(남한 중앙) 좌표
        naverMap.cameraPosition = CameraPosition(latlng, 5.5)

        for (data in dataList) {
            val marker = Marker()
            marker.width = Marker.SIZE_AUTO
            marker.height = Marker.SIZE_AUTO
            marker.position = LatLng(data.lat, data.lon)
            marker.map = naverMap
        }
    }
}