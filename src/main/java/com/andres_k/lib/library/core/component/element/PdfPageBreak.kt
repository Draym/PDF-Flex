package com.andres_k.lib.library.core.component.element

import com.andres_k.lib.library.core.component.PdfComponent
import com.andres_k.lib.library.core.property.*
import com.andres_k.lib.library.utils.FontCode
import com.andres_k.lib.library.utils.config.PdfContext
import com.andres_k.lib.library.utils.data.PdfDrawnElement
import com.andres_k.lib.library.utils.data.PdfOverdrawResult
import java.awt.Color

/**
 * Created on 2020/12/09.
 *
 * @author Kevin Andres
 */
@Suppress("DataClassPrivateConstructor")
data class PdfPageBreak private constructor(
    override val identifier: String? = null,
    override val position: Position = Position.ORIGIN,
    override val isBuilt: Boolean,
) : PdfComponent(identifier, position, Size.NONE, null, Spacing.NONE, Spacing.NONE, null, Background.NONE, Borders.NONE, false, Type.PAGE_BREAK) {

    constructor() : this(isBuilt = false)

    override fun buildContent(context: PdfContext, request: Box2dRequest, parent: BoxSize): PdfComponent {
        return this.copy(isBuilt = true)
    }

    override fun calcMaxSize(context: PdfContext, parent: BoxSize): SizeResult {
        return SizeResult(0f, 0f)
    }

    override fun preRenderContent(context: PdfContext, body: Box2d): PdfOverdrawResult {
        return PdfOverdrawResult(main = null, overdraw = this)
    }

    override fun drawContent(context: PdfContext, body: Box2d): List<PdfDrawnElement> {
        return emptyList()
    }

    override fun getChildren(): List<PdfComponent> {
        return emptyList()
    }

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
            isBuilt = isBuilt
        ) as T
    }
}
