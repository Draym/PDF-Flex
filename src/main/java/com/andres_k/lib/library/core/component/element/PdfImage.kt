package com.andres_k.lib.library.core.component.element

import com.andres_k.lib.library.core.component.PdfComponent
import com.andres_k.lib.library.core.property.*
import com.andres_k.lib.library.utils.DrawUtils
import com.andres_k.lib.library.utils.FontCode
import com.andres_k.lib.library.utils.PdfContext
import com.andres_k.lib.library.utils.PdfOverdrawResult
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import java.awt.Color

/**
 * Created on 2020/07/20.
 *
 * @author Kevin Andres
 */
@Suppress("DataClassPrivateConstructor")
data class PdfImage private constructor(
    val image: PDImageXObject,
    override val size: Size,
    val autoFill: Boolean,
    val respectRatio: Boolean,
    override val position: Position,
    override val bodyAlign: BodyAlign?,
    override val padding: Spacing,
    override val margin: Spacing,
    override val color: Color?,
    override val background: Background,
    override val borders: Borders,
    override val isBuilt: Boolean
) : PdfComponent(position, size, bodyAlign, padding, margin, color, background, borders, isBuilt, Type.IMAGE) {

    val aspectRation: Float = image.width.toFloat() / image.height.toFloat()

    constructor(
        image: PDImageXObject,
        size: ReqSize? = null,
        autoFill: Boolean = true,
        respectRatio: Boolean = true,
        position: Position = Position.ORIGIN,
        bodyAlign: BodyAlign? = null,
        margin: Spacing = Spacing.NONE,
        color: Color? = null,
        background: Background = Background.NONE,
        borders: Borders = Borders.NONE
    ) : this(image, size
        ?: Size(image.width.toFloat(), image.height.toFloat()), autoFill, respectRatio, position, bodyAlign, Spacing.NONE, margin, color, background, borders, false)

    override fun drawContent(context: PdfContext, body: Box2d) {
        DrawUtils.drawImage(
            stream = context.stream(),
            image = image,
            x = body.x,
            y = body.y - body.height,
            width = body.width,
            height = body.height
        )
    }

    override fun preRenderContent(context: PdfContext, body: Box2d): PdfOverdrawResult {
        return if (hasOverdrawY(body, context.viewBody)) {
            PdfOverdrawResult(overdraw = this)
        } else {
            PdfOverdrawResult(main = this)
        }
    }

    override fun buildContent(context: PdfContext, request: Box2dRequest, parent: BoxSize): PdfImage {
        val calcPos = Position(calcX(request, parent.width), calcY(request, parent.height), PosProperty.FIXED)
        val resized = calcImageResize(parent)
        return this.copy(size = Size(resized.width, resized.height), position = calcPos, isBuilt = true)
    }

    override fun calcMaxSize(context: PdfContext, parent: BoxSize): SizeResult {
        val imageResize = calcImageResize(parent)
        return imageResize.copy(
            width = imageResize.width  + padding.spacingX() + margin.spacingX(),
            height = imageResize.height + padding.spacingY() + margin.spacingY()
        )
    }

    private fun calcImageResize(parent: BoxSize): SizeResult {
        val resizedWidth: Float
        val resizedHeight: Float

        if (!autoFill) {
            return SizeResult(bodyWidth(), bodyHeight())
        }

        if (!respectRatio && parent.width != null && parent.height != null) {
            resizedWidth = parent.width
            resizedHeight = parent.height
        } else if (respectRatio && parent.height != null) {
            resizedHeight = parent.height
            resizedWidth = resizedHeight * aspectRation
        } else if (respectRatio && parent.width != null) {
            resizedWidth = parent.width
            resizedHeight = resizedWidth / aspectRation
        } else {
            resizedWidth = parent.width ?: bodyWidth()
            resizedHeight = parent.height ?: bodyHeight()
        }
        return SizeResult(resizedWidth, resizedHeight)
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

