package com.andres_k.lib.builder.template

import com.andres_k.lib.builder.PdfBuilder
import com.andres_k.lib.library.core.component.custom.PdfFooter
import com.andres_k.lib.library.core.component.custom.PdfHeader
import com.andres_k.lib.library.core.document.PdfDocument
import com.andres_k.lib.library.core.page.PdfPage
import com.andres_k.lib.library.output.OutputBuilder
import com.andres_k.lib.library.utils.FontCode
import com.andres_k.lib.library.utils.PdfFontLoader
import com.andres_k.lib.library.utils.config.PdfDebugContext
import com.andres_k.lib.library.utils.config.PdfProperties
import com.andres_k.lib.library.utils.data.PdfMetadata
import com.andres_k.lib.parser.PdfExplorer
import org.apache.fontbox.ttf.TrueTypeFont
import org.apache.pdfbox.pdmodel.font.PDFont

/**
 * Created on 2020/12/03.
 *
 * @author Kevin Andres
 */
abstract class PdfBaseTemplate(
    metadata: PdfMetadata = PdfMetadata.NONE,
) : PdfBuilder {

    protected val document = PdfDocument(metadata)

    /**
     * Generate a PDF using specified output
     *
     * Create Header & Footer & Pages then draw a PDF
     *
     * @param output the generated PDF will be rendered into the output
     */
    final override fun build(output: OutputBuilder): PdfExplorer {
        // INIT
        val desiredCustomFont = includeCustomFonts()
        val loadedFonts = loadFont(desiredCustomFont)
        val standardFonts = includeStandardFonts()
        val fonts = standardFonts + loadedFonts

        // CREATE
        val header = createHeader()
        val footer = createFooter()
        val pages = createPages()

        // SETUP
        val defaultProperties = getPdfDefaultProperties().copy(availableFont = fonts)
        val debugSettings = getPdfDefaultDebugSettings()

        // DRAW
        return document.draw(
            builder = output,
            pages = pages,
            header = header,
            footer = footer,
            properties = defaultProperties,
            debug = debugSettings
        )
    }

    private fun loadFont(fonts: Map<FontCode, TrueTypeFont>): Map<FontCode, PDFont> {
        return fonts.map { (key, font) ->
            key to PdfFontLoader.loadTTCFont(document.document, font)
        }.toMap()
    }

    protected abstract fun includeCustomFonts(): Map<FontCode, TrueTypeFont>
    protected abstract fun includeStandardFonts(): Map<FontCode, PDFont>

    protected abstract fun createHeader(): PdfHeader?
    protected abstract fun createFooter(): PdfFooter?
    protected abstract fun createPages(): List<PdfPage>
    protected abstract fun getPdfDefaultProperties(): PdfProperties
    protected abstract fun getPdfDefaultDebugSettings(): PdfDebugContext

    override fun close() {
        document.close()
    }
}
