package com.andres_k.lib.library.core.component.container

import com.andres_k.lib.library.core.component.PdfComponent
import com.andres_k.lib.library.core.property.*
import com.andres_k.lib.library.utils.*
import com.andres_k.lib.library.utils.config.PdfContext
import com.andres_k.lib.library.utils.data.PdfOverdrawResult
import java.awt.Color

/**
 * Created on 2020/07/20.
 *
 * @author Kevin Andres
 */
@Suppress("DataClassPrivateConstructor")
data class PdfRow private constructor(
    override val elements: List<PdfComponent>,
    override val identifier: String?,
    override val position: Position,
    override val size: Size,
    override val padding: Spacing,
    override val margin: Spacing,
    override val splitOnOverdraw: Boolean,
    override val color: Color?,
    override val background: Background,
    override val borders: Borders,
    override val isBuilt: Boolean
) : PdfContainer(elements, splitOnOverdraw, identifier, position, size, null, padding, margin, color, background, borders, isBuilt, Type.ROW) {

    constructor(
        elements: List<PdfComponent>,
        identifier: String? = null,
        position: Position = Position.ORIGIN,
        maxHeight: SizeAttr? = null,
        padding: Spacing = Spacing.NONE,
        margin: Spacing = Spacing.NONE,
        splitOnOverdraw: Boolean = true,
        color: Color? = null,
        background: Background = Background.NONE,
        borders: Borders = Borders.NONE
    ) : this(elements, identifier, position, Size(width = SizeAttr.percent(100f), height = maxHeight), padding, margin, splitOnOverdraw, color, background, borders, false)

    constructor(
        element: PdfComponent,
        identifier: String? = null,
        position: Position = Position.ORIGIN,
        maxHeight: SizeAttr? = null,
        padding: Spacing = Spacing.NONE,
        margin: Spacing = Spacing.NONE,
        splitOnOverdraw: Boolean = true,
        color: Color? = null,
        background: Background = Background.NONE,
        borders: Borders = Borders.NONE
    ) : this(listOf(element), identifier, position, Size(width = SizeAttr.percent(100f), height = maxHeight), padding, margin, splitOnOverdraw, color, background, borders, false)


    override fun preRenderContent(context: PdfContext, body: Box2d): PdfOverdrawResult {
        val drawElements: MutableList<PdfComponent> = arrayListOf()
        val overdrawElements: MutableList<PdfComponent> = arrayListOf()

        if (elements.isEmpty() && this.hasOverdrawY(body, context.viewBody)) {
            return PdfOverdrawResult(overdraw = this)
        }

        /** Try render elements **/
        var drawHeight = 0f
        var overdrawHeight = 0f
        elements.forEach {
            val result = it.preRender(context = context, parent = body)
            if (result.main != null) {
                if (drawHeight.smaller(result.main.height())) {
                    drawHeight = result.main.height()
                }
                drawElements.add(result.main)
            }
            if (result.overdraw != null) {
                if (overdrawHeight.smaller(result.overdraw.height())) {
                    overdrawHeight = result.overdraw.height()
                }
                overdrawElements.add(result.overdraw)
            }
        }

        if (overdrawElements.isEmpty()) {
            return PdfOverdrawResult(main = this)
        } else {
            return if (splitOnOverdraw) {
                val finalDrawElements = drawElements.map { col -> col.copyAbs<PdfComponent>(size = Size(col.size.width?.v, drawHeight)) }
                val finalOverdrawElements = overdrawElements.map { col -> col.copyAbs<PdfComponent>(size = Size(col.size.width?.v, overdrawHeight)) }

                val mainRow = if (finalDrawElements.isNotEmpty()) this.copy(elements = finalDrawElements, size = Size(size.width?.v, drawHeight + padding.spacingY())) else null
                PdfOverdrawResult(main = mainRow,
                    overdraw = this.copy(elements = finalOverdrawElements, size = Size(size.width?.v, overdrawHeight + padding.spacingY())))
            } else {
                if (this.height().eqOrBigger(context.viewBody.height)) {
                    throw IllegalArgumentException("[PdfRow] The height of the Row is superior to a page's height. splitOnOverdraw has to be set to True")
                }
                PdfOverdrawResult(overdraw = this)
            }
        }
    }

    override fun buildContent(context: PdfContext, request: Box2dRequest, parent: BoxSize): PdfComponent {
        val calcPos = Position(calcX(request, parent.width), calcY(request, parent.height), PosProperty.FIXED)
        val calcWidth: Float? = calcWidth(request, parent.width)
        val calcHeight: Float? = calcHeight(request, parent.height)
        val containerWidth: Float? = if (calcWidth != null) calcWidth - padding.spacingX() else null
        val containerHeight: Float? = if (calcHeight != null) calcHeight - padding.spacingY() else null

        if (containerWidth != null && containerWidth.eq(0f)) {
            println("----{WARNING}----[PdfRow] row width is 0")
        }

        var cursorX = 0f
        var maxHeight = 0f

        elements.forEach {
            val itSize = it.calcMaxSize(context = context, parent = BoxSize(containerWidth, containerHeight))
            if (itSize.height.bigger(maxHeight)) {
                maxHeight = itSize.height
            }
        }

        val calcElements: MutableList<PdfComponent> = arrayListOf()
         elements.forEach calculate@{
            val item = it.build(context, Box2dRequest(cursorX), BoxSize(containerWidth, containerHeight
                ?: maxHeight))
            //println("create col on $cursorX, $calcWidth, {$calcHeight?:$maxHeight} -> ${item.size.width}; ${item.width()}")
            cursorX += item.width()

            if (containerWidth != null && cursorX.bigger(containerWidth)) {
                println("----{WARNING}----[PdfRow] items{${item.javaClass.simpleName}} are too large for the row (row: $containerWidth; columns: $cursorX)")
                if (!context.properties.drawOverflowX) return@calculate
            }
            calcElements.add(item)
        }
        return this.copy(elements = calcElements, position = calcPos, size = Size(calcWidth, maxHeight + padding.spacingY()), isBuilt = true)
    }

    override fun calcMaxSize(context: PdfContext, parent: BoxSize): SizeResult {
        val calcWidth: Float? = calcWidth(null, parent.width, true)
        val calcHeight: Float? = calcHeight(null, parent.height, true)
        val containerWidth: Float? = if (calcWidth != null) calcWidth - padding.spacingX() else null
        val containerHeight: Float? = if (calcHeight != null) calcHeight - padding.spacingY() else null


        var maxWidth = 0f
        var maxHeight = 0f

        elements.forEach {
            val itSize = it.calcMaxSize(context = context, parent = BoxSize(containerWidth, containerHeight))
            if (itSize.height.bigger(maxHeight)) {
                maxHeight = itSize.height
            }
            maxWidth += itSize.width
        }

        //println(". row: ${SizeAttr(maxWidth + padding.spacingX() + margin.spacingX(), maxHeight + padding.spacingY() + margin.spacingY())}")
        return SizeResult(maxWidth + padding.spacingX() + margin.spacingX(), maxHeight + padding.spacingY() + margin.spacingY())
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
            padding = padding ?: this.padding,
            margin = margin ?: this.margin,
            color = color ?: this.color,
            background = background ?: this.background,
            borders = borders ?: this.borders,
            isBuilt = isBuilt
        ) as T
    }
}
