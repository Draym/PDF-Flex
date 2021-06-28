package com.andres_k.lib.library.output

import org.apache.pdfbox.pdmodel.PDDocument
import java.io.File

/**
 * Created on 2020/10/22.
 *
 * @author Kevin Andres
 */
class PdfToFile(private val output: File): OutputBuilder {
    override fun validateOutput() {
        if (!output.absolutePath.endsWith(".pdf")) {
            throw IllegalArgumentException("The output file should be a PDF")
        }
    }

    override fun save(document: PDDocument) {
        document.save(output)
    }


    /**
     * Because we use the built-in file output of PDDocument, we dont have to close it there.
     * It will be closed with PDDocument in PdfDocument.close()
     **/
    override fun close() {
    }
}
