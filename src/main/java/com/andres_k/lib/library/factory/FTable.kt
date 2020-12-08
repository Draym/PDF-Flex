package com.andres_k.lib.library.factory

import com.andres_k.lib.library.core.component.container.PdfCol
import com.andres_k.lib.library.core.component.element.PdfTable
import com.andres_k.lib.library.core.component.element.PdfText
import com.andres_k.lib.library.core.property.Background
import com.andres_k.lib.library.core.property.Borders
import com.andres_k.lib.library.core.property.Spacing
import com.andres_k.lib.library.utils.FontCode
import java.awt.Color

/**
 * Created on 2020/07/20.
 *
 * @author Kevin Andres
 */
object FTable {

    fun toTable(
        header: List<String>,
        rows: List<List<String>>,
        font: FontCode? = null,
        tableMargin: Spacing? = null,
        colPadding: Spacing? = null,
        bordered: Borders? = null,
        textColor: Color? = null,
        headerBackground: Color? = null,
        colBackground: Color? = null,
        headerVisible: Boolean = true
    ): PdfTable {
        return PdfTable(
            header = toRow(header, font, colPadding, bordered, textColor, headerBackground),
            rows = rows.map { toRow(it, font, colPadding, bordered, textColor, colBackground) },
            margin = tableMargin ?: Spacing.NONE,
            borders = bordered ?: Borders.NONE,
            headerVisible = headerVisible
        )
    }

    fun toRow(
        cols: List<String>,
        font: FontCode? = null,
        padding: Spacing? = null,
        borders: Borders? = null,
        textColor: Color? = null,
        backgroundColor: Color? = null
    ): List<PdfCol> {
        return cols.map { toCol(it, font, padding, borders, textColor, backgroundColor) }
    }

    fun toCol(
        text: String,
        font: FontCode? = null,
        padding: Spacing? = null,
        borders: Borders? = null,
        textColor: Color? = null,
        backgroundColor: Color? = null
    ): PdfCol {
        return PdfCol(
            content = PdfText(text = text, font = font, color = textColor),
            padding = padding ?: Spacing.NONE,
            borders = borders ?: Borders.NONE,
            background = Background(backgroundColor)
        )
    }
}
