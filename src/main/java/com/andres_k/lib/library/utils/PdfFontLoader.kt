package com.andres_k.lib.library.utils

import org.apache.fontbox.ttf.TrueTypeFont
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.font.PDType0Font

/**
 * Created on 2020/12/04.
 *
 * @author Kevin Andres
 */
object PdfFontLoader {
    fun loadTTCFont(document: PDDocument, fontTTC: TrueTypeFont): PDType0Font {
        return PDType0Font.load(document, fontTTC, true)
    }
}
