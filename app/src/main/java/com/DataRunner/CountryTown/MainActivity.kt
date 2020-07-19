package com.DataRunner.CountryTown

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.naver.maps.map.NaverMapSdk
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private var backWait:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_search,
                R.id.navigation_arround,
                R.id.navigation_more
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        NaverMapSdk.getInstance(container!!.context).client =
            NaverMapSdk.NaverCloudPlatformClient(getSecret("naver", "CLIENT_ID"))
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if(currentFocus != null){
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken,0)
        }
        return super.dispatchTouchEvent(ev)
    }

//    override fun onBackPressed() {
//        if(System.currentTimeMillis()-backWait >=2000){
//            backWait= System.currentTimeMillis()
//            Toast.makeText(this,"뒤로가기 버튼을 한 번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT).show()
//        } else {
//            super.onBackPressed()
//        }
//    }

    private fun getSecret(provider:String, keyArg:String): String {
        val assetManager = resources.assets
        val inputStream= assetManager.open("secret.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val obj = JSONObject(jsonString)
        val secret = obj.getJSONObject(provider)
        return secret.getString(keyArg)
    }
}
