package com.aleksejantonov.tajikair.navigation

import com.aleksejantonov.tajikair.ui.Screens
import java.lang.ref.WeakReference
import javax.inject.Inject

class AppRouter @Inject constructor() {

  private var navigator: WeakReference<MainNavigator>? = null

  fun attachNavigator(navigator: MainNavigator) {
    this.navigator = WeakReference(navigator)
  }

  fun detachNavigator() {
    this.navigator = null
  }

  fun openMain() = navigator?.get()?.openMain()
  fun replace(screen: Screens, data: Any? = null) = navigator?.get()?.replace(screen, data)
  fun forward(screen: Screens, data: Any? = null) = navigator?.get()?.forward(screen, data)
  fun back() = navigator?.get()?.back()
}