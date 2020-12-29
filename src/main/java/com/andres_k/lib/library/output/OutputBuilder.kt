package com.andres_k.lib.library.output

import org.apache.pdfbox.pdmodel.PDDocument
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * Created on 2020/10/22.
 *
 * @author Kevin Andres
 */
interface OutputBuilder: AutoCloseable {
    fun validateOutput()
    fun save(document: PDDocument)

    companion object {
        fun asFile(fileName: String): PdfToFile {
            return PdfToFile(output = File(fileName))
        }

        fun asFile(file: File): PdfToFile {
            return PdfToFile(output = file)
        }

        fun asStream(output: ByteArrayOutputStream): PdfToStream {
            return PdfToStream(output = output)
        }

        fun asByteStream(): PdfToByteStream {
            return PdfToByteStream()
        }

        fun asByteArray(): PdfToByteArray {
            return PdfToByteArray()
        }
    }
}
