package com.andres_k.lib.builder.converter.markdown.action

import com.andres_k.lib.builder.converter.PdfConverterConfig
import com.andres_k.lib.builder.converter.markdown.context.MarkdownConverterConfig
import com.andres_k.lib.builder.converter.markdown.context.MarkdownConverterContext
import com.andres_k.lib.library.core.component.custom.PdfTextLine
import com.andres_k.lib.library.core.component.element.PdfParagraph
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode

/**
 * Created on 2020/11/02.
 *
 * @author Kevin Andres
 */
object ConvertATXTitle : MarkdownAction {

    override fun run(
        node: ASTNode,
        nodeIndex: Int,
        parent: ASTNode,
        config: PdfConverterConfig,
        markdown: MarkdownConverterConfig,
        context: MarkdownConverterContext,
    ): PdfParagraph {
        val font = markdown.font(node.type)

        val text = node.children.mapIndexedNotNull { childIndex, child ->
            if (child.type == MarkdownTokenTypes.ATX_CONTENT) {
                val paragraph = ConvertParagraph.run(child, childIndex, node, config, markdown, context)
                paragraph.lines.map { line ->
                    PdfTextLine(line.items.map { text -> text.copy(font = font.first.code, fontSize = font.second) })
                }
            } else null
        }.flatten()

        return PdfParagraph(
            lines = text,
            interLine = config.defaultInterline
        )
    }
}
