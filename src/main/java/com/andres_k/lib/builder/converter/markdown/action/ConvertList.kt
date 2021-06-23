package com.andres_k.lib.builder.converter.markdown.action

import com.andres_k.lib.builder.converter.PdfConverterConfig
import com.andres_k.lib.builder.converter.markdown.MarkdownConverter
import com.andres_k.lib.builder.converter.markdown.context.MarkdownConverterConfig
import com.andres_k.lib.builder.converter.markdown.context.MarkdownConverterContext
import com.andres_k.lib.library.core.component.container.PdfView
import com.andres_k.lib.library.core.property.Size
import org.intellij.markdown.ast.ASTNode

/**
 * Created on 2020/11/02.
 *
 * @author Kevin Andres
 */
object ConvertList : MarkdownAction {
    override fun run(
        node: ASTNode,
        nodeIndex: Int,
        parent: ASTNode,
        config: PdfConverterConfig,
        markdown: MarkdownConverterConfig,
        context: MarkdownConverterContext,
    ): PdfView {
        val margin = markdown.margin(node.type)
        val padding = markdown.padding(node.type)
        val align = markdown.align(node.type)

        val rows = MarkdownConverter.analyseNodeChildren(node, config, markdown, context)
        return PdfView(
            elements = rows,
            margin = margin,
            padding = padding.merge(left = config.defaultPadding),
            size = Size.FULL,
            bodyAlign = align
        )
    }
}
