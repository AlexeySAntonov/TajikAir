package com.aleksejantonov.tajikair.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.aleksejantonov.tajikair.androidx.moxy.MvpAppCompatFragment
import com.aleksejantonov.tajikair.util.inflate

abstract class BaseFragment : MvpAppCompatFragment() {

    abstract val layoutId: Int

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?) =
        parent?.inflate(layoutId)
}