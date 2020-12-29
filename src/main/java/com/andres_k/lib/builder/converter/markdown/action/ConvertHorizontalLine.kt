package com.andres_k.lib.builder.converter.markdown.action

import com.andres_k.lib.builder.converter.PdfConverterConfig
import com.andres_k.lib.builder.converter.markdown.context.MarkdownConverterConfig
import com.andres_k.lib.builder.converter.markdown.context.MarkdownConverterContext
import com.andres_k.lib.library.core.component.container.PdfRow
import com.andres_k.lib.library.core.property.Borders
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode

/**
 * Created on 2020/11/02.
 *
 * @author Kevin Andres
 */
object ConvertHorizontalLine : MarkdownAction {

    override fun run(
        node: ASTNode,
        nodeIndex: Int,
        parent: ASTNode,
        config: PdfConverterConfig,
        markdown: MarkdownConverterConfig,
        context: MarkdownConverterContext,
    ): PdfRow? {
        val margin = markdown.margin(node.type)
        val padding = markdown.padding(node.type)

        val previous = if (nodeIndex != 0) parent.children[nodeIndex - 1] else null
        return if (previous != null && previous.type == MarkdownTokenTypes.EOL) {
            PdfRow(
                elements = emptyList(),
                borders = Borders.BOTTOM(),
                padding = padding.merge(top = config.defaultFontSize ?: 12f),
                margin = margin.merge(bottom = config.defaultFontSize ?: 12f)
            )
        } else null
    }
}
