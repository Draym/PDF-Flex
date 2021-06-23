package com.andres_k.lib.builder.converter.utils.interpreter

import com.andres_k.lib.library.core.component.element.PdfText

/**
 * Created on 2020/12/09.
 *
 * @author Kevin Andres
 */

/**
 * Custom Interpreter used for Markdown parsing
 *
 * When a PdfText bloc is detected it is possible to apply a custom interpreter to modify the output
 */
data class CustomTextInterpreter(
    override val interpreter: Map<String, CustomInterpreterAction<PdfText>>
) : CustomInterpreter<PdfText> {
    override val interpreterStart: String = "$=="
    override val interpreterEnd: String = "=="
    override val parameterStart: String = "\\("
    override val parameterEnd: String = "\\)"
    val parameterEq: String = "\\:"
    val parameterSeparator: String = ","

    override fun interpret(value: String): PdfText? {
        val index1 = value.indexOf(interpreterStart)
        val index2 = value.indexOf(interpreterEnd)
        val index3 = value.indexOf(parameterStart)
        val index4 = value.indexOf(parameterEnd)

        if (index1 == -1 || index2 == -1) {
            return null
        }
        return if (index3 < index4) {
            val action = value.substring(index1 + interpreterStart.length, index3)
            val parameters = value.substring(index3 + parameterStart.length, index4)
                .split(parameterSeparator, ignoreCase = true)
                .mapNotNull {
                    val elems = it.split(parameterEq, ignoreCase = true)
                    if (elems.size == 2) {
                        elems[0].trim() to elems[1].trim()
                    } else null
                }.toMap()
            interpreter[action]?.run(parameters)
        } else {
            val action = value.substring(index1 + interpreterStart.length, index2)
            interpreter[action]?.run(emptyMap())
        }
    }
}
