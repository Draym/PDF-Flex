package com.andres_k.lib.library.core.component.element

import com.andres_k.lib.library.core.component.PdfComponent
import com.andres_k.lib.library.core.property.*
import com.andres_k.lib.library.utils.DrawUtils
import com.andres_k.lib.library.utils.FontCode
import com.andres_k.lib.library.utils.PdfContext
import com.andres_k.lib.library.utils.PdfOverdrawResult
import java.awt.Color
import java.awt.geom.Point2D

/**
 * Created on 2020/07/20.
 *
 * @author Kevin Andres
 */
data class PdfShape(
    val points: List<Point2D>,
    val lineWeigh: Float = 1f,
    val respectParent: Boolean = true,
    val topIsOrigin: Boolean = true,
    override val color: Color? = null,
    override val background: Background = Background.NONE,
    override val isBuilt: Boolean = true
) : PdfComponent(Position.ORIGIN, Size.NONE, null, Spacing.NONE, Spacing.NONE, color, background, Borders.NONE, isBuilt, Type.SHAPE) {

    override fun drawContent(context: PdfContext, body: Box2d) {
        val parentX = if (respectParent) body.x else context.viewBody.x
        val parentY = if (respectParent) body.y else context.viewBody.y

        val finalPoints = points.map { point ->
            Point2D.Float(parentX + point.x.toFloat(),
                if (topIsOrigin) parentY - point.y.toFloat() else (parentY - body.height) + point.y.toFloat())
        }

        DrawUtils.drawShape(context.stream(), finalPoints, fillColor = background.color, color = defaultColor(context), weight = lineWeigh)
    }

    override fun preRenderContent(context: PdfContext, body: Box2d): PdfOverdrawResult {
        return PdfOverdrawResult(main = this)
    }

    override fun buildContent(context: PdfContext, request: Box2dRequest, parent: BoxSize): PdfShape {
        return this
    }

    override fun calcMaxSize(context: PdfContext, parent: BoxSize): SizeResult {
        return SizeResult(0f, 0f)
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
        return this.copy(color = color ?: this.color,
            background = background ?: this.background,
            isBuilt = isBuilt) as T
    }
}
