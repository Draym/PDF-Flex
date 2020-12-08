package com.andres_k.lib.builder.converter

import com.andres_k.lib.builder.converter.utils.symbol.BulletPicker
import com.andres_k.lib.builder.converter.utils.symbol.NumericalPicker
import com.andres_k.lib.builder.converter.utils.symbol.SymbolPicker
import com.andres_k.lib.library.utils.FontCode

/**
 * Created on 2020/11/02.
 *
 * @author Kevin Andres
 */
data class PdfConverterConfig(
    val data: String, // original Markdown text
    private val defaultFont: FontCode,
    private val defaultFontBold: FontCode? = null,
    private val defaultFontItalic: FontCode? = null,
    val defaultInterline: Float = 6f,
    val defaultFontSize: Float? = null,
    val defaultPadding: Float = 20f,
    val defaultUnorderedList: SymbolPicker = BulletPicker(),
    val defaultOrderedList: SymbolPicker = NumericalPicker(),
) {

    fun getDefaultFont(): FontCode {
        return defaultFont
    }

    fun getDefaultBoldFont(isRequired: Boolean = false): FontCode {
        return defaultFontBold
            ?: if (isRequired) throw IllegalArgumentException("PDFlex - converter requires a valid default font for Bold text") else getDefaultFont()
    }

    fun getDefaultItalicFont(isRequired: Boolean = false): FontCode {
        return defaultFontItalic
            ?: if (isRequired) throw IllegalArgumentException("PDFlex - converter requires a valid default font for Italic text") else getDefaultFont()
    }
}
