package com.aleksejantonov.tajikair.util

import java.math.BigInteger

const val MAX_LONGITUDE = 180.0
const val MIN_LONGITUDE = -180.0
const val WHOLE_PATH_ANIMATION_DURATION = 20000L

// https://en.wikipedia.org/wiki/Binomial_coefficient
fun getCoef(n: Int, k: Int): Int {
  return (factorial(n) / (factorial(k) * factorial(n - k))).toInt()
}

fun factorial(n: Int): BigInteger {
  if (n == 0) return BigInteger.ONE
  return BigInteger.valueOf(n.toLong()) * factorial(n - 1)
}

