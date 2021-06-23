package com.andres_k.lib.builder.converter.markdown.action

import com.andres_k.lib.builder.converter.PdfConverterConfig
import com.andres_k.lib.builder.converter.markdown.context.MarkdownConverterConfig
import com.andres_k.lib.builder.converter.markdown.context.MarkdownConverterContext
import com.andres_k.lib.library.core.component.container.PdfCol
import com.andres_k.lib.library.core.component.custom.PdfTextLine
import com.andres_k.lib.library.core.component.element.PdfParagraph
import com.andres_k.lib.library.core.component.element.PdfTable
import com.andres_k.lib.library.core.property.SizeAttr
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.findChildOfType
import org.intellij.markdown.flavours.gfm.GFMElementTypes
import org.intellij.markdown.flavours.gfm.GFMTokenTypes

/**
 * Created on 2020/11/02.
 *
 * @author Kevin Andres
 */
object ConvertTable : MarkdownAction {

    private fun getCols(row: ASTNode, markdown: MarkdownConverterConfig, config: PdfConverterConfig): List<PdfCol> {
        val padding = markdown.padding(GFMTokenTypes.CELL)
        val nbCols = row.children.count { it.type == GFMTokenTypes.CELL }
        val colSize = 100f / nbCols

        return row.children.mapIndexedNotNull { index, child ->
            if (child.type == GFMTokenTypes.CELL) {
                val text = PdfParagraph(listOf(PdfTextLine(ConvertText.extractText(child, markdown, config))))
                PdfCol(content = text, padding = padding, maxWidth = SizeAttr.percent(colSize))
            } else null
        }
    }

    private fun getHeader(table: ASTNode, markdown: MarkdownConverterConfig, config: PdfConverterConfig): List<PdfCol> {
        val header = table.findChildOfType(GFMElementTypes.HEADER)

        return if (header != null) {
            getCols(header, markdown, config)
        } else emptyList()
    }

    private fun getRows(table: ASTNode, markdown: MarkdownConverterConfig, config: PdfConverterConfig): List<List<PdfCol>> {
        return table.children.mapNotNull { child ->
            if (child.type == GFMElementTypes.ROW) {
                getCols(child, markdown, config)
            } else null
        }
    }

    override fun run(
        node: ASTNode,
        nodeIndex: Int,
        parent: ASTNode,
        config: PdfConverterConfig,
        markdown: MarkdownConverterConfig,
        context: MarkdownConverterContext,
    ): PdfTable {
        val margin = markdown.margin(node.type)
        val padding = markdown.padding(node.type)
        val align = markdown.align(node.type)
        val header = getHeader(node, markdown, config)
        val rows = getRows(node, markdown, config)

        return PdfTable(
            header = header,
            rows = rows,
            margin = margin,
            padding = padding,
            bodyAlign = align,
            maxWidth = SizeAttr.full()
        )
    }
}
