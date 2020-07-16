package com.DataRunner.CountryTown.ui.arround

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.DataRunner.CountryTown.*
import kotlinx.android.synthetic.main.fragment_arround.view.*
import kotlinx.android.synthetic.main.main_layout.view.*
import kotlin.collections.ArrayList


class ArroundFragment : Fragment() {

    private lateinit var root: View
    private lateinit var container: ViewGroup
    private lateinit var townDataList: ArrayList<TownData>
    private lateinit var gpsTracker: GpsTracker
    private val GPS_ENABLE_REQUEST_CODE = 2001
    private val PERMISSIONS_REQUEST_CODE = 100
    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private val utils = Utils()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        this.root = inflater.inflate(R.layout.main_layout, container, false)
        this.container = container!!

        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting()
        } else {
            checkRunTimePermission()
        }

        init()
        root.swipe.setOnRefreshListener {
            init()
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

    private fun init() {
        gpsTracker = GpsTracker(requireActivity())
        val myLoc = Location("myLoc")
        myLoc.latitude = gpsTracker.getLat()
        myLoc.longitude = gpsTracker.getLon()

        townDataList = parse()
        addDistance(townDataList, myLoc)
    }

    private fun addDistance(
        townDataList: ArrayList<TownData>,
        myLoc: Location
    ) {
        for (townData in townDataList) {
            val townLoc = Location("townLoc")
            townLoc.latitude = townData.lat
            townLoc.longitude = townData.lon
            val distance = myLoc.distanceTo(townLoc)
            if (distance < 10000) {
                townData.distance = String.format("%.1f", distance/1000)
            } else {
                townData.distance = (distance/1000).toInt().toString()
            }
        }
        townDataList.sortWith(
            Comparator {
                    data1, data2 -> data1.distance!!.toFloat().toInt() - data2.distance!!.toFloat().toInt()
            }
        )
    }

    private fun parse(checkSido : String = "전국"): ArrayList<TownData> {
        val ret = utils.parsing(container!!.context, checkSido)

        val dataAdapter = DataAdapter(this, ret) { data ->
            val toDetailIntent = Intent(activity, Detail::class.java)   // activity: 부모 Activity(Context)
            val b = Bundle()
            b.putParcelable("parceledData", data)
            toDetailIntent.putExtra("bundleData", b)
            startActivity(toDetailIntent)
        }
        root.result.adapter = dataAdapter

        // LayoutManager 설정. RecyclerView 에서는 필수
        val lm = LinearLayoutManager(activity)  // activity: 부모 Activity(Context)
        root.result.layoutManager = lm
        root.result.setHasFixedSize(true)

        return ret
    }

    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    override fun onRequestPermissionsResult(
        permsRequestCode: Int,
        permissions: Array<String?>,
        grandResults: IntArray
    ) {
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.size == REQUIRED_PERMISSIONS.size) {
            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            var check_result = true

            // 모든 퍼미션을 허용했는지 체크합니다.
            for (result in grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false
                    break
                }
            }
            if (check_result) {
                //위치 값을 가져올 수 있음
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        REQUIRED_PERMISSIONS[0]
                    )
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        REQUIRED_PERMISSIONS[1]
                    )
                ) {
                    Toast.makeText(
                        activity,
                        "위치 권한이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.",
                        Toast.LENGTH_LONG
                    ).show()
                    requireActivity().finish()
                } else {
                    Toast.makeText(
                        activity,
                        "위치 권한이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun checkRunTimePermission() {
        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
            hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED
        ) {
            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)

            // 3.  위치 값을 가져올 수 있음
        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.
            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    REQUIRED_PERMISSIONS[0]
                )
            ) {
                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(activity, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG)
                    .show()
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(
                    requireActivity(), REQUIRED_PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(
                    requireActivity(), REQUIRED_PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )
            }
        }
    }

    // 여기부터는 GPS 활성화를 위한 메소드들
    private fun showDialogForLocationServiceSetting() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage(
            """
            앱을 사용하기 위해서는 위치 서비스가 필요합니다.
            위치 설정을 수정하실래요?
            """.trimIndent()
        )
        builder.setCancelable(true)
        builder.setPositiveButton("설정") { dialog, id ->
            val callGPSSettingIntent =
                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE)
        }
        builder.setNegativeButton("취소") { dialog, id ->
            dialog.cancel() }
        builder.create().show()
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GPS_ENABLE_REQUEST_CODE ->
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음")
                        checkRunTimePermission()
                        return
                    }
                }
        }
    }

    private fun checkLocationServicesStatus(): Boolean {
        val locationManager =
            activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        return (locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }
}
