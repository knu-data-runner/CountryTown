package com.DataRunner.CountryTown

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.annotation.UiThread
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import kotlinx.android.synthetic.main.detail_layout.*

class Detail : FragmentActivity(), OnMapReadyCallback {
    private var latlan:LatLng = LatLng(0.0, 0.0)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_layout)

        // Get variables
        val bundleData = intent.getBundleExtra("bundleData")
        val parceledData = bundleData.getParcelable<Data>("parceledData")
        val title = parceledData?.sigungu + " " + parceledData?.title
        val programDescription = "[" + parceledData?.programType + "] " + parceledData?.programContent
        val lat = parceledData!!.lat
        val lon = parceledData!!.lon
        val address = parceledData?.addr
        val number = parceledData?.number
        val dataImgUrl2 = parceledData?.imgUrl2

        // Set variables and processing
        town_title.text = title
        program_description.text = programDescription
        addr.text = "주소: " + address
        call_number.text = number
        latlan = LatLng(lat, lon)
        call_button.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:"+number)
            startActivity(intent)
        }

        Glide.with(this).load(dataImgUrl2).into(detail_img)

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
        val options = NaverMapOptions()
            .camera(CameraPosition(latlan, 1.0))
            .mapType(NaverMap.MapType.Terrain)
        naverMap.cameraPosition = CameraPosition(latlan, 6.0)

        val marker = Marker()
        marker.position = latlan
        marker.map = naverMap
    }
}