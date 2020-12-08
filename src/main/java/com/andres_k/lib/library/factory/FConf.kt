package com.andres_k.lib.library.factory

import com.andres_k.lib.library.core.component.PdfComponent
import com.andres_k.lib.library.core.property.Background
import com.andres_k.lib.library.core.property.BodyAlign
import com.andres_k.lib.library.core.property.Borders
import com.andres_k.lib.library.core.property.Spacing
import com.andres_k.lib.library.utils.FontCode
import java.awt.Color

/**
 * Created on 2020/07/20.
 *
 * @author Kevin Andres
 */
data class FConf(
    val font: FontCode? = null,
    val bodyAlign: BodyAlign? = null,
    val padding: Spacing? = null,
    val margin: Spacing? = null,
    val color: Color? = null,
    val background: Background? = null,
    val borders: Borders? = null
)

fun <T : PdfComponent> T.conf(configuration: FConf): T {
    if (this.isBuilt) {
        throw IllegalAccessException("[FConf] It's not allowed to apply style if the component is already built.")
    }
    return this.copyAbs(
        font = configuration.font,
        bodyAlign = configuration.bodyAlign,
        padding = configuration.padding,
        margin = configuration.margin,
        color = configuration.color,
        background = configuration.background,
        borders = configuration.borders
    )
}
