package com.andres_k.lib.library.core.component.container

import com.andres_k.lib.library.core.component.PdfComponent
import com.andres_k.lib.library.core.property.*
import com.andres_k.lib.library.utils.*
import com.andres_k.lib.library.utils.config.PdfContext
import com.andres_k.lib.library.utils.config.PdfProperties
import com.andres_k.lib.library.utils.data.PdfOverdrawResult
import java.awt.Color

/**
 * Created on 2020/07/20.
 *
 * @author Kevin Andres
 */
@Suppress("DataClassPrivateConstructor")
data class PdfList private constructor(
    private val interLine: Float?,
    override val elements: List<PdfRow>,
    override val identifier: String?,
    override val position: Position,
    override val size: Size,
    override val bodyAlign: BodyAlign?,
    override val padding: Spacing,
    override val margin: Spacing,
    override val splitOnOverdraw: Boolean,
    override val color: Color?,
    override val background: Background,
    override val borders: Borders,
    override val isBuilt: Boolean
) : PdfContainer(elements, splitOnOverdraw, identifier, position, size, bodyAlign, padding, margin, color, background, borders, isBuilt, Type.LIST) {

    constructor(
        elements: List<PdfComponent>,
        interLine: Float? = null,
        identifier: String? = null,
        position: Position = Position.ORIGIN,
        bodyAlign: BodyAlign? = null,
        padding: Spacing = Spacing.NONE,
        margin: Spacing = Spacing.NONE,
        splitOnOverdraw: Boolean = true,
        color: Color? = null,
        background: Background = Background.NONE,
        borders: Borders = Borders.NONE
    ) : this(interLine, elements.map { PdfRow(arrayListOf(it)) }, identifier, position, Size.NULL, bodyAlign, padding, margin, splitOnOverdraw, color, background, borders, false)

    fun rows(): List<PdfRow> = elements

    override fun preRenderContent(context: PdfContext, body: Box2d): PdfOverdrawResult {
        /** Try render elements **/
        var overdrawIndex: Int? = null
        drawRow@ for (i in elements.indices) {
            if (elements[i].hasOverdrawY(body, context.viewBody)) {
                overdrawIndex = if (splitOnOverdraw) i else 0
                break@drawRow
            }
        }

        /** Build List of overdraw elements **/
        if (overdrawIndex != null) {
            if (!splitOnOverdraw && this.height().eqOrBigger(context.viewBody.height)) {
                throw IllegalArgumentException("[PdfList] The height of the List is superior to a page's height. splitOnOverdraw has to be set to True")
            }
            val overdrawRows = elements.subList(overdrawIndex, elements.size)
            var cursorY = 0f

            val calcOverdrawElements = overdrawRows.map { row ->
                val result: PdfRow = row.copyAbs(Position(row.position.x, cursorY), isBuilt = true)
                cursorY += row.height()
                result
            }
            val mainList = if (overdrawIndex != 0) {
                this.copy(elements = elements.subList(0, overdrawIndex), size = Size(size.width?.v, contentHeight() - cursorY))
            } else null

            return PdfOverdrawResult(
                main = mainList,
                overdraw = this.copy(elements = calcOverdrawElements, size = Size(size.width?.v, cursorY + padding.spacingY()))
            )
        }
        return PdfOverdrawResult(main = this)
    }

    override fun buildContent(context: PdfContext, request: Box2dRequest, parent: BoxSize): PdfComponent {
        val calcPos = Position(calcX(request, parent.width), calcY(request, parent.height), PosProperty.FIXED)
        val calcWidth: Float? = calcWidth(request, parent.width)
        val calcHeight: Float? = calcHeight(request, parent.height)
        val containerWidth: Float? = if (calcWidth != null) calcWidth - padding.spacingX() else null
        val containerHeight: Float? = if (calcHeight != null) calcHeight - padding.spacingY() else null

        var cursorY = 0f
        var maxWidth = 0f

        /** CALCULATE max Height & Width for each Row **/
        rows().forEach {
            val itSize = it.calcMaxSize(context = context, parent = BoxSize(containerWidth, containerHeight))
            if (itSize.width.bigger(maxWidth)) {
                maxWidth = itSize.width
            }
        }

        /** CALCULATE position for each Row **/
        val calcElements: MutableList<PdfRow> = arrayListOf()
        rows().forEach {
            val item: PdfRow = it.build(context, Box2dRequest(y = cursorY), BoxSize(containerWidth
                ?: maxWidth, containerHeight)) as PdfRow
            calcElements.add(item)
            cursorY += item.height() + getInterLine(context.properties)
        }
        maxWidth = calcWidth ?: maxWidth + padding.spacingX()
        val maxHeight = calcHeight ?: cursorY + padding.spacingY()

        return this.copy(elements = calcElements, position = calcPos, size = Size(maxWidth, maxHeight), isBuilt = true)
    }

    override fun calcMaxSize(context: PdfContext, parent: BoxSize): SizeResult {
        val calcWidth: Float? = calcWidth(null, parent.width, true)
        val calcHeight: Float? = calcHeight(null, parent.height, true)
        val containerWidth: Float? = if (calcWidth != null) calcWidth - padding.spacingX() else null
        val containerHeight: Float? = if (calcHeight != null) calcHeight - padding.spacingY() else null

        var maxWidth = 0f
        var maxHeight = 0f

        rows().forEach {
            val itSize = it.calcMaxSize(context = context, parent = BoxSize(containerWidth, containerHeight))
            if (itSize.width.bigger(maxWidth)) {
                maxWidth = itSize.width
            }
            maxHeight += itSize.height + getInterLine(context.properties)
        }
        return SizeResult(maxWidth + padding.spacingX() + margin.spacingX(), maxHeight + padding.spacingY() + margin.spacingY())
    }

    private fun getInterLine(properties: PdfProperties): Float {
        return interLine ?: properties.defaultInterline
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
