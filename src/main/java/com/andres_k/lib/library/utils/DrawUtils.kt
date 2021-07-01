package com.andres_k.lib.library.utils

import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState
import org.apache.pdfbox.util.Matrix
import java.awt.Color
import java.awt.geom.AffineTransform
import java.awt.geom.Point2D

/**
 * Created on 2020/07/20.
 *
 * @author Kevin Andres
 */
object DrawUtils {

    fun drawShape(
        stream: PDPageContentStream,
        points: List<Point2D>,
        fillColor: Color? = null,
        color: Color,
        weight: Float = 1f
    ) {
        if (points.isNotEmpty()) {
            stream.setLineWidth(weight)
            setColor(stream, borderColor = color, fillColor = fillColor)
            stream.moveTo(points[0].x.toFloat(), points[0].y.toFloat())
            points.map { point ->
                stream.lineTo(point.x.toFloat(), point.y.toFloat())
            }
            if (fillColor != null) {
                stream.fillAndStroke()
            } else {
                stream.stroke()
            }
        }
    }

    fun drawLine(
        stream: PDPageContentStream,
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        color: Color,
        weight: Float = 1f
    ) {
        //println("draw line at $x1, $y1 to $x2, $y2")
        setColor(stream, borderColor = color)
        stream.setLineWidth(weight)
        stream.moveTo(x1, y1)
        stream.lineTo(x2, y2)
        stream.stroke()
    }

    fun drawRect(
        stream: PDPageContentStream,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        color: Color,
        weight: Float = 1f,
        filled: Boolean = false,
        fillColor: Color? = null
    ) {
        //println("draw rect at $x, $y with $width, $height and color $color ${color.alpha}")
        if (width.eq(0f) || height.eq(0f)) {
            return
        }
        setColor(stream, if (filled) fillColor ?: color else null, color)
        stream.setLineWidth(weight)
        stream.addRect(x, y, width, -height)
        if (filled) stream.fill() else stream.stroke()
    }

    fun drawText(
        stream: PDPageContentStream,
        x: Float,
        y: Float,
        text: String,
        font: PDFont,
        fontSize: Float,
        color: Color
    ) {
        stream.setFont(font, fontSize)
        setColor(stream, fillColor = color)
        stream.beginText()
        stream.newLineAtOffset(x, y)
        stream.showText(text)
        stream.endText()
    }

    fun drawImage(
        stream: PDPageContentStream,
        image: PDImageXObject,
        x: Float,
        y: Float,
        width: Float?,
        height: Float?
    ) {
        drawImage(stream, image, Matrix(AffineTransform(width ?: image.width.toFloat(), 0f, 0f, height
            ?: image.height.toFloat(), x, y)))
    }

    fun drawImage(
        stream: PDPageContentStream,
        image: PDImageXObject,
        matrix: Matrix
    ) {
        stream.drawImage(image, matrix)
    }

    fun setColor(
        stream: PDPageContentStream,
        fillColor: Color? = null,
        borderColor: Color? = null
    ) {
        fillColor?.let {
            PDExtendedGraphicsState().also {
                it.nonStrokingAlphaConstant = fillColor.alpha.toFloat() / 255f
                stream.setGraphicsStateParameters(it)
            }
            stream.setNonStrokingColor(fillColor)
        }
        borderColor?.let {
            stream.setStrokingColor(borderColor)
            PDExtendedGraphicsState().also {
                it.strokingAlphaConstant = borderColor.alpha.toFloat() / 255f
                stream.setGraphicsStateParameters(it)
            }
        }
    }
}

fun Float.toDrawX(parentX: Float): Float {
    return parentX + this
}

fun Float.toDrawY(parentY: Float): Float {
    return parentY - this
}

fun Color.withAlpha(alpha: Float): Color {
    return Color(this.red, this.green, this.blue, (alpha * 255f).toInt())
}
