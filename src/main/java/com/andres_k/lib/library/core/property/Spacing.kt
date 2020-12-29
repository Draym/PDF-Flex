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
    val left: Float = 0f,
) {
    constructor(spacing: Float = 0f) : this(spacing, spacing, spacing, spacing)
    constructor(spacingX: Float = 0f, spacingY: Float = 0f) : this(spacingY, spacingX, spacingY, spacingX)

    fun spacingX(): Float {
        return left + right
    }

    fun spacingY(): Float {
        return top + bottom
    }

    fun merge(with: Spacing): Spacing {
        return Spacing(
            top = if (this.top == 0f) with.top else this.top,
            right = if (this.right == 0f) with.right else this.right,
            bottom = if (this.bottom == 0f) with.bottom else this.bottom,
            left = if (this.left == 0f) with.left else this.left
        )
    }

    fun merge(
        top: Float = 0f,
        right: Float = 0f,
        bottom: Float = 0f,
        left: Float = 0f,
    ): Spacing {
        return Spacing(
            top = if (this.top == 0f) top else this.top,
            right = if (this.right == 0f) right else this.right,
            bottom = if (this.bottom == 0f) bottom else this.bottom,
            left = if (this.left == 0f) left else this.left
        )
    }

    companion object {
        val NONE = Spacing()
    }
}
