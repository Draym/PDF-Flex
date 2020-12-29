package com.andres_k.lib.builder.converter.markdown.action

import com.andres_k.lib.builder.converter.PdfConverterConfig
import com.andres_k.lib.builder.converter.markdown.MarkdownConverter
import com.andres_k.lib.builder.converter.markdown.context.MarkdownConverterConfig
import com.andres_k.lib.builder.converter.markdown.context.MarkdownConverterContext
import com.andres_k.lib.builder.converter.markdown.context.MarkdownListDepth
import com.andres_k.lib.library.core.component.container.PdfCol
import com.andres_k.lib.library.core.component.container.PdfRow
import com.andres_k.lib.library.core.component.container.PdfView
import com.andres_k.lib.library.core.component.element.PdfText
import com.andres_k.lib.library.core.property.Size
import com.andres_k.lib.library.core.property.SizeAttr
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.findChildOfType
import org.intellij.markdown.ast.getTextInNode

/**
 * Created on 2020/11/02.
 *
 * @author Kevin Andres
 */
object ConvertListItem : MarkdownAction {

    override fun run(
        node: ASTNode,
        nodeIndex: Int,
        parent: ASTNode,
        config: PdfConverterConfig,
        markdown: MarkdownConverterConfig,
        context: MarkdownConverterContext,
    ): PdfRow {
        val margin = markdown.margin(node.type)
        val padding = markdown.padding(node.type)

        var listType = MarkdownTokenTypes.LIST_NUMBER
        var listItem = node.findChildOfType(MarkdownTokenTypes.LIST_NUMBER)

        val step = if (listItem != null) {
            val index = listItem.getTextInNode(config.data).filter { it.isDigit() }.toString().toInt()
            config.defaultOrderedList.get(index)
        } else {
            listItem = node.findChildOfType(MarkdownTokenTypes.LIST_BULLET)

            if (listItem == null) {
                throw IllegalArgumentException("PDFlex - ListItem require an {item number} / {bullet}")
            }
            listType = MarkdownTokenTypes.LIST_BULLET
            config.defaultUnorderedList.get(context.getListDepth(MarkdownTokenTypes.LIST_BULLET))
        }

        val rows = MarkdownConverter.analyseNodeChildren(
            node = node,
            config = config,
            markdown = markdown,
            context = context.copy(listDepth = MarkdownListDepth(context.getListDepth(listType) + 1, listType))
        )

        return PdfRow(
            elements = listOf(
                PdfCol(
                    content = PdfText(
                        text = "$step ",
                        font = config.getDefaultFont(),
                        fontSize = config.defaultFontSize
                    )
                ),
                PdfCol(
                    content = PdfView(
                        elements = rows,
                        size = Size.FULL
                    ),
                    maxWidth = SizeAttr.percent(97f)
                )
            ),
            margin = margin,
            padding = padding
        )
    }
}
