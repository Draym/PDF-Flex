package com.andres_k.lib.builder.converter.markdown.context

import org.intellij.markdown.IElementType

/**
 * Created on 2020/11/02.
 *
 * @author Kevin Andres
 */

/**
 * The Context is used to pack-up some information of the fly while reading the Node tree
 * the number of attribute may be extended to fill futures needs
 *
 * - listDepth: remember the depth level of a child within a list of list
 */
data class MarkdownConverterContext(
    val listDepth: MarkdownListDepth?
) {
    fun getListDepth(type: IElementType): Int {
        if (listDepth != null && listDepth.type == type) {
            return listDepth.level
        }
        return 1
    }

    companion object {
        val NEW = MarkdownConverterContext(null)
    }
}
