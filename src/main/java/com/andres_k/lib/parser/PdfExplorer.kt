package com.andres_k.lib.parser

import com.andres_k.lib.library.core.component.ComponentTypeCode
import com.andres_k.lib.library.utils.data.PdfDrawnElement
import com.andres_k.lib.library.utils.data.PdfDrawnPage

/**
 * Created on 2020/12/10.
 *
 * @author Kevin Andres
 */
data class PdfExplorer(val drawnPages: List<PdfDrawnPage>) {

    fun searchText(value: String, absPosition: Boolean = false): List<ElementPositionResult> {
        return searchText(Regex.fromLiteral(value), absPosition)
    }

    fun searchText(regex: Regex, absPosition: Boolean = false): List<ElementPositionResult> {
        return drawnPages.mapIndexed { pageIndex, page ->
            page.drawnElements.mapNotNull { component ->
                if (component.text != null && component.text.contains(regex)) {
                    ElementPositionResult(
                        page = pageIndex,
                        line = -1, // unknown
                        x = if (absPosition) component.xAbs else component.x,
                        y = page.height - (if (absPosition) component.yAbs else component.y),
                        width = component.width,
                        height = component.height,
                        pageWidth = page.width,
                        pageHeight = page.height,
                        identifier = component.identifier
                    )
                } else null
            }
        }.flatten()
    }

    fun searchByType(value: ComponentTypeCode, absPosition: Boolean = false): List<ElementPositionResult> {
        return drawnPages.mapIndexed { pageIndex, page ->
            page.drawnElements.mapNotNull { component ->
                if (component.type.equals(value)) {
                    ElementPositionResult(
                        page = pageIndex,
                        line = -1, // unknown
                        x = if (absPosition) component.xAbs else component.x,
                        y = page.height - (if (absPosition) component.yAbs else component.y),
                        width = component.width,
                        height = component.height,
                        pageWidth = page.width,
                        pageHeight = page.height,
                        identifier = component.identifier
                    )
                } else null
            }
        }.flatten()
    }

    fun searchByIdentifier(identifier: String, absPosition: Boolean = false): ElementPositionResult? {
        return drawnPages.mapIndexed { pageIndex, page ->
            page.drawnElements.mapNotNull { component ->
                if (component.identifier == identifier) {
                    ElementPositionResult(
                        page = pageIndex,
                        line = -1, // unknown
                        x = if (absPosition) component.xAbs else component.x,
                        y = page.height - (if (absPosition) component.yAbs else component.y),
                        width = component.width,
                        height = component.height,
                        pageWidth = page.width,
                        pageHeight = page.height,
                        identifier = component.identifier
                    )
                } else null
            }
        }.flatten().firstOrNull()
    }
}
