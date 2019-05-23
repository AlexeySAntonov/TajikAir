package com.aleksejantonov.tajikair.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aleksejantonov.tajikair.R
import com.aleksejantonov.tajikair.sl.SL

class SingleActivity : AppCompatActivity() {

    private val router = SL.componentManager().appComponent.router

    override fun onCreate(savedInstanceState: Bundle?) {
        router.createNavigator(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) router.openMain()
    }
}
