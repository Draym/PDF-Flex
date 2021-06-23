package com.andres_k.lib.builder.converter.markdown

import com.andres_k.lib.builder.converter.PdfConverterConfig
import com.andres_k.lib.builder.converter.markdown.action.*
import com.andres_k.lib.builder.converter.markdown.context.MarkdownConverterConfig
import com.andres_k.lib.builder.converter.markdown.context.MarkdownConverterContext
import com.andres_k.lib.library.core.component.PdfComponent
import com.andres_k.lib.library.core.component.container.PdfCol
import com.andres_k.lib.library.core.component.container.PdfRow
import com.andres_k.lib.library.core.property.BodyAlign
import com.andres_k.lib.library.core.property.SizeAttr
import com.andres_k.lib.library.core.property.Spacing
import com.andres_k.lib.library.utils.BaseFont
import com.andres_k.lib.library.utils.EFont
import com.andres_k.lib.library.utils.FontSize
import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.flavours.MarkdownFlavourDescriptor
import org.intellij.markdown.flavours.gfm.GFMElementTypes
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser

/**
 * Created on 2020/11/02.
 *
 * @author Kevin Andres
 */
object MarkdownConverter {

    /**
     * Define default attributes for specific ASTNode type
     */
    object Default {
        val font: Map<IElementType, Pair<EFont, FontSize>> = mapOf(
            MarkdownElementTypes.SETEXT_1 to (BaseFont.BOLD to 32f),
            MarkdownElementTypes.SETEXT_2 to (BaseFont.BOLD to 24f),
            MarkdownElementTypes.ATX_1 to (BaseFont.BOLD to 32f),
            MarkdownElementTypes.ATX_2 to (BaseFont.BOLD to 24f),
            MarkdownElementTypes.ATX_3 to (BaseFont.BOLD to 18.72f),
            MarkdownElementTypes.ATX_4 to (BaseFont.BOLD to 16f),
            MarkdownElementTypes.ATX_5 to (BaseFont.BOLD to 13.28f),
            MarkdownElementTypes.ATX_6 to (BaseFont.BOLD to 10.72f)
        )

        /**
         * Use a specific impl to read a Node by its type
         */
        val action: Map<IElementType, MarkdownAction> = mapOf(
            MarkdownElementTypes.PARAGRAPH to ConvertParagraph,
            MarkdownTokenTypes.TEXT to ConvertText,
            MarkdownElementTypes.STRONG to ConvertBold,
            MarkdownElementTypes.EMPH to ConvertItalic,
            MarkdownTokenTypes.EMPH to IgnoreAction,
            GFMElementTypes.TABLE to ConvertTable,
            MarkdownTokenTypes.HORIZONTAL_RULE to ConvertHorizontalLine,
            MarkdownElementTypes.ORDERED_LIST to ConvertList,
            MarkdownElementTypes.UNORDERED_LIST to ConvertList,
            MarkdownElementTypes.LIST_ITEM to ConvertListItem,
            MarkdownTokenTypes.LIST_NUMBER to IgnoreAction,
            MarkdownTokenTypes.LIST_BULLET to IgnoreAction,
            MarkdownElementTypes.SETEXT_1 to ConvertSTXTitle,
            MarkdownElementTypes.SETEXT_2 to ConvertSTXTitle,
            MarkdownElementTypes.ATX_1 to ConvertATXTitle,
            MarkdownElementTypes.ATX_2 to ConvertATXTitle,
            MarkdownElementTypes.ATX_3 to ConvertATXTitle,
            MarkdownElementTypes.ATX_4 to ConvertATXTitle,
            MarkdownElementTypes.ATX_5 to ConvertATXTitle,
            MarkdownElementTypes.ATX_6 to ConvertATXTitle,
            MarkdownElementTypes.INLINE_LINK to ConvertLink,
            MarkdownElementTypes.SHORT_REFERENCE_LINK to ConvertShortLink,
            MarkdownTokenTypes.HTML_TAG to ConvertHtmlTag,
            MarkdownElementTypes.HTML_BLOCK to ConvertHtmlBlock,
            MarkdownTokenTypes.EOL to ConvertEOL,
            MarkdownTokenTypes.WHITE_SPACE to IgnoreAction,
        )

        /**
         * Define default margin for specific ASTNode type
         */
        val margin: Map<IElementType, Spacing> = mapOf(
            MarkdownElementTypes.LIST_ITEM to Spacing(bottom = 6f)
        )

        /**
         * Define default padding for specific ASTNode type
         */
        val padding: Map<IElementType, Spacing> = mapOf()

        /**
         * Define default position for specific ASTNode type
         */
        val align: Map<IElementType, BodyAlign> = mapOf()
    }

    /**
     *
     * Explore a node and create a PdfRow for each of its children
     *
     * @param node current node in the tree to explore
     * @param config global config used by the converter. It is required to define available PDF Font
     * @param context it is used internally, if used combined with buildNodeTree, simply set context as MarkdownConverterContext.NEW
     */
    fun analyseNodeChildren(
        node: ASTNode,
        config: PdfConverterConfig,
        markdown: MarkdownConverterConfig,
        context: MarkdownConverterContext,
    ): List<PdfComponent> {
        return node.children.mapIndexedNotNull { index, child ->
            val action = markdown.action(child.type)
            val content = action.run(child, index, node, config, markdown, context)

            when {
                content == null -> {
                    null
                }
                content.type == PdfComponent.Type.PAGE_BREAK -> content
                else -> {
                    PdfRow(
                        elements = listOf(
                            PdfCol(
                                content = content,
                                maxWidth = SizeAttr.full()
                            )
                        )
                    )
                }
            }
        }
    }

    /**
     * Read data using IntelliJ.Markdown parser and generate a Node tree
     *
     * Fun is public to allow possible customisation. Use markdownToPDFlex instead for the base impl of parsing a text to PDF component
     *
     * @param data text using Markdown syntax
     * @param customDescriptor rules to parse the text, custom Descriptor can be created (see intellij.markdown.flavours documentation). Default is GFM.
     * @return Node tree
     */
    fun buildNodeTree(data: String, customDescriptor: MarkdownFlavourDescriptor? = null): ASTNode {
        val descriptor = customDescriptor ?: GFMFlavourDescriptor()
        return MarkdownParser(descriptor).buildMarkdownTreeFromString(data)
    }

    /**
     * Transform a Markdown text into PDFlex rows
     *  - analyse the text and create a Node tree
     *  - read the node tree and create PDFlex component for each Markdown tag
     *
     * @param text text formatted with Markdown syntax
     * @param descriptor rules to parse the text, custom Descriptor can be created (see intellij.markdown.flavours documentation). Default is GFM.
     * @return a list of PdfRow which can be inserted into a PdfPage and print as Pdf using PDFlex (see MarkdownToPDF class as example)
     */
    fun markdownToPDFlex(
        text: String,
        descriptor: MarkdownFlavourDescriptor? = null,
        config: PdfConverterConfig,
        markdown: MarkdownConverterConfig = MarkdownConverterConfig.DEFAULT,
    ): List<PdfComponent> {
        val node = buildNodeTree(text, descriptor)

        return analyseNodeChildren(
            node = node,
            config = config,
            markdown = markdown,
            context = MarkdownConverterContext.NEW
        )
    }

    /**
     * Utility function to stringify a Node
     * helpful for logs
     */
    fun stringifyNode(text: String, node: ASTNode, level: Int = 0): String {
        val builder = StringBuilder()

        builder.append("|")
        for (i in 0 until level) { builder.append("_") }
        builder.append("Node[${node.type}]: ${node.getTextInNode(text)}")
        if (node.children.isNotEmpty()) {
            builder.appendLine()
            builder.append("|")
            for (i in 0 until level) { builder.append("_") }
            builder.append("children:")
        }
        node.children.forEach { child ->
            builder.appendLine()
            builder.append(stringifyNode(text, child, level + 2))
        }
        return builder.toString()
    }
}
