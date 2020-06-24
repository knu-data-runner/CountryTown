package com.example.data_runner_app

import android.os.Bundle
import android.os.StrictMode
import android.view.Menu

import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.json.JSONArray


class MainActivity : AppCompatActivity() {
    var dataList = arrayListOf<data>()

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //파싱
        // Web 통신
        val DATAAdapter = dataAdapter(this, dataList)
        //text_home.adapter = DATAAdapter
        //start
        StrictMode.enableDefaults()

        try {
            val assetManager = resources.assets
            val inputStream= assetManager.open("data.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val jArray = JSONArray(jsonString)

            // 모든 공지 noticeList 에 저장
            for (i in 0 until jArray.length()) {

                val obj = jArray.getJSONObject(i)
                val title = obj.getString("체험마을명")
                var sido = obj.getString("시도명")
                val addr = obj.getString("소재지도로명주소")
                val master = obj.getString("대표자성명")
                val number = obj.getString("대표전화번호")
                var link = obj.getString("홈페이지주소")

                val listLine = data(title, sido, addr, master, "A", "B")
                dataList.add(listLine)
            }
        } catch (e: Exception) {
            val listLine = data("e" + e.toString(), "오류","오류", "오류", "오류", "오류")
            dataList.add(listLine)
        }
        //end


//        val fab: FloatingActionButton = findViewById(R.id.fab)
//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)

        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because eachㅅ
        // menu should be considered as top level destinations.


        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}
