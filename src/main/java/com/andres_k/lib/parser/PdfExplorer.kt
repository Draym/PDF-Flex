package com.andres_k.lib.parser

import com.andres_k.lib.library.core.component.PdfComponent
import com.andres_k.lib.library.utils.data.PdfDrawnPage

/**
 * Created on 2020/12/10.
 *
 * @author Kevin Andres
 */
data class PdfExplorer(private val drawnPages: List<PdfDrawnPage>) {

    fun searchText(value: String, absPosition: Boolean = false): List<ElementPositionResult> {
        return drawnPages.mapIndexed { pageIndex, page ->
            page.drawnElements.mapNotNull { component ->
                if (component.text != null && component.text.contains(value)) {
                    ElementPositionResult(
                        page = pageIndex,
                        line = -1, // unknown
                        x = if (absPosition) component.xAbs else component.x,
                        y = page.height - (if (absPosition) component.yAbs else component.y),
                        pageWidth = page.width,
                        pageHeight = page.height,
                        identifier = component.identifier
                    )
                } else null
            }
        }.flatten()
    }

    fun searchByType(value: PdfComponent.Type, absPosition: Boolean = false): List<ElementPositionResult> {
        return drawnPages.mapIndexed { pageIndex, page ->
            page.drawnElements.mapNotNull { component ->
                if (component.type == value) {
                    ElementPositionResult(
                        page = pageIndex,
                        line = -1, // unknown
                        x = if (absPosition) component.xAbs else component.x,
                        y = page.height - (if (absPosition) component.yAbs else component.y),
                        pageWidth = page.width,
                        pageHeight = page.height,
                        identifier = component.identifier
                    )
                } else null
            }
        }.flatten()
    }
}
