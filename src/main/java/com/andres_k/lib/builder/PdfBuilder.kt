package com.andres_k.lib.builder

import com.andres_k.lib.library.output.OutputBuilder

/**
 * Created on 2020/07/20.
 *
 * @author Kevin Andres
 */
interface PdfBuilder {
    fun build(output: OutputBuilder)
}
