package com.andres_k.lib.extension.template

import com.andres_k.lib.builder.template.PdfBaseTemplate
import com.andres_k.lib.library.core.component.PdfComponent
import com.andres_k.lib.library.core.component.custom.PdfFooter
import com.andres_k.lib.library.core.component.custom.PdfHeader
import com.andres_k.lib.library.core.page.PdfPage
import com.andres_k.lib.library.core.property.Spacing
import com.andres_k.lib.library.utils.BaseFont
import com.andres_k.lib.library.utils.FontCode
import com.andres_k.lib.library.utils.config.PdfDebugContext
import com.andres_k.lib.library.utils.config.PdfProperties
import org.apache.fontbox.ttf.TrueTypeFont
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDType1Font

/**
 * Created on 2021/06/28.
 *
 * @author Kevin Andres
 */
class BaseTestTemplate(private val components: List<PdfComponent>) : PdfBaseTemplate() {
    override fun includeCustomFonts(): Map<FontCode, TrueTypeFont> {
        return emptyMap()
    }

    override fun includeStandardFonts(): Map<FontCode, PDFont> {
        return mapOf(
            BaseFont.DEFAULT.code to PDType1Font.HELVETICA,
            BaseFont.BOLD.code to PDType1Font.HELVETICA_BOLD
        )
    }

    override fun createHeader(): PdfHeader? {
        return null
    }

    override fun createFooter(): PdfFooter? {
        return null
    }

    override fun createPages(): List<PdfPage> {
        return listOf(
            PdfPage(
                elements = components,
                padding = Spacing(0f, 0f, 0f, 0f)
            )
        )
    }

    override fun getPdfDefaultProperties(): PdfProperties {
        return PdfProperties()
    }

    override fun getPdfDefaultDebugSettings(): PdfDebugContext {
        return PdfDebugContext.DEFAULT
    }
}