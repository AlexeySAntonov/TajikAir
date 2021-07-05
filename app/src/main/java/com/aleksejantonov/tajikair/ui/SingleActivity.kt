package com.aleksejantonov.tajikair.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aleksejantonov.tajikair.R
import com.aleksejantonov.tajikair.di.DI
import com.aleksejantonov.tajikair.navigation.MainNavigator

class SingleActivity : AppCompatActivity() {

    private val router by lazy { DI.appComponent.appRouter() }
    private val navigator by lazy { MainNavigator(fragmentManager = supportFragmentManager) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) navigator.openMain()
    }

    override fun onResume() {
        super.onResume()
        router.attachNavigator(navigator)
    }

    override fun onPause() {
        router.detachNavigator()
        super.onPause()
    }
}
