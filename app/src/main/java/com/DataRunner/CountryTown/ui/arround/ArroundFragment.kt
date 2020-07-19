package com.DataRunner.CountryTown.ui.arround

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.DataRunner.CountryTown.*
import kotlinx.android.synthetic.main.main_layout.view.*
import kotlin.collections.ArrayList


class ArroundFragment : Fragment() {

    private lateinit var root: View
    private lateinit var container: ViewGroup
    private lateinit var townDataList: ArrayList<TownData>
    private lateinit var gpsTracker: GpsTracker
    private val utils = Utils()
    private val gpsUtils = GpsUtils()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        this.root = inflater.inflate(R.layout.fragment_arround, container, false)
        this.container = container!!

        if (!gpsUtils.checkLocationServicesStatus(requireContext())) {
            gpsUtils.showDialogForLocationServiceSetting(requireActivity())
        } else {
            gpsUtils.checkRunTimePermission(requireActivity())
        }

        townDataList = utils.getTownDataList(requireContext(), "전국")

        init(arrayOf("전국"), "location")
        root.swipe.setOnRefreshListener {
            init(arrayOf("전국"), "location")
            root.swipe.isRefreshing = false
        }

        //get current location test
//        this.root = inflater.inflate(R.layout.fragment_arround, container, false)
//        val latitude: Double = gpsTracker.getLat()
//        val longitude: Double = gpsTracker.getLon()
//        val textviewAddress = root.textview
//        val showLocationButton = root.button
//        showLocationButton.setOnClickListener {
//            gpsTracker = GpsTracker(requireActivity())
//            val latitude: Double = gpsTracker.getLat()
//            val longitude: Double = gpsTracker.getLon()
//            val address = utils.getCurrentAddress(requireActivity(), latitude, longitude)
//            textviewAddress.text = address
//            Toast.makeText(
//                activity,
//                "현재위치 \n위도 $latitude\n경도 $longitude",
//                Toast.LENGTH_LONG
//            ).show()
//        }
        return root
    }

    private fun init(queryArray: Array<String>, searchType: String="user") {
        gpsTracker = GpsTracker(requireContext())
        val myLoc = Location("myLoc")
        myLoc.latitude = gpsTracker.getLat()
        myLoc.longitude = gpsTracker.getLon()

        townDataList = utils.search(townDataList, queryArray, searchType)
        utils.setAdapter(requireContext(), root.result, townDataList)
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
            requireActivity(),
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
            requireActivity(),
            requestCode,
            resultCode,
            data
        )
    }
}
