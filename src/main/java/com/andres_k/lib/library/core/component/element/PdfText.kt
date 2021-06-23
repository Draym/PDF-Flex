package com.andres_k.lib.library.core.component.element

import com.andres_k.lib.library.core.component.PdfComponent
import com.andres_k.lib.library.core.property.*
import com.andres_k.lib.library.utils.DrawUtils
import com.andres_k.lib.library.utils.Font
import com.andres_k.lib.library.utils.FontCode
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
data class PdfText private constructor(
    val text: String,
    val font: FontCode?,
    val fontSize: Float?,
    override val identifier: String?,
    override val position: Position,
    override val size: Size,
    override val bodyAlign: BodyAlign?,
    override val padding: Spacing,
    override val margin: Spacing,
    override val color: Color?,
    override val background: Background,
    override val borders: Borders,
    override val isBuilt: Boolean,
) : PdfComponent(identifier, position, size, bodyAlign, padding, margin, color, background, borders, isBuilt, Type.TEXT) {

    constructor(
        text: String,
        font: FontCode? = null,
        fontSize: Float? = null,
        identifier: String? = null,
        position: Position = Position.ORIGIN,
        bodyAlign: BodyAlign? = null,
        margin: Spacing = Spacing.NONE,
        color: Color? = null,
        background: Background = Background.NONE,
        borders: Borders = Borders.NONE,
    ) : this(text, font, fontSize, identifier, position, Size.NULL, bodyAlign, Spacing.NONE, margin, color, background, borders, false)

    override fun drawContent(context: PdfContext, body: Box2d): List<PdfDrawnElement> {
        val drawX = body.x
        val drawY = body.y - (body.height * 3 / 4)
        DrawUtils.drawText(
            stream = context.stream(),
            x = drawX,
            y = drawY,
            text = text,
            font = getFont(context).font,
            fontSize = getFontSize(context),
            color = defaultColor(context)
        )
        return listOf(PdfDrawnElement(
            x = drawX,
            y = drawY,
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

    override fun buildContent(context: PdfContext, request: Box2dRequest, parent: BoxSize): PdfText {
        val calcPos = Position(calcX(request, parent), calcY(request, parent), PosProperty.FIXED)

        //println("text pos = $calcPos")
        return this.copy(
            font = getFont(context).code,
            fontSize = getFontSize(context),
            position = calcPos,
            size = Size(getTextWidth(context), getTextHeight(context)),
            isBuilt = true
        )
    }

    override fun calcMaxSize(context: PdfContext, parent: BoxSize): SizeResult {
        //println(". text: ${SizeAttr(width(), height())}")
        return SizeResult(
            width = getTextWidth(context) + padding.spacingX() + margin.spacingX(),
            height = getTextHeight(context) + padding.spacingY() + margin.spacingY()
        )
    }

    fun getFont(context: PdfContext): Font {
        return context.properties.getFont(this.font)
    }

    fun getFontSize(context: PdfContext): Float {
        return context.properties.getFontSize(this.fontSize)
    }

    fun getTextWidth(
        context: PdfContext,
    ): Float {
        return this.getFont(context).font.getStringWidth(this.text) / 1000 * this.getFontSize(context)
    }

    fun getTextHeight(
        context: PdfContext,
    ): Float {
        return this.getFont(context).font.fontDescriptor.fontBoundingBox.height / 1000 * this.getFontSize(context)
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
        isBuilt: Boolean,
    ): T {
        return this.copy(
            position = position ?: this.position,
            bodyAlign = bodyAlign ?: this.bodyAlign,
            padding = padding ?: this.padding,
            margin = margin ?: this.margin,
            color = color ?: this.color,
            font = font ?: this.font,
            background = background ?: this.background,
            borders = borders ?: this.borders,
            isBuilt = isBuilt
        ) as T
    }
}
