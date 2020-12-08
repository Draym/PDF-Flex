package com.andres_k.lib.library.core.component.container

import com.andres_k.lib.library.core.component.PdfComponent
import com.andres_k.lib.library.core.property.*
import com.andres_k.lib.library.utils.*
import java.awt.Color

/**
 * Created on 2020/07/20.
 *
 * @author Kevin Andres
 */
@Suppress("DataClassPrivateConstructor")
data class PdfView private constructor(
    override val elements: List<PdfComponent>,
    override val position: Position,
    override val size: Size,
    override val bodyAlign: BodyAlign?,
    override val padding: Spacing,
    override val margin: Spacing,
    override val color: Color?,
    override val background: Background,
    override val borders: Borders,
    override val isBuilt: Boolean
) : PdfContainer(elements, true, position, size, bodyAlign, padding, margin, color, background, borders, isBuilt, Type.VIEW) {

    constructor(
        elements: List<PdfComponent>,
        position: Position = Position.ORIGIN,
        size: Size = Size.NULL,
        bodyAlign: BodyAlign? = null,
        padding: Spacing = Spacing.NONE,
        margin: Spacing = Spacing.NONE,
        color: Color? = null,
        background: Background = Background.NONE,
        borders: Borders = Borders.NONE
    ) : this(elements, position, size, bodyAlign, padding, margin, color, background, borders, false)

    override fun preRenderContent(context: PdfContext, body: Box2d): PdfOverdrawResult {
        val drawElements: MutableList<PdfComponent> = arrayListOf()
        val overdrawElements: MutableList<PdfComponent> = arrayListOf()

        var drawHeight = 0f

        /** Try render elements **/
        elements.forEach {
            val result = it.preRender(context = context, parent = body)
            if (result.main != null) {
                if (drawHeight.smaller(result.main.endY())) {
                    drawHeight = result.main.endY()
                }
                drawElements.add(result.main)
            }
            if (result.overdraw != null) {
                overdrawElements.add(result.overdraw)
            }
        }

        /** Build Overdraw View elements **/
        var cursorY = 0f
        var previousX = 0f
        var maxLineHeight = 0f
        val calcOverdrawElements = overdrawElements.map { element ->
            val result: PdfComponent = element.copyAbs(Position(element.position.x, cursorY), isBuilt = true)

            if (result.height().bigger(maxLineHeight)) {
                maxLineHeight = result.height()
            }
            if (result.position.x.eqOrSmaller(previousX)) {
                cursorY += maxLineHeight
                maxLineHeight = 0f
            }
            previousX = result.position.x
            result
        }

        return if (calcOverdrawElements.isNotEmpty()) {
            val mainView = if (drawElements.isNotEmpty()) this.copy(elements = drawElements, size = Size(bodyWidth(), drawHeight)) else null
            PdfOverdrawResult(
                main = mainView,
                overdraw = this.copy(elements = calcOverdrawElements, size = Size(bodyWidth(), bodyHeight() - drawHeight)))
        } else {
            PdfOverdrawResult(main = this)
        }
    }

    override fun buildContent(context: PdfContext, request: Box2dRequest, parent: BoxSize): PdfComponent {
        val calcPos = Position(calcX(request, parent.width), calcY(request, parent.height), PosProperty.FIXED)
        val calcWidth: Float? = calcWidth(request, parent.width)
        val calcHeight: Float? = calcHeight(request, parent.height)

        val containerWidth: Float? = if (calcWidth != null) calcWidth - padding.spacingX() else null
        val containerHeight: Float? = if (calcHeight != null) calcHeight - padding.spacingY() else null

        if (containerWidth != null && containerWidth.eq(0f) && elements.isNotEmpty()) {
            throw IllegalArgumentException("[PdfView] view width is 0 but has ${elements.size} item(s) to draw")
        }

        var cursorX = 0f
        var cursorY = 0f
        var nextLine = 0f

        //if (elements.size > 0 && elements[0].position.y.smaller(0f)) elements[0].position.y else

        val calcElements = elements.map {
            var item: PdfComponent = it.build(context, Box2dRequest(cursorX, cursorY), BoxSize(containerWidth, containerHeight))

            if (containerWidth != null && (cursorX + item.width()).eqOrBigger(containerWidth)) {
                cursorY += nextLine
                nextLine = 0f
                if (cursorX.not(0f)) {
                    item = it.build(context, Box2dRequest(0f, cursorY), BoxSize(containerWidth, containerHeight))
                }
                cursorX = 0f
            }
            if (item.height().bigger(nextLine)) {
                nextLine = if (it.position.y.smaller(0f)) it.position.y + item.height() else item.height()
            }
            cursorX += item.width()
            item
        }
        cursorY += nextLine

        return this.copy(elements = calcElements,
            position = calcPos,
            size = Size(SizeAttr(calcWidth ?: cursorX + padding.spacingX()),
                SizeAttr(calcHeight ?: cursorY + padding.spacingY())),
            isBuilt = true)
    }

    override fun calcMaxSize(context: PdfContext, parent: BoxSize): SizeResult {
        val calcWidth: Float? = calcWidth(null, parent.width, true)
        val calcHeight: Float? = calcHeight(null, parent.height, true)
        val containerWidth: Float? = if (calcWidth != null) calcWidth - padding.spacingX() else null
        val containerHeight: Float? = if (calcHeight != null) calcHeight - padding.spacingY() else null

        var cursorX = 0f
        var cursorY = if (elements.size > 0 && elements[0].position.y.smaller(0f)) elements[0].position.y else 0f
        var nextLine = 0f

        elements.forEach {
            val itSize = it.calcMaxSize(context = context, parent = BoxSize(containerWidth, containerHeight))

            if (itSize.height.bigger(nextLine)) {
                nextLine = if (it.position.y.smaller(0f)) it.position.y + itSize.height else itSize.height
            }
            if (containerWidth != null && (cursorX + itSize.width).eqOrBigger(containerWidth)) {
                cursorY += nextLine
                nextLine = 0f
                cursorX = if (cursorX.not(0f)) itSize.width else 0f
            } else {
                cursorX += itSize.width
            }
        }
        cursorY += nextLine
        return SizeResult(calcWidth
            ?: cursorX + padding.spacingX() + margin.spacingX(), if (calcHeight != null) calcHeight + margin.spacingY() else cursorY + padding.spacingY() + margin.spacingY())
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
        return this.copy(
            position = position ?: this.position,
            size = size ?: this.size,
            bodyAlign = bodyAlign ?: this.bodyAlign,
            padding = padding ?: this.padding,
            margin = margin ?: this.margin,
            color = color ?: this.color,
            background = background ?: this.background,
            borders = borders ?: this.borders,
            isBuilt = isBuilt
        ) as T
    }
}
