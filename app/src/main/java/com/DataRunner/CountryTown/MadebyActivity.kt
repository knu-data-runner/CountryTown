package com.DataRunner.CountryTown

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.main_toolbar.*


class MadebyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.madeby_layout)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when(item.itemId) {

            R.id.action_main -> {
                var intent = Intent(this, MainActivity :: class.java)
                return true
            }
            R.id.action_settings -> {
                var intent = Intent(this, MainActivity :: class.java)
                return true
            }
            R.id.action_license -> {
                var intent = Intent(this, MainActivity :: class.java)
                return true
            }
            R.id.action_maker -> {
                var intent = Intent(this, MainActivity :: class.java)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
