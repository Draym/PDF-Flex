package com.andres_k.lib.builder.converter.markdown.action

import com.andres_k.lib.builder.converter.PdfConverterConfig
import com.andres_k.lib.builder.converter.markdown.context.MarkdownConverterConfig
import com.andres_k.lib.builder.converter.markdown.context.MarkdownConverterContext
import com.andres_k.lib.library.core.component.custom.PdfTextLine
import com.andres_k.lib.library.core.component.custom.addText
import com.andres_k.lib.library.core.component.element.PdfParagraph
import com.andres_k.lib.library.core.component.element.PdfText
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode

/**
 * Created on 2020/11/02.
 *
 * @author Kevin Andres
 */
object ConvertParagraph : MarkdownAction {

    override fun run(
        node: ASTNode,
        nodeIndex: Int,
        parent: ASTNode,
        config: PdfConverterConfig,
        markdown: MarkdownConverterConfig,
        context: MarkdownConverterContext,
    ): PdfParagraph {
        val lines: MutableList<PdfTextLine> = mutableListOf(PdfTextLine.EMPTY)
        var index = 0

        node.children.forEachIndexed { childIndex, child ->
            when (child.type) {
                MarkdownTokenTypes.TEXT -> {
                    val text = ConvertText.extractText(child, config)
                    lines.addText(index, text, text.margin.bottom)
                }
                MarkdownTokenTypes.WHITE_SPACE -> {
                    lines.addText(index, PdfText(" ", config.getDefaultFont(), config.defaultFontSize))
                }
                MarkdownElementTypes.EMPH -> {
                    val item = markdown.action(MarkdownElementTypes.EMPH).run(child, childIndex, node, config, markdown, context)
                    if (item != null) {
                        val text = (item as PdfText).text.lines()
                        lines.addText(index, PdfText(text.first(), item.font, item.fontSize))
                        (text.subList(1, text.size)).forEach { lines.add(PdfTextLine(PdfText(it, item.font, item.fontSize))) }
                        index += text.size - 1
                    }
                }
                MarkdownElementTypes.STRONG -> {
                    val item = markdown.action(MarkdownElementTypes.STRONG).run(child, childIndex, node, config, markdown, context)
                    if (item != null) {
                        val text = (item as PdfText).text.lines()
                        lines.addText(index, PdfText(text.first(), item.font, item.fontSize))
                        (text.subList(1, text.size)).forEach { lines.add(PdfTextLine(PdfText(it, item.font, item.fontSize))) }
                        index += text.size - 1
                    }
                }
                MarkdownTokenTypes.EOL -> {
                    lines.add(PdfTextLine.EMPTY)
                    index++
                }
                MarkdownTokenTypes.HTML_TAG -> {
                    if (child.getTextInNode(config.data).toString() == HTMLSupport.LINE_BREAK.code) {
                        lines.add(PdfTextLine.EMPTY)
                        index++
                    }
                }
            }
        }

        return PdfParagraph(
            lines = lines,
            interLine = config.defaultInterline
        )
    }
}
