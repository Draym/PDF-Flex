package com.andres_k.lib.library.utils

import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sign

/**
 * Created on 2020/07/20.
 *
 * @author Kevin Andres
 */

fun Float.round(dec: Int): Float {
    val delim = 10f.pow(dec)
    return round(this * delim) / delim
}

fun Float.bigger(value: Float): Boolean {
    return this.round(3) > value.round(3)
}

fun Float.smaller(value: Float): Boolean {
    return this.round(3) < value.round(3)
}

fun Float.eqOrBigger(value: Float): Boolean {
    return this.round(3) >= value.round(3)
}

fun Float.eqOrSmaller(value: Float): Boolean {
    return this.round(3) <= value.round(3)
}

fun Float.eq(value: Float): Boolean {
    return this.round(3) == value.round(3)
}

fun Float.not(value: Float): Boolean {
    return !this.eq(value)
}

fun Float.within(v1: Float, v2: Float): Boolean {
    return this.eqOrBigger(v1) && this.eqOrSmaller(v2)
}

fun Float.positive(): Float {
    return if (this.sign == -1f) 0f else this
}

fun Float.negative(): Float {
    return if (this.sign == 1f) 0f else this
}
