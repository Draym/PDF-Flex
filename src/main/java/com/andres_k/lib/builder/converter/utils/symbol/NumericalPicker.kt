package com.andres_k.lib.builder.converter.utils.symbol

/**
 * Created on 2020/11/02.
 *
 * @author Kevin Andres
 */
class NumericalPicker(
    private val delim: String = "."
) : SymbolPicker {

    override fun get(step: Int): String {
        return "$step$delim"
    }
}
