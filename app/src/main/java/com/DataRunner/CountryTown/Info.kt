package com.DataRunner.CountryTown

import android.os.Bundle
import androidx.annotation.UiThread
import androidx.fragment.app.FragmentActivity
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import kotlinx.android.synthetic.main.info_layout.*

class Info : FragmentActivity(), OnMapReadyCallback {
    private val latlan: LatLng = LatLng(36.35111, 127.38500)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.info_layout)

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
        naverMap.cameraPosition = CameraPosition(latlan, 4.0)

        val marker = Marker()
        marker.position = latlan
        marker.map = naverMap
    }
}