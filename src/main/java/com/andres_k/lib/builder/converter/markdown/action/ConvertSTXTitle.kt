package com.andres_k.lib.builder.converter.markdown.action

import com.andres_k.lib.builder.converter.PdfConverterConfig
import com.andres_k.lib.builder.converter.markdown.context.MarkdownConverterConfig
import com.andres_k.lib.builder.converter.markdown.context.MarkdownConverterContext
import com.andres_k.lib.library.core.component.element.PdfText
import com.andres_k.lib.library.core.property.Borders
import com.andres_k.lib.library.core.property.Spacing
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.findChildOfType
import org.intellij.markdown.ast.getTextInNode

/**
 * Created on 2020/11/02.
 *
 * @author Kevin Andres
 */
object ConvertSTXTitle : MarkdownAction {

    fun getATXContent(
        content: ASTNode?,
        config: PdfConverterConfig,
    ): String? {
        val text = content?.findChildOfType(MarkdownTokenTypes.TEXT)
        return text?.getTextInNode(config.data)?.toString()
    }

    override fun run(
        node: ASTNode,
        nodeIndex: Int,
        parent: ASTNode,
        config: PdfConverterConfig,
        markdown: MarkdownConverterConfig,
        context: MarkdownConverterContext,
    ): PdfText {
        val font = markdown.font(node.type)
        val content = node.findChildOfType(MarkdownTokenTypes.SETEXT_CONTENT)

        val text = getATXContent(content, config) ?: ""
        return PdfText(
            text = text,
            font = font.first.code,
            fontSize = font.second,
            borders = Borders.BOTTOM(thickness = if (node.type == MarkdownTokenTypes.SETEXT_1) 3f else 1.5f),
            margin = Spacing(bottom = 10f)
        )
    }
}
