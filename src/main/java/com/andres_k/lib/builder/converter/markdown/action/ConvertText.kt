package com.andres_k.lib.builder.converter.markdown.action

import com.andres_k.lib.builder.converter.PdfConverterConfig
import com.andres_k.lib.builder.converter.markdown.context.MarkdownConverterConfig
import com.andres_k.lib.builder.converter.markdown.context.MarkdownConverterContext
import com.andres_k.lib.library.core.component.PdfComponent
import com.andres_k.lib.library.core.component.element.PdfText
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode

/**
 * Created on 2020/11/02.
 *
 * @author Kevin Andres
 */
object ConvertText : MarkdownAction {

    override fun run(
        node: ASTNode,
        nodeIndex: Int,
        parent: ASTNode,
        config: PdfConverterConfig,
        markdown: MarkdownConverterConfig,
        context: MarkdownConverterContext,
    ): PdfText {
        return extractText(node, markdown, config)
    }

    fun extractText(node: ASTNode, markdown: MarkdownConverterConfig, config: PdfConverterConfig): PdfText {
        val margin = markdown.margin(node.type)
        val text = node.getTextInNode(config.data).toString()
        val customInterpreter = config.customInterpreter[PdfComponent.Type.TEXT]

        val customOutput = if (customInterpreter == null || !customInterpreter.isInterpreterValue(text)) {
            null
        } else {
            val output = customInterpreter.interpret(text)
            if (output is PdfText) output else null
        }

        return customOutput ?: PdfText(
            text = text,
            font = config.getDefaultFont(),
            fontSize = config.defaultFontSize,
            margin = margin
        )
    }
}
