package com.andres_k.lib.builder.converter.markdown.action

import com.andres_k.lib.builder.converter.PdfConverterConfig
import com.andres_k.lib.builder.converter.markdown.context.MarkdownConverterConfig
import com.andres_k.lib.builder.converter.markdown.context.MarkdownConverterContext
import com.andres_k.lib.library.core.component.element.PdfText
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode

/**
 * Created on 2020/11/04.
 *
 * @author Kevin Andres
 */
object ConvertItalic : MarkdownAction {
    override fun run(
        node: ASTNode,
        nodeIndex: Int,
        parent: ASTNode,
        config: PdfConverterConfig,
        markdown: MarkdownConverterConfig,
        context: MarkdownConverterContext,
    ): PdfText {
        val fontItalic = config.getDefaultItalicFont()
        var text = ""
        node.children.forEach { child ->
            if (child.type != MarkdownTokenTypes.EMPH) {
                text += child.getTextInNode(config.data)
            }
        }
        return PdfText(text, fontItalic, config.defaultFontSize)
    }
}
