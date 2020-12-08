package com.andres_k.lib.library.holder

import com.andres_k.lib.library.core.component.custom.PdfFooter
import com.andres_k.lib.library.core.component.custom.PdfHeader
import com.andres_k.lib.library.output.OutputBuilder
import com.andres_k.lib.library.utils.PdfContextDebug
import com.andres_k.lib.library.utils.PdfMetadata
import com.andres_k.lib.library.utils.PdfPageProperties
import com.andres_k.lib.library.utils.PdfProperties
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPageContentStream

/**
 * Created on 2020/07/20.
 *
 * @author Kevin Andres
 */
class PdfDocument(
    metadata: PdfMetadata = PdfMetadata.NONE
) {
    val document: PDDocument = PDDocument()

    init {
        if (metadata.author != null) {
            document.documentInformation.author = metadata.author
        }
        if (metadata.creator != null) {
            document.documentInformation.creator = metadata.creator
        }
        if (metadata.title != null) {
            document.documentInformation.title = metadata.title
        }
        if (metadata.subject != null) {
            document.documentInformation.subject = metadata.subject
        }
        if (metadata.custom.isNotEmpty()) {
            metadata.custom.map { document.documentInformation.setCustomMetadataValue(it.first, it.second) }
        }
    }

    private fun calculatePages(
        pages: List<PdfPage>,
        header: PdfHeader?,
        footer: PdfFooter?,
        properties: PdfProperties,
        debug: PdfContextDebug,
    ): List<PdfPage> {
        val mutablePages: MutableList<PdfPage> = pages.toMutableList()
        val resultPages: MutableList<PdfPage> = arrayListOf()

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

    private fun computeDocument(
        pages: List<PdfPage>,
        header: PdfHeader? = PdfHeader.NONE,
        footer: PdfFooter? = PdfFooter.NONE,
        properties: PdfProperties = PdfProperties.DEFAULT,
        debug: PdfContextDebug = PdfContextDebug.DEFAULT,
    ) {
        /** Calculate final pages : done separately to know the total number of page before draw **/
        val calcPages: List<PdfPage> = calculatePages(pages, header, footer, properties, debug)

        calcPages.forEachIndexed { i, page ->

            /** Create content stream **/
            val contentStream = PDPageContentStream(document, page.page)

            /** Draw **/
            page.draw(contentStream, PdfPageProperties(i + 1, calcPages.size), properties, debug)

            /** Close and save page **/
            contentStream.close()
            document.addPage(page.page)
        }
    }

    fun draw(
        builder: OutputBuilder,
        pages: List<PdfPage>,
        header: PdfHeader? = PdfHeader.NONE,
        footer: PdfFooter? = PdfFooter.NONE,
        properties: PdfProperties = PdfProperties.DEFAULT,
        debug: PdfContextDebug = PdfContextDebug.DEFAULT,
    ) {
        builder.validateOutput()
        computeDocument(
            pages = pages,
            header = header,
            footer = footer,
            properties = properties,
            debug = debug
        )
        builder.save(document)
        document.document.close()
    }
}
