package com.andres_k.lib.library.core.component.container

import com.andres_k.lib.library.core.component.PdfComponent
import com.andres_k.lib.library.core.component.element.PdfPageBreak
import com.andres_k.lib.library.core.property.*
import com.andres_k.lib.library.utils.config.PdfContext
import com.andres_k.lib.library.utils.data.PdfDrawnElement
import java.awt.Color

/**
 * Created on 2020/07/20.
 *
 * @author Kevin Andres
 */
abstract class PdfContainer(
    open val elements: List<PdfComponent>,
    open val splitOnOverdraw: Boolean,
    identifier: String?,
    position: Position,
    size: Size,
    bodyAlign: BodyAlign? = null,
    padding: Spacing,
    margin: Spacing,
    color: Color?,
    background: Background,
    borders: Borders,
    isBuilt: Boolean,
    type: Type,
) : PdfComponent(identifier, position, size, bodyAlign, padding, margin, color, background, borders, isBuilt, type) {

    override fun verifyContent() {
        elements.forEach {
            if (this !is PdfRow && it is PdfCol) {
                throw IllegalArgumentException("[$type] PdfCol can only be contained by PdfRow.")
            }
            if (this !is PdfView && it is PdfPageBreak) {
                throw IllegalArgumentException("[$type] PdfPageBreak is only supported in direct child of PdfView.")
            }
            if (it is PdfContainer) {
                it.verifyContent()
            }
        }
    }

    override fun drawContent(context: PdfContext, body: Box2d): List<PdfDrawnElement> {
        val drawElements = elements.map { it.draw(context = context, parent = body) }.flatten()
        return listOf(PdfDrawnElement(
            x = body.x,
            y = body.y,
            xAbs = body.x - padding.left,
            yAbs = body.y - padding.top,
            type = type,
            identifier = identifier,
            text = null
        )) + drawElements
    }

    override fun getChildren(): List<PdfComponent> {
        return elements
    }
}
