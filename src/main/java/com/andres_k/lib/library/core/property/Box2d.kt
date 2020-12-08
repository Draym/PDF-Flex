package com.andres_k.lib.library.core.property

/**
 * Created on 2020/07/20.
 *
 * @author Kevin Andres
 */
data class Box2d(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
) {
    fun maxX(): Float {
        return x + width
    }

    fun maxY(): Float {
        return y + height
    }
}

data class Box2dRequest(
    val x: Float? = null,
    val y: Float? = null,
    val width: Float? = null,
    val height: Float? = null
) {
    companion object {
        val ORIGIN = Box2dRequest(0f, 0f)
    }
}
