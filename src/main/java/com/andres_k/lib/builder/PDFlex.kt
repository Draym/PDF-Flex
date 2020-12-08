package com.andres_k.lib.builder

import com.andres_k.lib.builder.converter.markdown.MarkdownToPDF

/**
 * Created on 2020/10/29.
 *
 * @author Kevin Andres
 */
object PDFlex {

    object Convert {

        fun fromMarkdown(content: String, paddingX: Float? = null, paddingY: Float? = null): PdfBuilder {
            return MarkdownToPDF(content, paddingX, paddingY)
        }

        fun fromJson(): PdfBuilder {
            TODO("not implemented yet")
        }
    }
}
