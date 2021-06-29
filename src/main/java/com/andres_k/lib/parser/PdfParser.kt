package com.andres_k.lib.parser

import org.apache.pdfbox.contentstream.operator.color.*
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.text.PDFTextStripper
import org.apache.pdfbox.text.TextPosition
import org.apache.pdfbox.util.Matrix
import java.awt.Color


/**
 * Created on 2020/12/10.
 *
 * @author Kevin Andres
 */
class PdfParser(document: PDDocument) {
    private val parser = CustomParser()

    init {
        parser.getText(document)
    }

    fun debug() {
        parser.getPages().forEach { page ->
            println("----")
            println("PAGE n.${page.index}")
            page.lines.forEach { line ->
                println("LINE [${line.index}]")
                line.sentences.forEach { sentence ->
                    println("$sentence")
                }
            }
        }
    }

    /**
     * Search a text into a PDF
     * @return the position information of the first character of [text]
     */
    fun search(text: String): ElementPositionResult? {
        parser.getPages().forEach { page ->
            page.lines.forEach { line ->
                line.sentences.forEach { sentence ->
                    val result = sentence.text.indexOf(text)

                    if (result != -1 && result < sentence.characters.size) {
                        val firstCharacter = sentence.characters[result]

                        return ElementPositionResult(
                            page = page.index,
                            line = line.index,
                            x = firstCharacter.x,
                            y = firstCharacter.y,
                            width = firstCharacter.width,
                            height = firstCharacter.height,
                            color = firstCharacter.color,
                            pageWidth = page.pageWidth,
                            pageHeight = page.pageHeight
                        )
                    }
                }
            }
        }
        return null
    }

    private class CustomParser : PDFTextStripper() {
        private val pages: MutableList<Page> = mutableListOf()
        private var currentPage = 0
        private var currentLine = 0
        private var currentSentence = 0
        private lateinit var previous: TextPosition

        init {
            addOperator(SetStrokingColorSpace())
            addOperator(SetNonStrokingColorSpace())
            addOperator(SetStrokingDeviceCMYKColor())
            addOperator(SetNonStrokingDeviceCMYKColor())
            addOperator(SetNonStrokingDeviceRGBColor())
            addOperator(SetStrokingDeviceRGBColor())
            addOperator(SetNonStrokingDeviceGrayColor())
            addOperator(SetStrokingDeviceGrayColor())
            addOperator(SetStrokingColor())
            addOperator(SetStrokingColorN())
            addOperator(SetNonStrokingColor())
            addOperator(SetNonStrokingColorN())
        }

        override fun processTextPosition(text: TextPosition?) {

            if (text != null) {
                val color = Color(graphicsState.nonStrokingColor.toRGB())

                if (this::previous.isInitialized) {
                    if (previous.y > text.y) {
                        ++currentPage
                    } else if (previous.y < text.y) {
                        ++currentLine
                    }
                    if (text.x - previous.x > previous.width) {
                        ++currentSentence
                    }
                }
                if (pages.size == currentPage) {
                    pages.add(Page(index = currentPage, pageWidth = text.pageWidth, pageHeight = text.pageHeight))
                    currentLine = 0
                    currentSentence = 0
                }
                if (pages[currentPage].lines.size == currentLine) {
                    pages[currentPage].lines.add(Line(index = currentLine))
                    currentSentence = 0
                }
                if (pages[currentPage].lines[currentLine].sentences.size == currentSentence) {
                    pages[currentPage].lines[currentLine].sentences.add(Sentence(character = CharacterPosition.build(text, color)))
                } else {
                    val it = pages[currentPage].lines[currentLine].sentences[currentSentence]
                    pages[currentPage].lines[currentLine].sentences[currentSentence] = it.copy(characters = it.characters + CharacterPosition.build(text, color))
                }
                previous = text

            }
        }

        fun getPages(): List<Page> {
            return pages
        }
    }

    private data class Page(val lines: MutableList<Line> = mutableListOf(), val index: Int, val pageWidth: Float, val pageHeight: Float)
    private data class Line(val sentences: MutableList<Sentence> = mutableListOf(), val index: Int)
    private data class Sentence(val characters: List<CharacterPosition>) {
        constructor(character: CharacterPosition) : this(listOf(character))

        val text: String = characters.joinToString(separator = "") { it.unicode }
        val width: Float = characters.map { it.width }.sum()

        override fun toString(): String {
            return text
        }
    }

    private data class CharacterPosition(
        val textMatrix: Matrix,
        val x: Float, val y: Float,
        val endX: Float, val endY: Float,
        val width: Float, val height: Float,
        val unicode: String,
        val font: PDFont, val fontSize: Float,
        val color: Color
    ) {
        companion object {
            fun build(text: TextPosition, color: Color): CharacterPosition {
                return CharacterPosition(text.textMatrix, text.x, text.y, text.endX, text.endY, text.width, text.height, text.unicode, text.font, text.fontSize, color)
            }
        }
    }
}
