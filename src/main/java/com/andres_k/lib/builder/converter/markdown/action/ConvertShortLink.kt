package com.andres_k.lib.builder.converter.markdown.action

import com.andres_k.lib.builder.converter.PdfConverterConfig
import com.andres_k.lib.builder.converter.markdown.context.MarkdownConverterConfig
import com.andres_k.lib.builder.converter.markdown.context.MarkdownConverterContext
import com.andres_k.lib.library.core.component.PdfComponent
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.findChildOfType

/**
 * Created on 2021/02/09.
 *
 * @author Kevin Andres
 */
object ConvertShortLink : MarkdownAction {
    override fun run(
        node: ASTNode,
        nodeIndex: Int,
        parent: ASTNode,
        config: PdfConverterConfig,
        markdown: MarkdownConverterConfig,
        context: MarkdownConverterContext
    ): PdfComponent? {
        return node.findChildOfType(MarkdownElementTypes.LINK_LABEL)?.let { linkLabel ->
            linkLabel.findChildOfType(MarkdownTokenTypes.TEXT)?.let { text ->
                ConvertText.extractText(text, markdown, config)
            }
        }
    }
}
