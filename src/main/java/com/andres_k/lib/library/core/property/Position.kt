package com.andres_k.lib.library.core.property

import com.andres_k.lib.library.utils.bigger
import com.andres_k.lib.library.utils.smaller

/**
 * Created on 2020/07/20.
 *
 * @author Kevin Andres
 */
data class Position(
    val x: Float = 0f,
    val y: Float = 0f,
    val property: PosProperty = PosProperty.FIXED
) {
    init {
        if (property == PosProperty.RELATIVE && ( (x.smaller(0f) || y.smaller(0f)) || (x.bigger(100f) || y.bigger(100f)))) {
            throw IllegalArgumentException("[Position] Relative position must be between 0 <=> 100")
        }
    }

    companion object {
        val ORIGIN = Position()

        fun fixed(x: Float, y: Float) = Position(x, y, PosProperty.FIXED)
        fun relative(x: Float, y: Float) = Position(x, y, PosProperty.RELATIVE)
    }
}

enum class PosProperty {
    RELATIVE,
    FIXED
}
