package com.andres_k.lib.library.core.component.custom

import com.andres_k.lib.library.core.component.element.PdfText
import com.andres_k.lib.library.core.property.Box2d
import com.andres_k.lib.library.core.property.Spacing
import com.andres_k.lib.library.utils.bigger
import com.andres_k.lib.library.utils.config.PdfContext

/**
 * Created on 2020/11/04.
 *
 * @author Kevin Andres
 */
data class PdfTextLine private constructor(
    val items: List<PdfText>,
    val forceInterLine: Boolean,
    val interLine: Float?,
) {
    /** Margin are not yet supported on independent PdfText within a PdfParagraph **/
    constructor(
        text: PdfText,
        forceInterLine: Boolean = false,
        interLine: Float? = null,
    ) : this(listOf(text.copy(margin = Spacing.NONE)), forceInterLine, interLine)

    constructor(
        text: String,
        forceInterLine: Boolean = false,
        interLine: Float? = null,
    ) : this(PdfText(text), forceInterLine, interLine)


    fun getTextWidth(context: PdfContext): Float {
        var width = 0f

        items.forEach {
            width += it.getTextWidth(context)
        }
        return width
    }

    fun getTextHeight(context: PdfContext): Float {
        var height = 0f

        items.forEach {
            val newHeight = it.getTextHeight(context)
            if (newHeight.bigger(height))
                height = newHeight
        }
        return height
    }

    fun width(): Float {
        var width = 0f

        items.forEach {
            width += it.width()
        }
        return width
    }

    fun height(): Float {
        var height = 0f

        items.forEach {
            val newHeight = it.height()
            if (newHeight.bigger(height))
                height = newHeight
        }
        return height
    }

    fun hasOverdrawX(parent: Box2d, page: Box2d): Boolean {
        for (text in items) {
            if (text.hasOverdrawX(parent, page)) {
                return true
            }
        }
        return false
    }

    fun hasOverdrawY(parent: Box2d, page: Box2d): Boolean {
        for (text in items) {
            if (text.hasOverdrawY(parent, page)) {
                return true
            }
        }
        return false
    }

    companion object {
        val EMPTY = of(emptyList())

        fun of(items: List<PdfText>,
               forceInterLine: Boolean = false,
               interLine: Float? = null): PdfTextLine {
            return PdfTextLine(items.map { it.copy(margin = Spacing.NONE) }, forceInterLine, interLine)
        }
    }
}

fun MutableList<PdfTextLine>.addText(index: Int, text: PdfText, interLine: Float? = null) {
    this[index] = PdfTextLine.of(items = this[index].items + text, interLine = interLine)
}

fun MutableList<PdfTextLine>.addText(index: Int, text: PdfTextLine, interLine: Float? = null) {
    this[index] = PdfTextLine.of(items = this[index].items + text.items, interLine = interLine)
}
