package com.andres_k.lib.library.core.component.custom

import com.andres_k.lib.library.core.component.PdfComponent
import com.andres_k.lib.library.core.property.Box2d
import com.andres_k.lib.library.core.property.Box2dRequest
import com.andres_k.lib.library.core.property.BoxSize
import com.andres_k.lib.library.utils.config.PdfContext
import com.andres_k.lib.library.utils.data.PdfDrawnElement

/**
 * Created on 2020/07/20.
 *
 * @author Kevin Andres
 */
data class PdfComponentHolder(val content: PdfComponent) {

    fun build(context: PdfContext, request: Box2dRequest, parent: BoxSize): PdfComponentHolder {
        return this.copy(content = content.build(context, request, parent))
    }

    fun verifyContent() {
        content.verifyContent()
    }

    fun draw(context: PdfContext, parent: Box2d): List<PdfDrawnElement> {
        return content.draw(context = context, parent = parent)
    }

    fun height(): Float {
        return content.height()
    }

    fun width(): Float {
        return content.width()
    }

    companion object {
        val NONE: PdfComponentHolder? = null
    }
}

typealias PdfHeader = PdfComponentHolder
typealias PdfFooter = PdfComponentHolder
