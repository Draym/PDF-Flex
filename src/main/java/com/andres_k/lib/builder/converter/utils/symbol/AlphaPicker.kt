package com.andres_k.lib.builder.converter.utils.symbol

/**
 * Created on 2020/11/02.
 *
 * @author Kevin Andres
 */
class AlphaPicker(
    private val delim: String = "",
    private val values: String = "abcdefghijklmnopqrstuvwxyz"
) : SymbolPicker {

    override fun get(step: Int): String {
        return if (step >= values.length) {
            val loop = step / values.length
            val index = step % values.length

            "${(0 until loop).map { values[0] }.joinToString(delim)}$delim${values[index - 1]}"
        } else {
            values[step - 1].toString()
        }
    }
}
