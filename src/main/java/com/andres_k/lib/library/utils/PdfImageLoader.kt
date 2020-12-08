package com.andres_k.lib.library.utils

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import java.net.URL

/**
 * Created on 2020/12/04.
 *
 * @author Kevin Andres
 */
object PdfImageLoader {
    fun loadFrom(document: PDDocument, image: ByteArray): PDImageXObject {
        return PDImageXObject.createFromByteArray(document, image, "image")
    }

    fun loadFrom(document: PDDocument, url: URL): PDImageXObject {
        val bytes = url.readBytes()
        return loadFrom(document, bytes)
    }

    fun loadFrom(document: PDDocument, path: String): PDImageXObject {
        val bytes = javaClass.getResourceAsStream(path).readBytes()
        return loadFrom(document, bytes)
    }
}
