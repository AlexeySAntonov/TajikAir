package com.aleksejantonov.tajikair.ui.base

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import com.aleksejantonov.tajikair.util.navBarHeight
import com.aleksejantonov.tajikair.util.statusBarHeight

abstract class BaseFragment : Fragment() {

  protected var _binding: Any? = null
  private var baseHandler: Handler? = null

  private val statusBarHeight by lazy { statusBarHeight() }
  private val navBarHeight by lazy { navBarHeight() }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    baseHandler = Handler(Looper.getMainLooper())
    onStatusBarHeight(statusBarHeight)
    onNavigationBarHeight(navBarHeight)
  }

  override fun onDestroyView() {
    baseHandler?.removeCallbacksAndMessages(null)
    baseHandler = null
    _binding = null
    super.onDestroyView()
  }

  protected open fun onStatusBarHeight(statusBarHeight: Int) {}
  protected open fun onNavigationBarHeight(navBarHeight: Int) {}

  protected fun safePost(action: () -> Unit) {
    baseHandler?.post { action.invoke() }
  }

  protected fun safePostDelayed(action: () -> Unit, delay: Long) {
    baseHandler?.postDelayed({ action.invoke() }, delay)
  }
}