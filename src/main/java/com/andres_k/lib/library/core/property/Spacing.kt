package com.andres_k.lib.library.core.property

/**
 * Created on 2020/07/20.
 *
 * @author Kevin Andres
 */
data class Spacing(
    val top: Float = 0f,
    val right: Float = 0f,
    val bottom: Float = 0f,
    val left: Float = 0f
) {
    constructor(spacing: Float = 0f): this(spacing, spacing, spacing, spacing)
    constructor(spacingX: Float = 0f, spacingY: Float = 0f): this(spacingY, spacingX, spacingY, spacingX)

    fun spacingX(): Float {
        return left + right
    }

    fun spacingY(): Float {
        return top + bottom
    }

    companion object {
        val NONE = Spacing()
    }
}
