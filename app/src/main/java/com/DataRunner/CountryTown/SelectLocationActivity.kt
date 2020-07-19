package com.DataRunner.CountryTown

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.select_location_layout.*

class SelectLocationActivity : AppCompatActivity() {

    private var townDataList = arrayListOf<TownData>()
    private val utils = Utils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_location_layout)
        supportActionBar!!.title = "지역 선택"

        val localLayoutList = arrayOf(
            all,
            sudo,
            kangwon,
            chung,
            jeon,
            kyung,
            jeju
        )
        for (layout in localLayoutList) {
            layout.setOnClickListener { view ->
                val intent = Intent(this, ListActivity :: class.java)
                intent.putExtra("query", layout.text)
                intent.putExtra("searchType", "location")
                startActivity(intent)
            }
        }
    }
}
