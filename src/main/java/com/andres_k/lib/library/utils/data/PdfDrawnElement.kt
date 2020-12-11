package com.andres_k.lib.library.utils.data

import com.andres_k.lib.library.core.component.PdfComponent

/**
 * Created on 2020/12/11.
 *
 * @author Kevin Andres
 */

/**
 * Drawn Element position within a PDF page
 * default origin(0,0) is bottom left
 *
 * (x,y) represent the position of the printing area (if any printed text for example), including padding.
 * (xAbs,yAbs) represent the position of the block element, not including padding.
 */
data class PdfDrawnElement(
    val x: Float,
    val y: Float,
    val xAbs: Float,
    val yAbs: Float,
    val type: PdfComponent.Type,
    val identifier: String?,
    val text: String?
)
