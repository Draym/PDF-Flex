package com.andres_k.lib.builder.converter.markdown.action

import com.andres_k.lib.builder.converter.PdfConverterConfig
import com.andres_k.lib.builder.converter.markdown.context.MarkdownConverterConfig
import com.andres_k.lib.builder.converter.markdown.context.MarkdownConverterContext
import com.andres_k.lib.library.core.component.PdfComponent
import com.andres_k.lib.library.core.component.container.PdfRow
import com.andres_k.lib.library.core.property.Spacing
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode

/**
 * Created on 2020/11/05.
 *
 * @author Kevin Andres
 */
object ConvertHtmlTag : MarkdownAction {
    override fun run(node: ASTNode, nodeIndex: Int, parent: ASTNode, config: PdfConverterConfig, markdown: MarkdownConverterConfig, context: MarkdownConverterContext): PdfComponent? {

        return when (node.getTextInNode(config.data).toString()) {
            HTMLSupport.BR.code -> {
                PdfRow(
                    elements = emptyList(),
                    padding = Spacing(top = config.defaultFontSize ?: 12f)
                )
            }
            else -> {
                null
            }
        }
    }
}
