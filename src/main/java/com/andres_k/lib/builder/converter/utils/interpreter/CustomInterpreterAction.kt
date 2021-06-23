package com.andres_k.lib.builder.converter.utils.interpreter

import com.andres_k.lib.library.core.component.PdfComponent

/**
 * Created on 2020/12/10.
 *
 * @author Kevin Andres
 */
interface CustomInterpreterAction<T : PdfComponent> {

    fun run(parameters: Map<String, String>): T
}
