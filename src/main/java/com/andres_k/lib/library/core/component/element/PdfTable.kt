package com.andres_k.lib.library.core.component.element

import com.andres_k.lib.library.core.component.PdfComponent
import com.andres_k.lib.library.core.component.container.PdfCol
import com.andres_k.lib.library.core.property.*
import com.andres_k.lib.library.utils.*
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
data class PdfTable private constructor(
    val header: List<PdfCol>,
    val rows: List<List<PdfCol>>,
    val headerVisible: Boolean,
    val headerVisibleOnRebuilt: Boolean,
    val splitOnOverdraw: Boolean,
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
) : PdfComponent(identifier, position, size, bodyAlign, padding, margin, color, background, borders, isBuilt, Type.TABLE) {

    constructor(
        header: List<PdfCol>,
        rows: List<List<PdfCol>>,
        headerVisible: Boolean = true,
        headerVisibleOnRebuilt: Boolean? = null,
        splitOnOverdraw: Boolean = true,
        identifier: String? = null,
        position: Position = Position.ORIGIN,
        maxWidth: SizeAttr? = null,
        bodyAlign: BodyAlign? = null,
        padding: Spacing = Spacing.NONE,
        margin: Spacing = Spacing.NONE,
        color: Color? = null,
        background: Background = Background.NONE,
        borders: Borders = Borders.NONE,
    ) : this(
        header = header,
        rows = rows,
        headerVisible = headerVisible,
        headerVisibleOnRebuilt = headerVisibleOnRebuilt ?: headerVisible,
        splitOnOverdraw = splitOnOverdraw,
        identifier = identifier,
        position = position,
        size = Size(width = maxWidth),
        bodyAlign = bodyAlign,
        padding = padding,
        margin = margin,
        color = color,
        background = background,
        borders = borders,
        isBuilt = false
    )

    override fun drawContent(context: PdfContext, body: Box2d): List<PdfDrawnElement> {
        var drawnHeaders: List<PdfDrawnElement> = emptyList()
        /** Draw Header **/
        if (headerVisible) {
            drawnHeaders = header.map { col ->
                col.draw(context = context, parent = body)
            }.flatten()
        }

        /** Draw Table Elements **/
        val drawnRows = rows.map { row ->
            row.map { col -> col.draw(context = context, parent = body) }.flatten()
        }.flatten()
        return listOf(PdfDrawnElement(
            x = body.x,
            y = body.y,
            xAbs = body.x - padding.left,
            yAbs = body.y - padding.top,
            type = type,
            identifier = identifier,
            text = null
        )) + drawnHeaders + drawnRows
    }

    override fun preRenderContent(context: PdfContext, body: Box2d): PdfOverdrawResult {

        /** Check Header render **/
        if (headerVisible) {
            header.forEach { col ->
                val result = col.preRender(context = context, parent = body)
                if (result.overdraw != null) {
                    return PdfOverdrawResult(overdraw = this)
                }
            }
        }

        /** Check Elements render **/
        var overdrawIndex: Int? = null
        drawRows@ for (i in rows.indices) {
            var hasOverdraw = false
            rows[i].forEach {
                if (it.hasOverdrawY(parent = body, page = context.viewBody)) {
                    hasOverdraw = true
                }
            }
            if (hasOverdraw) {
                overdrawIndex = i
                break@drawRows
            }
        }

        /** Build Table of overdraw elements **/
        val headerHeight = if (headerVisibleOnRebuilt) (if (header.isNotEmpty()) header[0].height() else 0f) else 0f
        if (overdrawIndex != null) {
            if (!splitOnOverdraw) {
                if (this.height().eqOrBigger(context.viewBody.height)) {
                    throw IllegalArgumentException("[PdfTable] The height of the Table is superior to a page's height. splitOnOverdraw has to be set to True")
                } else {
                    return PdfOverdrawResult(overdraw = this)
                }
            }
            var cursorY = headerHeight
            val overdrawRows = rows.subList(overdrawIndex, rows.size)
            //println("overdrawRows: ${overdrawRows.size} $overdrawIndex")
            val calcOverdrawRows = overdrawRows.map { newRow ->
                var rowHeight = 0f
                val calcRow = newRow.map { newCol ->
                    rowHeight = newCol.height()
                    newCol.copy(position = Position(newCol.position.x, cursorY))
                }
                cursorY += rowHeight
                calcRow
            }
            //println("calc rows = ${calcRows.size}")
            val mainTable = if (overdrawIndex != 0) {
                this.copy(
                    rows = rows.subList(0, overdrawIndex),
                    size = Size(size.width?.v, contentHeight() + headerHeight - cursorY)
                )
            } else null
            return PdfOverdrawResult(
                main = mainTable,
                overdraw = this.copy(
                    rows = calcOverdrawRows,
                    headerVisible = headerVisibleOnRebuilt,
                    size = Size(size.width?.v, cursorY + padding.spacingY())
                ))
        }
        return PdfOverdrawResult(main = this)
    }

    override fun buildContent(context: PdfContext, request: Box2dRequest, parent: BoxSize): PdfComponent {
        val calcPos = Position(calcX(request, parent), calcY(request, parent), PosProperty.FIXED)
        val calcWidth: Float? = calcWidth(request, parent)
        val calcHeight: Float? = calcHeight(request, parent)
        val containerWidth: Float? = if (calcWidth != null) calcWidth - padding.spacingX() else null
        val containerHeight: Float? = if (calcHeight != null) calcHeight - padding.spacingY() else null

        val bodyBox = BoxSize(containerWidth, containerHeight, type)

        val savedMaxWidth: MutableMap<Int, Float> = mutableMapOf()
        val savedMaxHeight: MutableMap<Int, Float> = mutableMapOf()
        val fixedWidthIndexes: MutableList<Int> = mutableListOf()

        /** CALCULATE max Height & Width for each Row / Col **/

        (listOf(header) + rows).forEachIndexed { i1, row ->
            var maxHeight = 0f
            row.forEachIndexed { i2, col ->
                val itSize = col.calcMaxSize(context = context, parent = bodyBox)
                if (i1 == 0 && col.size.width != null) {
                    fixedWidthIndexes.add(i2)
                    if (col.size.width.isPercentage && containerWidth == null) {
                        throw IllegalArgumentException("[Table] the table need to be have a width if you wish to define column width in %")
                    }
                    savedMaxWidth[i2] = if (col.size.width.isPercentage) col.size.width.v * containerWidth!! / 100 else col.size.width.v
                    //println("Col: ${col.size.width}% of $containerWidth-> ${savedMaxWidth[i2]}")
                } else if (!fixedWidthIndexes.contains(i2) && itSize.width.bigger(savedMaxWidth[i2] ?: 0f)) {
                    savedMaxWidth[i2] = itSize.width
                }
                if (itSize.height.bigger(maxHeight)) {
                    maxHeight = itSize.height
                }
            }
            if (maxHeight.bigger(savedMaxHeight[i1] ?: 0f)) {
                savedMaxHeight[i1] = maxHeight
            }
        }

        /** CALCULATE position for each Row / Col **/

        var cursorY = 0f

        var cursorX = 0f
        val calcHeader: MutableList<PdfCol> = arrayListOf()
        header.forEachIndexed { i2, col ->
            val item: PdfCol = col.build(context, Box2dRequest(cursorX, cursorY, savedMaxWidth[i2], savedMaxHeight[0]), bodyBox) as PdfCol
            calcHeader.add(item)
            cursorX += savedMaxWidth[i2] ?: 0f
        }
        if (headerVisible) {
            cursorY += savedMaxHeight[0] ?: 0f
        }

        val calcRows: MutableList<MutableList<PdfCol>> = arrayListOf()
        rows.forEachIndexed { i, row ->
            val calcRow: MutableList<PdfCol> = arrayListOf()
            cursorX = 0f
            val i1 = i + 1 // because savedMaxHeight[0] is the header row
            row.forEachIndexed { i2, col ->
                val item: PdfCol = col.build(context, Box2dRequest(cursorX, cursorY, savedMaxWidth[i2], savedMaxHeight[i1]), bodyBox) as PdfCol
                calcRow.add(item)
                cursorX += savedMaxWidth[i2] ?: 0f
            }
            cursorY += savedMaxHeight[i1] ?: 0f
            calcRows.add(calcRow)
        }

        val maxWidth = calcWidth ?: savedMaxWidth.values.sum() + padding.spacingX()
        val maxHeight = calcHeight ?: cursorY + padding.spacingY()
        return this.copy(header = calcHeader, rows = calcRows, position = calcPos, size = Size(maxWidth, maxHeight), isBuilt = true)
    }

    override fun calcMaxSize(context: PdfContext, parent: BoxSize): SizeResult {
        val calcWidth: Float? = calcWidth(null, parent, true)
        val calcHeight: Float? = calcHeight(null, parent, true)
        val containerWidth: Float? = if (calcWidth != null) calcWidth - padding.spacingX() else null
        val containerHeight: Float? = if (calcHeight != null) calcHeight - padding.spacingY() else null


        val savedMaxWidth: MutableMap<Int, Float> = mutableMapOf()
        val fixedWidthIndexes: MutableList<Int> = mutableListOf()
        var cursorY = 0f

        (listOf(header) + rows).forEachIndexed { i1, row ->
            var maxHeight = 0f
            row.forEachIndexed { i2, col ->
                val itSize = col.calcMaxSize(context = context, parent = BoxSize(containerWidth, containerHeight, type))
                if (i1 == 0 && col.size.width != null) {
                    fixedWidthIndexes.add(i2)
                    if (col.size.width.isPercentage && containerWidth == null) {
                        throw IllegalArgumentException("[Table] the table need to be have a width if you wish to define column width in %")
                    }
                    savedMaxWidth[i2] = if (col.size.width.isPercentage) col.size.width.v * containerWidth!! / 100 else col.size.width.v
                } else if (!fixedWidthIndexes.contains(i2) && itSize.width.bigger(savedMaxWidth[i2] ?: 0f)) {
                    savedMaxWidth[i2] = itSize.width
                }
                if (itSize.height.bigger(maxHeight)) {
                    maxHeight = itSize.height
                }
            }
            cursorY += maxHeight
        }
        return SizeResult(calcWidth
            ?: savedMaxWidth.values.sum() + padding.spacingX() + margin.spacingX(), cursorY + padding.spacingY() + margin.spacingY())
    }

    override fun getChildren(): List<PdfComponent> {
        return header + rows.flatten()
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
        return this.copy(position = position ?: this.position,
            size = size ?: this.size,
            bodyAlign = bodyAlign ?: this.bodyAlign,
            padding = padding ?: this.padding,
            margin = margin ?: this.margin,
            color = color ?: this.color,
            background = background ?: this.background,
            borders = borders ?: this.borders,
            isBuilt = isBuilt) as T
    }
}

