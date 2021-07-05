package com.aleksejantonov.tajikair.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aleksejantonov.tajikair.util.inflate

abstract class BaseFragment : Fragment() {

    abstract val layoutId: Int

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?) =
        parent?.inflate(layoutId)
}