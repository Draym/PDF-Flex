package com.andres_k.lib.builder.converter.markdown

import com.andres_k.lib.builder.converter.PdfConverterConfig
import com.andres_k.lib.builder.template.PdfBaseTemplate
import com.andres_k.lib.library.core.component.custom.PdfFooter
import com.andres_k.lib.library.core.component.custom.PdfHeader
import com.andres_k.lib.library.core.property.Spacing
import com.andres_k.lib.library.holder.PdfPage
import com.andres_k.lib.library.utils.BaseFont
import com.andres_k.lib.library.utils.EFont
import com.andres_k.lib.library.utils.config.PdfProperties
import org.apache.fontbox.ttf.TrueTypeFont
import org.apache.pdfbox.pdmodel.font.PDType1Font

/**
 * Created on 2020/10/29.
 *
 * @author Kevin Andres
 */


/**
 * Generate a PDF from a Markdown stylised text
 *
 * Base Impl without additional styling
 * Use MarkdownConverter to generate PDFlex components from text input
 */
class MarkdownToPDF(
    private val content: String,
    paddingX: Float? = null,
    paddingY: Float? = null,
    private val debug: Boolean = false
) : PdfBaseTemplate() {
    private val paddingX: Float = paddingX ?: 20f
    private val paddingY: Float = paddingY ?: 10f

    override fun getFontToLoad(): Map<EFont, TrueTypeFont> {
        return emptyMap()
    }

    override fun createHeader(): PdfHeader? {
        return null
    }

    override fun createFooter(): PdfFooter? {
        return null
    }

    override fun createPages(): List<PdfPage> {
        val font = BaseFont.DEFAULT.code
        val fontB = BaseFont.BOLD.code
        val fontL = BaseFont.ITALIC.code

        /** config **/
        val config = PdfConverterConfig(
            data = content,
            defaultFont = font,
            defaultFontBold = fontB,
            defaultFontItalic = fontL
        )

        /** PDF content **/
        val content = MarkdownConverter.markdownToPDFlex(
            text = content,
            config = config
        )
        val page = PdfPage(
            elements = content,
            padding = Spacing(paddingY, paddingX, paddingY, paddingX)
        )
        return listOf(page)
    }

    override fun getPdfDefaultProperties(): PdfProperties {
        return PdfProperties(
            debugOn = debug,
            availableFont = mapOf(
                BaseFont.DEFAULT.code to PDType1Font.TIMES_ROMAN,
                BaseFont.BOLD.code to PDType1Font.TIMES_BOLD,
                BaseFont.ITALIC.code to PDType1Font.TIMES_ITALIC
            )
        )
    }
}
