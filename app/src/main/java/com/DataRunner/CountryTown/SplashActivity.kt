package com.DataRunner.CountryTown

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    val SPLASH_VIEW_TIME: Long = 1500
    private val utils = Utils()

    override fun onCreate(savedInstanceState: Bundle?) {
        utils.saveData(this, "town")
        utils.saveData(this, "maker")
        utils.saveData(this, "home/slide")
        super.onCreate(savedInstanceState)
        Handler().postDelayed({
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_VIEW_TIME)
    }
}