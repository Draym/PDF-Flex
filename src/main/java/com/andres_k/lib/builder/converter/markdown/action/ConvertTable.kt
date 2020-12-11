package com.andres_k.lib.builder.converter.markdown.action

import com.andres_k.lib.builder.converter.PdfConverterConfig
import com.andres_k.lib.builder.converter.markdown.context.MarkdownConverterConfig
import com.andres_k.lib.builder.converter.markdown.context.MarkdownConverterContext
import com.andres_k.lib.library.core.component.container.PdfCol
import com.andres_k.lib.library.core.component.element.PdfTable
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

    private fun getCols(row: ASTNode, config: PdfConverterConfig): List<PdfCol> {
        return row.children.mapIndexedNotNull { index, child ->
            if (child.type == GFMTokenTypes.CELL) {
                val text = ConvertText.extractText(child, config)
                PdfCol(content = text)
            } else null
        }
    }

    private fun getHeader(table: ASTNode, config: PdfConverterConfig): List<PdfCol> {
        val header = table.findChildOfType(GFMElementTypes.HEADER)

        return if (header != null) {
            getCols(header, config)
        } else emptyList()
    }

    private fun getRows(table: ASTNode, config: PdfConverterConfig): List<List<PdfCol>> {
        return table.children.mapNotNull { child ->
            if (child.type == GFMElementTypes.ROW) {
                getCols(child, config)
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
        val header = getHeader(node, config)
        val rows = getRows(node, config)

        return PdfTable(
            header = header,
            rows = rows
        )
    }
}
