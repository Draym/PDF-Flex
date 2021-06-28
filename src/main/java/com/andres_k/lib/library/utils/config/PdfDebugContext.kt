package com.andres_k.lib.library.utils.config

import com.andres_k.lib.library.core.component.ComponentType
import com.andres_k.lib.library.core.component.ComponentTypeCode
import com.andres_k.lib.library.core.property.Border
import com.andres_k.lib.library.utils.withAlpha
import java.awt.Color

/**
 * Created on 2020/07/20.
 *
 * @author Kevin Andres
 */
data class PdfDebugContext(
    val borders: Map<ComponentType, Border>,
    val background: Map<ComponentType, Color>
) {

    fun hasBorder(type: ComponentType): Boolean {
        return borders.containsKey(type)
    }

    fun hasBackground(type: ComponentType): Boolean {
        return background.containsKey(type)
    }

    companion object {
        val DEFAULT = PdfDebugContext(
            borders = mapOf(
                ComponentTypeCode.ROW.type to Border(Color.RED.withAlpha(0.5f)),
                ComponentTypeCode.COL.type to Border(Color.BLUE.withAlpha(0.5f)),
                ComponentTypeCode.TABLE.type to Border(Color.PINK.withAlpha(0.7f))
            ),
            background = mapOf(
                ComponentTypeCode.COL.type to Color.CYAN.withAlpha(0.05f),
                ComponentTypeCode.TEXT.type to Color.LIGHT_GRAY.withAlpha(0.3f),
                ComponentTypeCode.PAGE_NB.type to Color.ORANGE.withAlpha(0.3f),
                ComponentTypeCode.PAGE_BREAK.type to Color.GREEN.withAlpha(0.3f)
            )
        )
    }
}
