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
}
