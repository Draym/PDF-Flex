package com.andres_k.lib.library.core.property

import com.andres_k.lib.library.utils.withAlpha
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import java.awt.Color

/**
 * Created on 2020/07/20.
 *
 * @author Kevin Andres
 */
data class Background(
    val color: Color? = null,
    val image: PDImageXObject? = null
) {
    companion object {
        fun ON(color: Color? = Color.CYAN.withAlpha(0.08f), image: PDImageXObject? = null) = Background(color, image)
        val NONE = Background()
    }
}
