package com.andres_k.lib.library.output

import org.apache.pdfbox.pdmodel.PDDocument
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * Created on 2020/10/22.
 *
 * @author Kevin Andres
 */
interface OutputBuilder {
    fun validateOutput()
    fun save(document: PDDocument)

    companion object {
        fun toFile(fileName: String): PdfToFile {
            return PdfToFile(output = File(fileName))
        }

        fun toFile(file: File): PdfToFile {
            return PdfToFile(output = file)
        }

        fun toStream(output: ByteArrayOutputStream): PdfToStream {
            return PdfToStream(output = output)
        }

        fun toByteStream(): PdfToByteStream {
            return PdfToByteStream()
        }

        fun toByteArray(): PdfToByteArray {
            return PdfToByteArray()
        }
    }
}
