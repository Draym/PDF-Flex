package com.andres_k.lib.library.output

import org.apache.pdfbox.pdmodel.PDDocument
import java.io.OutputStream

/**
 * Created on 2020/10/22.
 *
 * @author Kevin Andres
 */
class PdfToStream(private val output: OutputStream) : OutputBuilder {
    override fun validateOutput() {
    }

    override fun save(document: PDDocument) {
        document.save(output)
    }

    override fun close() {
        output.close()
    }
}
