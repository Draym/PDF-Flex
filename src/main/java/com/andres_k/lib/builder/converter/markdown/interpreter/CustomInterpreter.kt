package com.andres_k.lib.builder.converter.markdown.interpreter

import com.andres_k.lib.library.core.component.PdfComponent

/**
 * Created on 2020/12/09.
 *
 * @author Kevin Andres
 */
interface CustomInterpreter<T : PdfComponent> {
    val interpreter: Map<String, CustomInterpreterAction<T>>
    val interpreterStart: String
    val interpreterEnd: String
    val parameterStart: String
    val parameterEnd: String

    fun isInterpreterValue(value: String): Boolean {
        return value.trim().startsWith(interpreterStart) && value.trim().endsWith(interpreterEnd)
    }

    fun interpret(value: String): T?
}
