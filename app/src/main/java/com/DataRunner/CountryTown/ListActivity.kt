package com.DataRunner.CountryTown

import android.content.Intent
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.main_layout.*

class ListActivity : AppCompatActivity() {

    private var townDataList = arrayListOf<TownData>()
    private lateinit var gpsTracker: GpsTracker
    private val utils = Utils()
    private val gpsUtils = GpsUtils()
    private val recommendList = arrayOf(
        "kang_009",     // 섬강매향골마을
        "sudo_011",     // 장단콩마을
        "kyung_016",    // 하범곡마을
        "chung_015",    // 추평호산뜰애마을
        "jeon_019",     // 태산선비마을
        "jeju_001"      // 가시리마을
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        val query = intent.getStringExtra("query")
        val searchType = intent.getStringExtra("searchType")
        if (searchType=="location") {
            townDataList = utils.getTownDataList(this, query.substring(0,2))
        } else {
            townDataList = utils.getTownDataList(this)
        }
        val queryArray = when (query) {
            "경작체험", "만들기체험", "생활체험" -> arrayOf(query)
            "자연체험", "전통체험" -> arrayOf(query.substring(0,2))
            "기타" -> arrayOf("기타","건강")
            "추천" -> recommendList
            "전국" -> arrayOf("전국")
            else -> arrayOf()  // Exception situation
        }
        supportActionBar!!.title = query

        if (!gpsUtils.checkLocationServicesStatus(this)) {
            gpsUtils.showDialogForLocationServiceSetting(this)
        } else {
            gpsUtils.checkRunTimePermission(this)
        }

        init(queryArray, searchType)
        swipe.setOnRefreshListener {
            init(queryArray, searchType)
            swipe.isRefreshing = false
        }
    }

    private fun init(queryArray: Array<String>, searchType: String="user") {
        gpsTracker = GpsTracker(this)
        val myLoc = Location("myLoc")
        myLoc.latitude = gpsTracker.getLat()
        myLoc.longitude = gpsTracker.getLon()

        townDataList = utils.search(townDataList, queryArray, searchType)
        utils.setAdapter(this, result, townDataList)
        utils.addDistance(townDataList, myLoc)
    }

    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    override fun onRequestPermissionsResult(
        permsRequestCode: Int,
        permissions: Array<String?>,
        grandResults: IntArray
    ) {
        gpsUtils.onRequestPermissionsResult(
            this,
            permsRequestCode,
            permissions,
            grandResults
        )
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        gpsUtils.onActivityResult(
            this,
            requestCode,
            resultCode,
            data
        )
    }
}
