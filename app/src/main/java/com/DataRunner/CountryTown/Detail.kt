package com.DataRunner.CountryTown

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import androidx.annotation.UiThread
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import kotlinx.android.synthetic.main.detail_layout.*
import kotlinx.android.synthetic.main.main_layout.*
import org.json.JSONArray

class Detail : FragmentActivity(), OnMapReadyCallback {
    private var latlan:LatLng = LatLng(0.0, 0.0)
    var TO_GRID = 0
    var TO_GPS = 1

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
            intent.data = Uri.parse("tel:"+number)
            startActivity(intent)
        }
        like.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:"+number)
            startActivity(intent)
        }
        share.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:"+number)
            startActivity(intent)
        }
        Glide.with(this).load(dataImgUrl2).into(detail_img)
        registration_button.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(link)
            startActivity(intent)
        }

        val grid = convertGRID_GPS(TO_GRID, lat, lon)
        val gridX = grid?.x
        val gridY = grid?.y
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
        naverMap.cameraPosition = CameraPosition(latlan, 6.0)

        val marker = Marker()
        marker.position = latlan
        marker.map = naverMap
    }

    //

    //위도, 경도 -> GRID X좌표, Y좌표 변환
    private fun convertGRID_GPS(mode: Int, lat_X: Double, lng_Y: Double): LatXLngY? {
        val RE = 6371.00877 // 지구 반경(km)
        val GRID = 5.0 // 격자 간격(km)
        val SLAT1 = 30.0 // 투영 위도1(degree)
        val SLAT2 = 60.0 // 투영 위도2(degree)
        val OLON = 126.0 // 기준점 경도(degree)
        val OLAT = 38.0 // 기준점 위도(degree)
        val XO = 43.0 // 기준점 X좌표(GRID)
        val YO = 136.0 // 기1준점 Y좌표(GRID)

        //
        // LCC DFS 좌표변환 ( code : "TO_GRID"(위경도->좌표, lat_X:위도,  lng_Y:경도), "TO_GPS"(좌표->위경도,  lat_X:x, lng_Y:y) )
        //
        val DEGRAD = Math.PI / 180.0
        val RADDEG = 180.0 / Math.PI
        val re = RE / GRID
        val slat1 = SLAT1 * DEGRAD
        val slat2 = SLAT2 * DEGRAD
        val olon = OLON * DEGRAD
        val olat = OLAT * DEGRAD
        var sn =
            Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5)
        sn =
            Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(
                sn
            )
        var sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5)
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn
        var ro = Math.tan(Math.PI * 0.25 + olat * 0.5)
        ro = re * sf / Math.pow(ro, sn)
        val rs = LatXLngY()
        if (mode == TO_GRID) {
            rs.lat = lat_X
            rs.lng = lng_Y
            var ra =
                Math.tan(Math.PI * 0.25 + lat_X * DEGRAD * 0.5)
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
    //

}