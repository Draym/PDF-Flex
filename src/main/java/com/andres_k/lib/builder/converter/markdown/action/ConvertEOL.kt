package com.andres_k.lib.builder.converter.markdown.action

import com.andres_k.lib.builder.converter.PdfConverterConfig
import com.andres_k.lib.builder.converter.markdown.context.MarkdownConverterConfig
import com.andres_k.lib.builder.converter.markdown.context.MarkdownConverterContext
import com.andres_k.lib.library.core.component.element.PdfText
import com.andres_k.lib.library.core.property.Spacing
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode

/**
 * Created on 2020/11/02.
 *
 * @author Kevin Andres
 */
object ConvertEOL : MarkdownAction {

    override fun run(
        node: ASTNode,
        nodeIndex: Int,
        parent: ASTNode,
        config: PdfConverterConfig,
        markdown: MarkdownConverterConfig,
        context: MarkdownConverterContext,
    ): PdfText? {
        val previous = if (nodeIndex != 0) parent.children[nodeIndex - 1] else null
        return if (previous != null && previous.type == MarkdownTokenTypes.EOL) {
            PdfText(
                text = "",
                margin = Spacing(top = config.defaultFontSize ?: 12f)
            )
        } else null
    }
}
