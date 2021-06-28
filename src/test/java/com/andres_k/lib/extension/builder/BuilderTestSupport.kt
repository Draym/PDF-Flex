package com.andres_k.lib.extension.builder

import com.andres_k.lib.library.core.component.PdfComponent
import com.andres_k.lib.library.core.component.custom.PdfFooter
import com.andres_k.lib.library.core.component.custom.PdfHeader
import com.andres_k.lib.library.core.page.PdfPage
import com.andres_k.lib.library.core.property.Box2d
import com.andres_k.lib.library.core.property.Box2dRequest
import com.andres_k.lib.library.core.property.BoxSize
import com.andres_k.lib.library.utils.config.PdfContext
import com.andres_k.lib.library.utils.config.PdfDebugContext
import com.andres_k.lib.library.utils.config.PdfPageProperties
import com.andres_k.lib.library.utils.config.PdfProperties

/**
 * Created on 2021/06/28.
 *
 * @author Kevin Andres
 */
object BuilderTestSupport {

    fun <T : PdfComponent> build(item: T, parent: BoxSize): T {
        val context = PdfContext(PdfProperties.DEFAULT, null, PdfPageProperties.ORIGIN, Box2d(0f, 0f, 0f, 0f), PdfDebugContext.DEFAULT)
        val request = Box2dRequest.ORIGIN
        return item.build(context, request, parent) as T
    }

    fun build(
        pages: List<PdfPage>,
        header: PdfHeader? = null,
        footer: PdfFooter? = null,
        properties: PdfProperties = PdfProperties.DEFAULT
    ): List<PdfPage> {
        val mutablePages: MutableList<PdfPage> = pages.toMutableList()
        val resultPages: MutableList<PdfPage> = arrayListOf()
        val debug = PdfDebugContext.DEFAULT

        var i = 0
        var max = mutablePages.size
        while (i < max) {
            val pageProperties = PdfPageProperties(i + 1, pages.size)

            /** Calculate page view and define pre-render **/
            val calculatedPage = mutablePages[i].build(pageProperties, properties, debug, header, footer)
            val preRenderResult = calculatedPage.preRender(pageProperties, properties, debug)

            /** Save drawable view of the first page **/
            resultPages.add(preRenderResult.first)

            /** Append the overdraw content to the next/new page **/
            val overdrawElement = preRenderResult.second
            if (overdrawElement != null) {
                if (properties.createPageOnOverdraw || i + 1 >= mutablePages.size) {
                    mutablePages.add(i + 1, PdfPage(overdrawElement.elements, mutablePages[i].padding))
                } else {
                    mutablePages[i + 1] = PdfPage(overdrawElement.elements + mutablePages[i + 1].view.elements, mutablePages[i + 1].padding)
                }
                max = mutablePages.size
            }
            ++i
        }
        return resultPages
    }
}