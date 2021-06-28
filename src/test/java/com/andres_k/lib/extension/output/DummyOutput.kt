package com.andres_k.lib.extension.output

import com.andres_k.lib.library.output.OutputBuilder
import org.apache.pdfbox.pdmodel.PDDocument

/**
 * Created on 2020/10/22.
 *
 * @author Kevin Andres
 */
class DummyOutput(): OutputBuilder {
    override fun validateOutput() {}
    override fun save(document: PDDocument) {}
    override fun close() {}
}
