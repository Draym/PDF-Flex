package com.andres_k.lib.library.output

import org.apache.pdfbox.pdmodel.PDDocument
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * Created on 2020/10/22.
 *
 * @author Kevin Andres
 */
class PdfToByteStream : OutputBuilder {
    private val output: ByteArrayOutputStream = ByteArrayOutputStream()

    override fun validateOutput() {
    }

    override fun save(document: PDDocument) {
        document.save(output)
    }

    fun get(): ByteArrayInputStream {
        return ByteArrayInputStream(output.toByteArray())
    }

    override fun close() {
        output.close()
    }
}
