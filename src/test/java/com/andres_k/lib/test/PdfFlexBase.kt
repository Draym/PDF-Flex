package com.andres_k.lib.test

import com.andres_k.lib.extension.output.DummyOutput
import com.andres_k.lib.library.utils.config.PdfProperties

/**
 * Created on 2021/06/28.
 *
 * @author Kevin Andres
 */
open class PdfFlexBase {
    val properties = PdfProperties.DEFAULT
    val dummyOutput = DummyOutput()
}