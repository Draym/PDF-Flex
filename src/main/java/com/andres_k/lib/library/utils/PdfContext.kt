package com.andres_k.lib.library.utils

import com.andres_k.lib.library.core.property.Box2d
import org.apache.pdfbox.pdmodel.PDPageContentStream

/**
 * Created on 2020/07/20.
 *
 * @author Kevin Andres
 */
data class PdfContext(
    val properties: PdfProperties,
    private val stream: PDPageContentStream?,
    val page: PdfPageProperties,
    val viewBody: Box2d,
    val debug: PdfContextDebug
) {
    fun stream(): PDPageContentStream {
        if (stream == null) {
            throw IllegalArgumentException("[PdfContext] Stream is only available within the draw process")
        }
        return stream
    }
}
