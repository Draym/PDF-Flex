package com.andres_k.lib.builder.converter.utils

/**
 * Created on 2020/11/03.
 *
 * @author Kevin Andres
 */
object RegexUtil {

    fun buildSplitWithDelim(vararg delimiters: String): Regex {
        val condition = delimiters.map {
            "(?<=\\$it)"
        }
        val reg = condition.joinToString("|") { it }
        return Regex(reg)
    }
}
