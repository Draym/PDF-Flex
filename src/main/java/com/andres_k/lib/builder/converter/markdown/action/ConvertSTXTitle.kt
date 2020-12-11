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

/**
 * Created on 2020/11/02.
 *
 * @author Kevin Andres
 */
object ConvertSTXTitle : MarkdownAction {

    override fun run(
        node: ASTNode,
        nodeIndex: Int,
        parent: ASTNode,
        config: PdfConverterConfig,
        markdown: MarkdownConverterConfig,
        context: MarkdownConverterContext,
    ): PdfText {
        val font = markdown.font(node.type)
        val contentText = node.findChildOfType(MarkdownTokenTypes.SETEXT_CONTENT)?.findChildOfType(MarkdownTokenTypes.TEXT)

        return if (contentText != null) {
            ConvertText.extractText(contentText, config)
        } else {
            PdfText(text = "")
        }.copy(
            font = font.first.code,
            fontSize = font.second,
            borders = Borders.BOTTOM(thickness = if (node.type == MarkdownTokenTypes.SETEXT_1) 3f else 1.5f),
            margin = Spacing(bottom = 10f)
        )
    }
}
