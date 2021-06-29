package com.andres_k.lib.test

import com.andres_k.lib.extension.output.DummyOutput
import com.andres_k.lib.library.utils.config.PdfProperties
import java.io.File

/**
 * Created on 2021/06/28.
 *
 * @author Kevin Andres
 */
open class PdfFlexBase {
    protected val properties = PdfProperties.DEFAULT
    protected val pdfPath = "pdf_output"
    protected val dummyOutput = DummyOutput()

    protected fun getPdfTestPath(fileName: String): File {
        return File("$pdfPath/$fileName")
    }
}