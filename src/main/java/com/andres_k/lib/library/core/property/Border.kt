package com.andres_k.lib.library.core.property

import java.awt.Color


/**
 * Created on 2020/07/20.
 *
 * @author Kevin Andres
 */
data class Borders(
    val top: Border? = null,
    val right: Border? = null,
    val bottom: Border? = null,
    val left: Border? = null
) {
    companion object {
        val NONE = Borders()
        fun DEFAULT(top: Boolean = false, right: Boolean = false, bottom: Boolean = false, left: Boolean = false) = Borders(
            if (top) Border() else null,
            if (right) Border() else null,
            if (bottom) Border() else null,
            if (left) Border() else null)

        fun ALL(color: Color? = null, thickness: Float = 1f) = Borders(Border(color, thickness), Border(color, thickness), Border(color, thickness), Border(color, thickness))
        fun TOP(color: Color? = null, thickness: Float = 1f) = Borders(top = Border(color, thickness))
        fun LEFT(color: Color? = null, thickness: Float = 1f) = Borders(left = Border(color, thickness))
        fun RIGHT(color: Color? = null, thickness: Float = 1f) = Borders(right = Border(color, thickness))
        fun BOTTOM(color: Color? = null, thickness: Float = 1f) = Borders(bottom = Border(color, thickness))
    }
}

data class Border(val color: Color? = null, val thickness: Float = 1f)
