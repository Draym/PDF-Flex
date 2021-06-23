package com.andres_k.lib.library.core.component.container

import com.andres_k.lib.library.core.component.PdfComponent
import com.andres_k.lib.library.core.property.*
import com.andres_k.lib.library.utils.FontCode
import com.andres_k.lib.library.utils.config.PdfContext
import com.andres_k.lib.library.utils.data.PdfOverdrawResult
import java.awt.Color

/**
 * Created on 2020/07/20.
 *
 * @author Kevin Andres
 */
@Suppress("DataClassPrivateConstructor")
data class PdfCol private constructor(
    override val elements: List<PdfComponent>,
    override val identifier: String?,
    override val position: Position,
    override val size: Size,
    override val padding: Spacing,
    override val color: Color?,
    override val background: Background,
    override val borders: Borders,
    override val isBuilt: Boolean,
) : PdfContainer(elements, false, identifier, position, size, null, padding, Spacing.NONE, color, background, borders, isBuilt, Type.COL) {

    fun content(): PdfComponent? = if (elements.isNotEmpty()) elements[0] else null

    constructor(
        content: PdfComponent,
        maxWidth: SizeAttr? = null,
        identifier: String? = null,
        padding: Spacing = Spacing.NONE,
        color: Color? = null,
        background: Background = Background.NONE,
        borders: Borders = Borders.NONE,
    ) : this(listOf(content), identifier, Position.ORIGIN, Size(maxWidth, SizeAttr.percent(100f)), padding, color, background, borders, false)

    // empty col
    constructor(
        color: Color? = null,
        background: Background = Background.NONE,
        borders: Borders = Borders.NONE,
    ) : this(listOf(), null, Position.ORIGIN, Size(null, SizeAttr.percent(100f)), Spacing.NONE, color, background, borders, false)

    override fun preRenderContent(context: PdfContext, body: Box2d): PdfOverdrawResult {
        val result = content()?.preRender(context = context, parent = body)

        return PdfOverdrawResult(
            main = if (result?.main != null) this.copy(elements = listOf(result.main), size = Size(size.width?.v, result.main.height())) else null,
            overdraw = if (result?.overdraw != null) this.copy(elements = listOf(result.overdraw), size = Size(size.width?.v, result.overdraw.height())) else null
        )
    }

    override fun buildContent(context: PdfContext, request: Box2dRequest, parent: BoxSize): PdfComponent {
        val calcPos = Position(calcX(request, parent), calcY(request, parent), PosProperty.FIXED)
        val calcWidth: Float? = calcWidth(request, parent)
        val calcHeight: Float? = calcHeight(request, parent)
        val containerWidth: Float? = if (calcWidth != null) calcWidth - padding.spacingX() else null
        val containerHeight: Float? = if (calcHeight != null) calcHeight - padding.spacingY() else null

        //println("New col : $calcWidth ; $calcHeight ($parentWidth; $parentHeight) $containerWidth, $containerHeight")

        var maxWidth = content()?.calcMaxSize(context = context, parent = BoxSize(containerWidth, containerHeight, type))?.width
            ?: 0f

        val calcElement = content()?.build(context, Box2dRequest(), BoxSize(containerWidth
            ?: maxWidth, containerHeight, type))

        maxWidth = calcWidth ?: maxWidth + padding.spacingX()
        val maxHeight = calcHeight ?: (calcElement?.height() ?: 0f) + padding.spacingY()

        return this.copy(elements = if (calcElement != null) listOf(calcElement) else emptyList(), size = Size(maxWidth, maxHeight), position = calcPos, isBuilt = true)
    }

    override fun calcMaxSize(context: PdfContext, parent: BoxSize): SizeResult {
        val calcWidth: Float? = calcWidth(null, parent, true)
        val calcHeight: Float? = calcHeight(null, parent, true)
        val containerWidth: Float? = if (calcWidth != null) calcWidth - padding.spacingX() else null
        val containerHeight: Float? = if (calcHeight != null) calcHeight - padding.spacingY() else null

        val contentSizes = content()?.calcMaxSize(context = context, parent = BoxSize(containerWidth, containerHeight, type))
            ?: SizeResult(0f, 0f)

        //println("** col return ${SizeAttr(contentSizes.width + padding.spacingX() + margin.spacingX(), contentSizes.height + padding.spacingY() + margin.spacingY())}")
        return SizeResult(calcWidth
            ?: contentSizes.width + padding.spacingX() + margin.spacingX(), contentSizes.height + padding.spacingY() + margin.spacingY())
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
            size = size ?: this.size,
            padding = padding ?: this.padding,
            color = color ?: this.color,
            background = background ?: this.background,
            borders = borders ?: this.borders,
            isBuilt = isBuilt
        ) as T
    }

}
