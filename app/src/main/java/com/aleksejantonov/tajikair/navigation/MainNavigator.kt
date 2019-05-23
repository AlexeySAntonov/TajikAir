package com.aleksejantonov.tajikair.navigation

import androidx.fragment.app.Fragment
import com.aleksejantonov.tajikair.ui.SingleActivity
import com.aleksejantonov.tajikair.R
import com.aleksejantonov.tajikair.api.entity.City
import com.aleksejantonov.tajikair.navigation.MainNavigator.Commands.*
import com.aleksejantonov.tajikair.ui.Screens
import com.aleksejantonov.tajikair.ui.Screens.*
import com.aleksejantonov.tajikair.ui.main.MainFragment
import com.aleksejantonov.tajikair.ui.map.MapFragment

class MainNavigator(activity: SingleActivity) {

    enum class Commands {
        FORWARD,
        BACK,
        REPLACE
    }

    private val fragmentManager by lazy { activity.supportFragmentManager }

    fun openMain() = replace(MAIN_FRAGMENT)

    fun replace(screen: Screens, data: Any? = null) = applyCommand(screen, REPLACE, data)

    fun forward(screen: Screens, data: Any? = null) = applyCommand(screen, FORWARD, data)

    fun back() = fragmentManager.popBackStackImmediate()

    private fun applyCommand(screen: Screens, command: Commands, data: Any?, animate: Boolean = true) {
        fragmentManager
            .beginTransaction()
            .apply { if (animate) setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right) }
            .replace(R.id.container, getFragment(screen, data))
            .apply { if (command == FORWARD) addToBackStack(null) }
            .commitAllowingStateLoss()
    }

    private fun getFragment(screen: Screens, data: Any? = null): Fragment {
        return when (screen) {
            MAIN_FRAGMENT -> MainFragment.newInstance()
            MAP_FRAGMENT  -> MapFragment.newInstance(
                (data as Pair<City, City>).first, data.second
            )
        }
    }
}