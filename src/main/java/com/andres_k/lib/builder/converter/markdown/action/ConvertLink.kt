package com.andres_k.lib.builder.converter.markdown.action

import com.andres_k.lib.builder.converter.PdfConverterConfig
import com.andres_k.lib.builder.converter.markdown.context.MarkdownConverterConfig
import com.andres_k.lib.builder.converter.markdown.context.MarkdownConverterContext
import com.andres_k.lib.library.core.component.PdfComponent
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.findChildOfType

/**
 * Created on 2021/02/09.
 *
 * @author Kevin Andres
 */
object ConvertLink: MarkdownAction {
    override fun run(
        node: ASTNode,
        nodeIndex: Int,
        parent: ASTNode,
        config: PdfConverterConfig,
        markdown: MarkdownConverterConfig,
        context: MarkdownConverterContext
    ): PdfComponent? {
        // TODO create LINK element with PDFBox: https://stackoverflow.com/questions/50974511/add-hyperlink-to-pdf-files-using-pdfbox/50975462
        return node.findChildOfType(MarkdownElementTypes.INLINE_LINK)?.let { linkLabel ->
            linkLabel.findChildOfType(MarkdownElementTypes.LINK_TEXT)?.let { linkText ->
                linkLabel.findChildOfType(MarkdownTokenTypes.TEXT)?.let { text ->
                    ConvertText.extractText(text, markdown, config)
                }
            }
        }
    }
}
