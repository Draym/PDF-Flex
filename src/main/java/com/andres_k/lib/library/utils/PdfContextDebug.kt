package com.andres_k.lib.library.utils

import com.andres_k.lib.library.core.component.PdfComponent.Type
import com.andres_k.lib.library.core.property.Border
import java.awt.Color

/**
 * Created on 2020/07/20.
 *
 * @author Kevin Andres
 */
data class PdfContextDebug(
    val borders: Map<Type, Border>,
    val background: Map<Type, Color>
) {

    fun hasBorder(type: Type): Boolean {
        return borders.containsKey(type)
    }

    fun hasBackground(type: Type): Boolean {
        return background.containsKey(type)
    }

    companion object {
        val DEFAULT = PdfContextDebug(
            borders = mapOf(
                Type.ROW to Border(Color.RED.withAlpha(0.5f)),
                Type.COL to Border(Color.BLUE.withAlpha(0.5f)),
                Type.TABLE to Border(Color.PINK.withAlpha(0.7f))
            ),
            background = mapOf(
                Type.COL to Color.CYAN.withAlpha(0.05f),
                Type.TEXT to Color.LIGHT_GRAY.withAlpha(0.3f)
            )
        )
    }
}
