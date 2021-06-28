package com.andres_k.lib.library.core.page

import com.andres_k.lib.library.core.component.ComponentTypeCode
import com.andres_k.lib.library.core.component.PdfComponent
import com.andres_k.lib.library.core.component.container.PdfView
import com.andres_k.lib.library.core.component.custom.PdfFooter
import com.andres_k.lib.library.core.component.custom.PdfHeader
import com.andres_k.lib.library.core.property.*
import com.andres_k.lib.library.utils.config.PdfContext
import com.andres_k.lib.library.utils.config.PdfDebugContext
import com.andres_k.lib.library.utils.config.PdfPageProperties
import com.andres_k.lib.library.utils.config.PdfProperties
import com.andres_k.lib.library.utils.data.PdfDrawnElement
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle

/**
 * Created on 2020/07/20.
 *
 * @author Kevin Andres
 */
@Suppress("DataClassPrivateConstructor")
data class PdfPage private constructor(
    val view: PdfView,
    val page: PDPage,
    val padding: Spacing,
    private val header: PdfHeader?,
    private val footer: PdfFooter?,
) {

    constructor(
        elements: List<PdfComponent>,
        padding: Spacing,
        header: PdfHeader? = null,
        footer: PdfFooter? = null,
        pageBox: PDRectangle = PDRectangle.A4,
    ) : this(
        view = PdfView(elements = elements, size = ReqWidthSize(SizeAttr.percent(100f), SizeAttr.percent(100f)), background = Background.NONE),
        page = PDPage(pageBox),
        padding = padding,
        header = header,
        footer = footer
    )

    private val pageBody = Box2d(
        page.cropBox.lowerLeftX + padding.left,
        page.cropBox.upperRightY - padding.top,
        page.cropBox.width - padding.spacingX(),
        page.cropBox.height - padding.spacingY()
    )


    init {
        view.verifyContent()
    }

    fun build(
        pageProperties: PdfPageProperties,
        properties: PdfProperties,
        debug: PdfDebugContext,
        defaultHeader: PdfHeader?,
        defaultFooter: PdfFooter?,
    ): PdfPage {
        /** Init Header & Footer */
        val header = this.header ?: defaultHeader
        val footer = this.footer ?: defaultFooter
        header?.verifyContent()
        footer?.verifyContent()

        /** Build Header & Footer **/
        val contextPage = PdfContext(properties, null, pageProperties, pageBody, debug)
        val calculatedHeader = header?.build(contextPage, Box2dRequest.ORIGIN, BoxSize(pageBody.width, pageBody.height, ComponentTypeCode.PAGE.type))
        val calculatedFooter = footer?.build(contextPage, Box2dRequest.ORIGIN, BoxSize(pageBody.width, pageBody.height, ComponentTypeCode.PAGE.type))

        /** Build Content **/
        val viewBody = getContentBody(calculatedHeader?.content, calculatedFooter?.content)
        val contextCalculation = PdfContext(properties, null, pageProperties, viewBody, debug)
        val calculatedView = view.build(contextCalculation, Box2dRequest.ORIGIN, BoxSize(viewBody.width, viewBody.height, ComponentTypeCode.PAGE.type)) as PdfView

        /** return calculated PdfPage **/
        return this.copy(view = calculatedView, header = calculatedHeader, footer = calculatedFooter)
    }

    fun preRender(
        pageProperties: PdfPageProperties,
        properties: PdfProperties,
        debug: PdfDebugContext,
    ): Pair<PdfPage, PdfView?> {
        /** Create Contexts **/
        val viewBody = getContentBody(header?.content, footer?.content)
        val contextDraw = PdfContext(
            properties = properties,
            stream = null,
            page = pageProperties,
            viewBody = viewBody /*view.getDrawableContent(viewBody)*/,
            debug = debug
        )

        /** Pre-render View **/
        val overdrawResult = view.preRender(context = contextDraw, parent = viewBody)

        /** Return drawable page and overdraw content **/
        return Pair(
            first = this.copy(view = if (overdrawResult.main != null) overdrawResult.main as PdfView else PdfView(listOf())),
            second = overdrawResult.overdraw?.copyAbs(margin = Spacing.NONE, isBuilt = true))
    }

    fun draw(
        contentStream: PDPageContentStream,
        pageProperties: PdfPageProperties,
        properties: PdfProperties,
        debug: PdfDebugContext,
    ): List<PdfDrawnElement> {
        //println("draw page: origin(${page.cropBox.lowerLeftX + padding.left}, ${page.cropBox.upperRightY - padding.top})")
        /** Define final header & Footer **/
        val finalHeader: PdfComponent? = header?.content?.copyAbs(Position.ORIGIN)
        val finalFooter: PdfComponent? = footer?.content?.copyAbs(Position(0f, padding.top + pageBody.height - footer.content.height() - padding.bottom))

        /** Create Body & Contexts **/
        val viewBody = getContentBody(finalHeader, finalFooter)

        val contextPage = PdfContext(
            properties = properties,
            stream = contentStream,
            page = pageProperties,
            viewBody = pageBody,
            debug = debug
        )
        val contextDraw = PdfContext(
            properties = properties,
            stream = contentStream,
            page = pageProperties,
            viewBody = viewBody,//view.getDrawableContent(viewBody),
            debug = debug
        )

        /** Draw Header & Content & Footer **/
        val drawnHeader = finalHeader?.draw(context = contextPage, parent = pageBody) ?: emptyList()
        val drawnView = view.draw(context = contextDraw, parent = viewBody)
        val drawnFooter = finalFooter?.draw(context = contextPage, parent = pageBody) ?: emptyList()

        return drawnHeader + drawnView + drawnFooter
    }

    private fun getContentBody(header: PdfComponent?, footer: PdfComponent?): Box2d {
        return Box2d(
            x = pageBody.x,
            y = pageBody.y - (header?.height() ?: 0f),
            width = pageBody.width,
            height = pageBody.height - ((header?.height() ?: 0f) + (footer?.height() ?: 0f))
        )
    }
}
