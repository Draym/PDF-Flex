package com.andres_k.lib.library.utils

import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDType1Font
import java.awt.Color

/**
 * Created on 2020/07/20.
 *
 * @author Kevin Andres
 */
data class PdfProperties(
    private val defaultFont: PDType1Font = PDType1Font.HELVETICA,
    val defaultFontSize: FontSize = 11f,
    val defaultInterline: Float = 2f,
    val color: Color = Color.BLACK,
    val drawOverflowX: Boolean = true,
    val drawOverflowY: Boolean = true,
    val createPageOnOverdraw: Boolean = false,
    val debugOn: Boolean = false,
    private val availableFont: Map<FontCode, PDFont> = emptyMap(),
) {

    fun getDefaultFont(): Font {
        val font = availableFont[BaseFont.DEFAULT.code] ?: defaultFont
        return Font(BaseFont.DEFAULT.code, font)
    }

    fun getFont(code: FontCode?, isRequired: Boolean = false): Font {
        if (code == null) {
            return getDefaultFont()
        }
        val font = availableFont[code]
            ?: return if (isRequired) throw IllegalArgumentException("PDFlex - requires the font '$code'") else getDefaultFont()
        return Font(code, font)
    }

    fun getFontSize(fontSize: FontSize?): FontSize {
        return fontSize ?: defaultFontSize
    }

    companion object {
        val DEFAULT = PdfProperties()
    }
}
