package com.andres_k.lib.library.utils

import org.apache.fontbox.ttf.TrueTypeCollection
import org.apache.fontbox.ttf.TrueTypeFont
import org.apache.pdfbox.pdmodel.font.PDFont
import java.lang.Character.UnicodeBlock

/**
 * Created on 2020/07/20.
 *
 * @author Kevin Andres
 */
object FontUtils {
    /**
     * Load specified TrueTypeCollection as TrueTypeFont
     *
     * @param fontPath path of the font from the resources folder of your application
     * @param fontName is required because TTC are stored locally as font collection
     */
    fun loadTTCFont(
        fontPath: String,
        fontName: String,
    ): TrueTypeFont {
        val stream = FontUtils::class.java.getResourceAsStream(fontPath)
            ?: throw Exception("File [$fontPath] not found")

        val trueTypeCollection = TrueTypeCollection(stream)

        return trueTypeCollection.getFontByName(fontName)
    }

    fun getTextWidth(
        text: String,
        font: PDFont,
        fontSize: Float
    ): Float {
        return font.getStringWidth(text) / 1000 * fontSize
    }

    fun getTextHeight(
        font: PDFont,
        fontSize: Float
    ): Float {
        return font.fontDescriptor.fontBoundingBox.height / 1000 * fontSize
    }
}

interface EFont {
    val code: FontCode
}

enum class BaseFont(override val code: FontCode) : EFont {
    DEFAULT("DEFAULT"),
    BOLD("BOLD"),
    ITALIC("ITALIC")
}

data class Font(val code: FontCode, val font: PDFont) {
    fun toPair(): Pair<FontCode, PDFont> {
        return code to font
    }
}

typealias FontCode = String
typealias FontSize = Float

/**
 * Check if the text contains Ideographs (China, Japan, Korea)
 */
fun String.hasCJK(): Boolean {
    return this.any {
        val block = UnicodeBlock.of(it)

        UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS == block
            || UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS == block
            || UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A == block
    }
}
