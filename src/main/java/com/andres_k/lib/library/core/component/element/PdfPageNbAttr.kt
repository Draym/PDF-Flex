package com.andres_k.lib.library.core.component.element

import com.andres_k.lib.library.core.component.PdfComponent
import com.andres_k.lib.library.core.property.*
import com.andres_k.lib.library.utils.DrawUtils
import com.andres_k.lib.library.utils.Font
import com.andres_k.lib.library.utils.FontCode
import com.andres_k.lib.library.utils.FontUtils
import com.andres_k.lib.library.utils.config.PdfContext
import com.andres_k.lib.library.utils.data.PdfDrawnElement
import com.andres_k.lib.library.utils.data.PdfOverdrawResult
import java.awt.Color

/**
 * Created on 2020/07/20.
 *
 * @author Kevin Andres
 */
@Suppress("DataClassPrivateConstructor")
data class PdfPageNbAttr private constructor(
    private val numberType: NbType,
    private val font: FontCode?,
    private val fontSize: Float?,
    override val identifier: String?,
    override val position: Position,
    override val size: Size,
    override val bodyAlign: BodyAlign?,
    override val padding: Spacing,
    override val margin: Spacing,
    override val color: Color?,
    override val background: Background,
    override val borders: Borders,
    override val isBuilt: Boolean
) : PdfComponent(identifier, position, Size(), bodyAlign, padding, margin, color, background, borders, isBuilt, Type.PAGE_NB) {

    constructor(
        numberType: NbType,
        font: FontCode? = null,
        fontSize: Float? = null,
        identifier: String? = null,
        position: Position = Position.ORIGIN,
        bodyAlign: BodyAlign? = null,
        padding: Spacing = Spacing.NONE,
        margin: Spacing = Spacing.NONE,
        color: Color? = null,
        background: Background = Background.NONE,
        borders: Borders = Borders.NONE,
    ) : this(numberType, font, fontSize, identifier, position, Size(), bodyAlign, padding, margin, color, background, borders, false)

    enum class NbType {
        CURRENT,
        TOTAL
    }

    override fun drawContent(context: PdfContext, body: Box2d): List<PdfDrawnElement> {
        //println("draw $text at $position")
        val text = getValue(context)
        DrawUtils.drawText(
            stream = context.stream(),
            x = body.x,
            y = body.y - (body.height * 3 / 4),
            text = text,
            font = getFont(context).font,
            fontSize = getFontSize(context),
            color = defaultColor(context)
        )
        return listOf(PdfDrawnElement(
            x = body.x,
            y = body.y,
            xAbs = body.x - padding.left,
            yAbs = body.y - padding.top,
            type = type,
            identifier = identifier,
            text = text
        ))
    }

    override fun preRenderContent(context: PdfContext, body: Box2d): PdfOverdrawResult {
        return if (hasOverdrawY(body, context.viewBody)) {
            PdfOverdrawResult(overdraw = this)
        } else {
            PdfOverdrawResult(main = this)
        }
    }

    override fun buildContent(context: PdfContext, request: Box2dRequest, parent: BoxSize): PdfPageNbAttr {
        val font = getFont(context)
        val size = Size(
            width = FontUtils.getTextWidth(getValue(context), font.font, getFontSize(context)),
            height = FontUtils.getTextHeight(font.font, getFontSize(context))
        )
        val calcPos = Position(calcX(request, parent.width), calcY(request, parent.height), PosProperty.FIXED)
        return this.copy(font = getFont(context).code, fontSize = getFontSize(context), position = calcPos, size = size, isBuilt = true)
    }

    override fun calcMaxSize(context: PdfContext, parent: BoxSize): SizeResult {
        val font = getFont(context)
        return SizeResult(
            width = FontUtils.getTextWidth(getValue(context), font.font, getFontSize(context)),
            height = FontUtils.getTextHeight(font.font, getFontSize(context))
        )
    }

    private fun getValue(context: PdfContext): String {
        return if (numberType == NbType.CURRENT) context.page.pageNumber.toString() else context.page.pageTotal.toString()
    }

    fun getFont(context: PdfContext): Font {
        return context.properties.getFont(this.font)
    }

    fun getFontSize(context: PdfContext): Float {
        return context.properties.getFontSize(this.fontSize)
    }

    override fun getChildren(): List<PdfComponent> {
        return emptyList()
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : PdfComponent> copyAbs(
        position: Position?,
        size: Size?,
        bodyAlign: BodyAlign?,
        padding: Spacing?,
        margin: Spacing?,
        color: Color?,
        font: FontCode?,
        background: Background?,
        borders: Borders?,
        isBuilt: Boolean
    ): T {
        return this.copy(position = position ?: this.position,
            size = size ?: this.size,
            bodyAlign = bodyAlign ?: this.bodyAlign,
            padding = padding ?: this.padding,
            margin = margin ?: this.margin,
            color = color ?: this.color,
            font = font ?: this.font,
            background = background ?: this.background,
            borders = borders ?: this.borders,
            isBuilt = isBuilt) as T
    }
}
