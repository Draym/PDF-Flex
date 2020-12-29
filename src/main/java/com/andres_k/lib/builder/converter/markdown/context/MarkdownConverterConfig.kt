package com.andres_k.lib.builder.converter.markdown.context

import com.andres_k.lib.builder.converter.markdown.MarkdownConverter
import com.andres_k.lib.builder.converter.markdown.action.IgnoreAction
import com.andres_k.lib.builder.converter.markdown.action.MarkdownAction
import com.andres_k.lib.library.core.property.Spacing
import com.andres_k.lib.library.utils.EFont
import com.andres_k.lib.library.utils.FontSize
import org.intellij.markdown.IElementType
import java.util.*

/**
 * Created on 2020/11/04.
 *
 * @author Kevin Andres
 */
data class MarkdownConverterConfig(
    private val action: Map<IElementType, MarkdownAction>,
    private val font: Map<IElementType, Pair<EFont, FontSize>>,
    private val margin: Map<IElementType, Spacing>,
    private val padding: Map<IElementType, Spacing>,
    private val ignoreMissing: Boolean = false,
) {

    fun action(type: IElementType): MarkdownAction {
        val result = action[type]
        if (result == null) {
            if (ignoreMissing) {
                return IgnoreAction
            }
            throw NotImplementedError("PDFlex [config] Converter for Markdown[${type}] is not implemented.")
        }
        return result
    }

    fun font(type: IElementType): Pair<EFont, FontSize> {
        val result = font[type]
        if (result == null) {
            if (ignoreMissing) {
                return font.entries.firstOrNull()?.value
                    ?: throw MissingFormatArgumentException("PDFlex [config] trying to get a font, but none are available.")
            }
            throw IllegalArgumentException("PDFlex [config] no font defined for [${type}]")
        }
        return result
    }

    fun margin(type: IElementType): Spacing {
        return margin[type] ?: Spacing.NONE
    }

    fun padding(type: IElementType): Spacing {
        return padding[type] ?: Spacing.NONE
    }

    companion object {
        val DEFAULT = MarkdownConverterConfig(
            action = MarkdownConverter.Default.action,
            font = MarkdownConverter.Default.font,
            margin = MarkdownConverter.Default.margin,
            padding = MarkdownConverter.Default.padding
        )
    }
}
