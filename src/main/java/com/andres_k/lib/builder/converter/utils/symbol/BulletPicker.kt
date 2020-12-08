package com.andres_k.lib.builder.converter.utils.symbol

/**
 * Created on 2020/11/02.
 *
 * @author Kevin Andres
 */
class BulletPicker : SymbolPicker {

    private val values: String = "•◦⁃"

    override fun get(step: Int): String {
        return if (step >= values.length) {
            val index = step % values.length
            values[index - 1].toString()
        } else {
            values[step - 1].toString()
        }
    }
}
