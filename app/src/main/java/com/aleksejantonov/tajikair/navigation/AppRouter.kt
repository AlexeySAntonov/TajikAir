package com.aleksejantonov.tajikair.navigation

import com.aleksejantonov.tajikair.ui.SingleActivity
import com.aleksejantonov.tajikair.ui.Screens

class AppRouter {
    private lateinit var navigator: MainNavigator

    fun createNavigator(activity: SingleActivity) {
        navigator = MainNavigator(activity)
    }

    fun openMain() = navigator.openMain()
    fun replace(screen: Screens, data: Any? = null) = navigator.replace(screen, data)
    fun forward(screen: Screens, data: Any? = null) = navigator.forward(screen, data)
    fun back() = navigator.back()
}