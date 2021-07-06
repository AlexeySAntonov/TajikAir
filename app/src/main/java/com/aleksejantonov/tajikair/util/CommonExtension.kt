package com.aleksejantonov.tajikair.util

import android.os.Build
import com.aleksejantonov.tajikair.api.entity.City

fun cityStub() = City("", "", null, emptyList())

fun hasOreo() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O