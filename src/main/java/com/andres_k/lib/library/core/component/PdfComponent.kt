package com.andres_k.lib.library.core.component

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
abstract class PdfComponent(
    open val identifier: String?,
    open val position: Position,
    open val size: Size,
    open val bodyAlign: BodyAlign?,
    open val padding: Spacing,
    open val margin: Spacing,
    open val color: Color?,
    open val background: Background,
    open val borders: Borders,
    open val isBuilt: Boolean,
    open val type: ComponentType
) {

    protected abstract fun buildContent(context: PdfContext, request: Box2dRequest, parent: BoxSize): PdfComponent

    abstract fun calcMaxSize(context: PdfContext, parent: BoxSize): SizeResult

    protected abstract fun preRenderContent(context: PdfContext, body: Box2d): PdfOverdrawResult
    protected abstract fun drawContent(context: PdfContext, body: Box2d): List<PdfDrawnElement>

    fun build(context: PdfContext, request: Box2dRequest, parent: BoxSize): PdfComponent {
        if (isBuilt) {
            //println("----{WARNING}----[$type] trying to reBuild")
            return this
        }
        return buildContent(context, request, parent)
    }

    open fun verifyContent() {}

    fun preRender(context: PdfContext, parent: Box2d): PdfOverdrawResult {
        return preRenderContent(context, getDrawableContent(parent))
    }

    fun draw(context: PdfContext, parent: Box2d): List<PdfDrawnElement> {
        val drawableContent = getDrawableContent(parent)
        drawBackground(context, parent)
        if (context.properties.debugOn) {
            drawMargin(context, parent)
            drawPadding(context, parent)
        }
        drawBorder(context, parent)
        return drawContent(context, drawableContent)
    }

    private fun drawBackground(context: PdfContext, parent: Box2d) {
        val startX = alignedX(parent).toDrawX(parent.x)
        val startY = alignedY(parent).toDrawY(parent.y)
        //println("draw (${this.javaClass.simpleName}) background at ${bodyX().toDrawX(parent.x)}=$startX, ${bodyY().toDrawY(parent.y)}=$startY [[$margin :: $padding]]")

        if (background.color != null || (context.properties.debugOn && context.debug.hasBackground(type))) {
            DrawUtils.drawRect(context.stream(), startX, startY, bodyWidth(), bodyHeight(), background.color
                ?: context.debug.background[type]!!, filled = true)
        }
        if (background.image != null) {
            DrawUtils.drawImage(context.stream(), background.image!!, startX, startY, bodyWidth(), bodyHeight())
        }
    }

    private fun drawBorder(context: PdfContext, parent: Box2d) {
        val startX = alignedX(parent).toDrawX(parent.x)
        val startY = alignedY(parent).toDrawY(parent.y)
        val endX = startX + bodyWidth()
        val endY = startY - bodyHeight()

        val borders: Borders = if (context.properties.debugOn && context.debug.hasBorder(type)) {
            this.borders.copy(top = this.borders.top ?: context.debug.borders[type],
                right = this.borders.right ?: context.debug.borders[type],
                bottom = this.borders.bottom ?: context.debug.borders[type],
                left = this.borders.left ?: context.debug.borders[type])
        } else {
            this.borders
        }

        if (borders.top != null) {
            DrawUtils.drawLine(context.stream(), startX, startY, endX, startY, borders.top.color
                ?: defaultColor(context), borders.top.thickness)
        }
        if (borders.right != null) {
            DrawUtils.drawLine(context.stream(), endX, startY, endX, endY, borders.right.color
                ?: defaultColor(context), borders.right.thickness)
        }
        if (borders.bottom != null) {
            DrawUtils.drawLine(context.stream(), startX, endY, endX, endY, borders.bottom.color
                ?: defaultColor(context), borders.bottom.thickness)
        }
        if (borders.left != null) {
            DrawUtils.drawLine(context.stream(), startX, startY, startX, endY, borders.left.color
                ?: defaultColor(context), borders.left.thickness)
        }
    }

    private fun drawMargin(context: PdfContext, parent: Box2d) {
        val startX = (alignedX(parent) - margin.left).toDrawX(parent.x)
        val startY = (alignedY(parent) - margin.top).toDrawY(parent.y)
        val endX = startX + width()
        val endY = startY - height()
        DrawUtils.drawRect(context.stream(), startX, startY, width(), margin.top, Color.ORANGE.withAlpha(0.2f), filled = true)
        DrawUtils.drawRect(context.stream(), startX, endY, width(), -margin.bottom, Color.ORANGE.withAlpha(0.2f), filled = true)
        DrawUtils.drawRect(context.stream(), startX, startY, margin.left, height(), Color.ORANGE.withAlpha(0.2f), filled = true)
        DrawUtils.drawRect(context.stream(), endX, startY, -margin.right, height(), Color.ORANGE.withAlpha(0.2f), filled = true)
    }

    private fun drawPadding(context: PdfContext, parent: Box2d) {
        val startX = alignedX(parent).toDrawX(parent.x)
        val startY = alignedY(parent).toDrawY(parent.y)
        val endX = startX + bodyWidth()
        val endY = startY - bodyHeight()
        DrawUtils.drawRect(context.stream(), startX, startY, bodyWidth(), padding.top, Color.GREEN.withAlpha(0.2f), filled = true)
        DrawUtils.drawRect(context.stream(), startX, endY, bodyWidth(), -padding.bottom, Color.GREEN.withAlpha(0.2f), filled = true)
        DrawUtils.drawRect(context.stream(), startX, startY, padding.left, bodyHeight(), Color.GREEN.withAlpha(0.2f), filled = true)
        DrawUtils.drawRect(context.stream(), endX, startY, -padding.right, bodyHeight(), Color.GREEN.withAlpha(0.2f), filled = true)
    }

    abstract fun getChildren(): List<PdfComponent>

    abstract fun <T : PdfComponent> copyAbs(
        position: Position? = null,
        size: Size? = null,
        bodyAlign: BodyAlign? = null,
        padding: Spacing? = null,
        margin: Spacing? = null,
        color: Color? = null,
        font: FontCode? = null,
        background: Background? = null,
        borders: Borders? = null,
        isBuilt: Boolean = false
    ): T

    /** CALCULATION **/

    protected fun calcX(request: Box2dRequest, parent: BoxSize): Float {
        return if (position.property == PosProperty.RELATIVE) {
            if (request.x != null) {
                println("--{WARNING}--[Position] requestedX ignored")
            }
            if (parent.width == null) {
                throw IllegalArgumentException("[Position] child($type) can't have relative x (%) if parent(${parent.type} doesn't have a known width")
            } else parent.width * position.x / 100
        } else position.x + (request.x ?: 0f)
    }

    protected fun calcY(request: Box2dRequest, parent: BoxSize): Float {
        return if (position.property == PosProperty.RELATIVE) {
            if (request.y != null) {
                println("--{WARNING}--[Position] requestedY ignored")
            }
            if (parent.height == null) {
                throw IllegalArgumentException("[Position] child($type) can't have relative y (%) if parent(${parent.type}) doesn't have a known height")
            } else parent.height * position.y / 100
        } else position.y + (request.y ?: 0f)
    }

    protected fun calcWidth(request: Box2dRequest? = null, parent: BoxSize, ignore: Boolean = false): Float? {
        if (request?.width != null) {
            return request.width
        }
        return if (size.width == null) {
            null
        } else {
            if (size.width!!.isPercentage) {
                if (parent.width == null) {
                    if (ignore) return null else throw IllegalArgumentException("[SizeParameter] child($type) can't have relative width (%) if parent(${parent.type}) doesn't have a known width")
                } else {
                    (parent.width * size.width!!.v / 100) - margin.spacingX()
                }
            } else {
                size.width!!.v
            }
        }
    }

    protected fun calcHeight(request: Box2dRequest? = null, parent: BoxSize, ignore: Boolean = false): Float? {
        if (request?.height != null) {
            return request.height
        }
        return if (size.height == null) {
            null
        } else {
            if (size.height!!.isPercentage) {
                if (parent.height == null) {
                    if (ignore) return null else throw IllegalArgumentException("[SizeParameter] child($type) can't have relative height (%) if parent(${parent.type}) doesn't have a known height")
                } else {
                    (parent.height * size.height!!.v / 100) - margin.spacingY()
                }
            } else {
                size.height!!.v
            }
        }
    }

    private fun alignedX(parent: Box2d) = bodyAlign?.horizontal?.transform(this.getBody(), margin, parent) ?: bodyX()
    private fun alignedY(parent: Box2d) = bodyAlign?.vertical?.transform(this.getBody(), margin, parent) ?: bodyY()

    /** GETTER ONLY USABLE FOR POST-CALCULATION **/
    fun x(): Float = if (position.property != PosProperty.RELATIVE) position.x else throw IllegalArgumentException("[PdfElement] Position need to be recalculated before get x")
    fun y(): Float = if (position.property != PosProperty.RELATIVE) position.y else throw IllegalArgumentException("[PdfElement] Position need to be recalculated before get y")
    fun endX(): Float = x() + width()
    fun endY(): Float = y() + height()
    fun width(): Float = bodyWidth() + margin.spacingX()
    fun height(): Float = bodyHeight() + margin.spacingY()

    protected fun bodyX(): Float = x() + margin.left
    protected fun bodyY(): Float = y() + margin.top
    protected fun bodyEndX(): Float = bodyX() + bodyWidth()
    protected fun bodyEndY(): Float = bodyY() + bodyHeight()
    protected fun bodyWidth(): Float = if (size.width != null && !size.width!!.isPercentage) size.width!!.v else throw IllegalArgumentException("[PdfElement] Size need to be recalculated before get width (${size.width})")
    protected fun bodyHeight(): Float = if (size.height != null && !size.height!!.isPercentage) size.height!!.v else throw IllegalArgumentException("[PdfElement] Size need to be recalculated before get height (${size.height})")

    protected fun getBody(): Box2d = Box2d(bodyX(), bodyY(), bodyWidth(), bodyHeight())

    protected fun contentX(): Float = bodyX() + padding.left
    protected fun contentY(): Float = bodyY() + padding.top
    protected fun contentWidth(): Float = bodyWidth() - padding.spacingX()
    protected fun contentHeight(): Float = bodyHeight() - padding.spacingY()

    /** GETTER USED TO DRAW THE FINAL X & Y **/
    protected fun defaultColor(context: PdfContext): Color {
        return color ?: context.properties.color
    }

    private fun drawableX(parent: Box2d): Float {
        return alignedX(parent).toDrawX(parent.x)
    }

    private fun drawableY(parent: Box2d): Float {
        return alignedY(parent).toDrawY(parent.y)
    }

    private fun getDrawableContent(parent: Box2d): Box2d {
        return Box2d(alignedX(parent) + padding.left + parent.x, parent.y - (alignedY(parent) + padding.top), contentWidth(), contentHeight())
    }

    fun isOutOfPage(parent: Box2d, page: Box2d): Boolean {
        val x = drawableX(parent)
        val y = drawableY(parent)
        return !(x.within(page.x, page.maxX())
            && y.within(page.y - page.height, page.y))
    }

    fun hasOverdrawX(parent: Box2d, page: Box2d): Boolean {
        val x = drawableX(parent)
        val endX = x + width()
        if (isOutOfPage(parent, page)) {
            return true
        }
        return x.within(page.x, page.maxX())
            && !endX.within(page.x, page.maxX())
    }

    fun hasOverdrawY(parent: Box2d, page: Box2d): Boolean {
        val y = drawableY(parent)
        val endY = y - height()
        if (isOutOfPage(parent, page)) {
            //println("{$type} out of page pos: $y -> $endY page: $page, parent: $parent")
            return true
        }
        return y.within(page.y - page.height, page.y)
            && !endY.within(page.y - page.height, page.y)
    }
}
