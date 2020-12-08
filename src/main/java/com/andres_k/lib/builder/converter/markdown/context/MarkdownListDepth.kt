package com.andres_k.lib.builder.converter.markdown.context

import org.intellij.markdown.IElementType

/**
 * Created on 2020/11/02.
 *
 * @author Kevin Andres
 */

/**
 * Used by the Markdown list node to calculate at which level a children is within a list of list
 *
 * example :
 *  • level 1
 *      ◦ level 2
 *          ⁃ level 3
 *  • level 1
 */
data class MarkdownListDepth(
    val level: Int,
    val type: IElementType
)
