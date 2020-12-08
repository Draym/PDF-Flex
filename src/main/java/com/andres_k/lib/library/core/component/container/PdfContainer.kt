package com.andres_k.lib.library.core.component.container

import com.andres_k.lib.library.core.component.PdfComponent
import com.andres_k.lib.library.core.property.*
import com.andres_k.lib.library.utils.PdfContext
import java.awt.Color

/**
 * Created on 2020/07/20.
 *
 * @author Kevin Andres
 */
abstract class PdfContainer(
    open val elements: List<PdfComponent>,
    open val splitOnOverdraw: Boolean,
    position: Position,
    size: Size,
    bodyAlign: BodyAlign? = null,
    padding: Spacing,
    margin: Spacing,
    color: Color?,
    background: Background,
    borders: Borders,
    isBuilt: Boolean,
    type: Type
) : PdfComponent(position, size, bodyAlign, padding, margin, color, background, borders, isBuilt, type) {

    override fun verifyContent() {
        elements.forEach {
            if (this !is PdfRow && it is PdfCol) {
                throw IllegalArgumentException("[$type] PdfCol can only be contained by PdfRow")
            }
        }
    }

    override fun drawContent(context: PdfContext, body: Box2d) {
        elements.forEach { it.draw(context = context, parent = body) }
    }
}
